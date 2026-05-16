package com.codingchallenge.ordersystem.customerorder.order.service;

import com.codingchallenge.ordersystem.customerorder.order.dao.OrderIdempotencyRepository;
import com.codingchallenge.ordersystem.customerorder.order.dao.OrderRepository;
import com.codingchallenge.ordersystem.customerorder.order.dto.CreateOrderRequest;
import com.codingchallenge.ordersystem.customerorder.order.dto.OrderItemDto;
import com.codingchallenge.ordersystem.customerorder.order.dto.OrderResponse;
import com.codingchallenge.ordersystem.customerorder.order.dto.PaymentMethodDto;
import com.codingchallenge.ordersystem.customerorder.order.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.DuplicateFormatFlagsException;
import java.util.List;
import java.util.Optional;

@Service
public class DefaultOrderService implements OrderService {

    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final OrderIdempotencyRepository idempotencyRepository;

    @Autowired
    public DefaultOrderService(OrderRepository orderRepository,
                               ModelMapper modelMapper,
                               OrderIdempotencyRepository idempotencyRepository) {
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
        this.idempotencyRepository = idempotencyRepository;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, String key) {

        if (key != null) {

            Optional<OrderIdempotency> existing =
                    idempotencyRepository.findByIdempotencyKey(key);

            if (existing.isPresent()) {

                OrderIdempotency stored = existing.get();

                if (isSameRequest(request, stored)) {

                    Order order = orderRepository.findById(stored.getOrderId())
                            .orElseThrow();

                    return modelMapper.map(order, OrderResponse.class);
                }

                throw
                        new DuplicateFormatFlagsException("A request with the same Idempotency-Key has already been processed.");
            }
        }

        Order order = buildOrder(request);
        order = orderRepository.save(order);

        if (key != null) {
            OrderIdempotency entry = new OrderIdempotency();
            entry.setIdempotencyKey(key);
            entry.setRequestHash(generateHash(request));
            entry.setOrderId(order.getId());
            idempotencyRepository.save(entry);
        }

        return modelMapper.map(order, OrderResponse.class);
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
