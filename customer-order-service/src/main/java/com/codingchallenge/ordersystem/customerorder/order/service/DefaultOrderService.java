package com.codingchallenge.ordersystem.customerorder.order.service;

import com.codingchallenge.ordersystem.customerorder.order.dao.OrderIdempotencyRepository;
import com.codingchallenge.ordersystem.customerorder.order.dao.OrderRepository;
import com.codingchallenge.ordersystem.customerorder.order.dto.request.CreateOrderRequest;
import com.codingchallenge.ordersystem.customerorder.order.dto.request.OrderItemDto;
import com.codingchallenge.ordersystem.customerorder.order.dto.request.OrderListResponse;
import com.codingchallenge.ordersystem.customerorder.order.dto.request.PaymentMethodDto;
import com.codingchallenge.ordersystem.customerorder.order.dto.response.OrderResponse;
import com.codingchallenge.ordersystem.customerorder.order.entity.*;
import com.codingchallenge.ordersystem.customerorder.order.exception.ExistingOrderException;
import com.codingchallenge.ordersystem.customerorder.order.exception.OrderNotFoundException;
import com.codingchallenge.ordersystem.customerorder.order.external.api.productcatalog.exception.OrderProductValidationException;
import com.codingchallenge.ordersystem.customerorder.order.external.api.productcatalog.service.ProductCatalogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.DuplicateFormatFlagsException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class DefaultOrderService implements OrderService {

    private final ProductCatalogService productCatalogService;
    private final OrderRepository orderRepository;
    private final OrderIdempotencyRepository idempotencyRepository;
    private final ModelMapper modelMapper;
    private final Executor executor;

    @Autowired
    public DefaultOrderService(OrderRepository orderRepository,
                               ModelMapper modelMapper,
                               OrderIdempotencyRepository idempotencyRepository,
                               ProductCatalogService productCatalogService,
                               Executor executor) {
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
        this.idempotencyRepository = idempotencyRepository;
        this.productCatalogService = productCatalogService;
        this.executor = executor;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, String key) {

        handleIdempotency(request, key);

        validateProducts(request.getOrderItems());

        Order order = buildOrder(request);

        order = orderRepository.save(order);

        saveIdempotencyIfRequired(request, key, order);

        return modelMapper.map(order, OrderResponse.class);
    }

    @Override
    public OrderResponse getOrderById(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException("Order not found: " + orderId)
                );

        return modelMapper.map(order, OrderResponse.class);
    }

    @Override
    public OrderListResponse listOrders(int limit, int offset, String category) {
        int page = offset / limit;

        Pageable pageable = PageRequest.of(page, limit);

        Page<Order> result;

        if (category != null) {
            Category categoryEnum = category != null ? Category.valueOf(category) : null;
            result = orderRepository.findByCategory(
                    categoryEnum,
                    pageable
            );
        } else {
            result = orderRepository.findAll(pageable);
        }

        List<OrderResponse> items = result.getContent()
                .stream()
                .map(order -> modelMapper.map(order, OrderResponse.class))
                .toList();

        return OrderListResponse.builder()
                .items(items)
                .total(result.getTotalElements())
                .limit(limit)
                .offset(offset)
                .build();
    }


    private Order buildOrder(CreateOrderRequest request) {

        Order order = Order.builder()
                .category(Category.valueOf(request.getCategory()))
                .customerId(request.getCustomer().getCustomerId())
                .siteId(request.getSite().getSiteId())
                .state(State.DRAFT)
                .build();

        order.setOrderItems(mapOrderItems(request.getOrderItems(), order));
        order.setPaymentMethod(mapPaymentMethod(request.getPaymentMethod(), order));

        return order;
    }

    private List<OrderItem> mapOrderItems(List<OrderItemDto> itemDtos, Order order) {
        return itemDtos.stream()
                .map(item -> OrderItem.builder()
                        .productOfferingId(item.getProductOfferingId())
                        .quantity(item.getQuantity())
                        .order(order)
                        .build()
                )
                .toList();
    }

    private PaymentMethod mapPaymentMethod(PaymentMethodDto dto, Order order) {
        if (dto == null) return null;

        return PaymentMethod.builder()
                .type(dto.getPaymentType())
                .order(order)
                .build();
    }

    private void validateProducts(List<OrderItemDto> items) {

        List<CompletableFuture<Boolean>> futures = items.stream()
                .map(item -> CompletableFuture.supplyAsync(() ->
                        productCatalogService.productOfferingExists(
                                item.getProductOfferingId()
                        ), executor)
                )
                .toList();

        for (CompletableFuture<Boolean> future : futures) {
            try {
                Boolean isValid = future.get(3, TimeUnit.SECONDS);

                if (!isValid) {
                    throw new OrderProductValidationException("One or more products are invalid");
                }

            } catch (TimeoutException ex) {
                throw new OrderProductValidationException("Product validation timed out", ex);

            } catch (Exception ex) {
                throw new OrderProductValidationException("Error while validating product", ex);
            }
        }
    }

    private void handleIdempotency(CreateOrderRequest request, String key) {

        if (key == null) return;

        Optional<OrderIdempotency> existing =
                idempotencyRepository.findByIdempotencyKey(key);

        if (existing.isEmpty()) return;

        OrderIdempotency stored = existing.get();

        if (!isSameRequest(request, stored)) {
            throw new DuplicateFormatFlagsException(
                    "A request with the same Idempotency-Key has already been processed."
            );
        }

        Order order = orderRepository.findById(stored.getOrderId())
                .orElseThrow();

        throw new ExistingOrderException("Order already exists for this idempotency key.");
    }

    private void saveIdempotencyIfRequired(CreateOrderRequest request,
                                           String key,
                                           Order order) {

        if (key == null) return;

        OrderIdempotency entry = new OrderIdempotency();
        entry.setIdempotencyKey(key);
        entry.setRequestHash(generateHash(request));
        entry.setOrderId(order.getId());

        idempotencyRepository.save(entry);
    }

    private boolean isSameRequest(CreateOrderRequest request,
                                  OrderIdempotency stored) {

        return stored.getRequestHash()
                .equals(generateHash(request));
    }


    private String generateHash(CreateOrderRequest request) {

        try {
            ObjectMapper mapper = new ObjectMapper();

            String json = mapper.writeValueAsString(request);

            return DigestUtils.sha256Hex(json);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate request hash", e);
        }
    }
}
