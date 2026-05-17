package com.codingchallenge.ordersystem.customerorder.order.service;

import com.codingchallenge.ordersystem.customerorder.order.dao.OrderRepository;
import com.codingchallenge.ordersystem.customerorder.order.dto.request.CreateOrderRequest;
import com.codingchallenge.ordersystem.customerorder.order.dto.request.OrderListResponse;
import com.codingchallenge.ordersystem.customerorder.order.dto.response.OrderResponse;
import com.codingchallenge.ordersystem.customerorder.order.entity.Category;
import com.codingchallenge.ordersystem.customerorder.order.entity.Order;
import com.codingchallenge.ordersystem.customerorder.order.entity.State;
import com.codingchallenge.ordersystem.customerorder.order.exception.OrderNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DefaultOrderService implements OrderService {

    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    private final OrderValidator orderValidator;
    private final OrderStateMachine orderStateMachine;
    private final OrderItemService orderItemService;
    private final IdempotencyService idempotencyService;
    private final ProductValidationService productValidationService;


    public DefaultOrderService(
            OrderRepository orderRepository,
            ModelMapper modelMapper,
            OrderValidator orderValidator,
            OrderStateMachine orderStateMachine,
            OrderItemService orderItemService,
            IdempotencyService idempotencyService,
            ProductValidationService productValidationService,
            ProductCatalogClient productCatalogClient
    ) {
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
        this.orderValidator = orderValidator;
        this.orderStateMachine = orderStateMachine;
        this.orderItemService = orderItemService;
        this.idempotencyService = idempotencyService;
        this.productValidationService = productValidationService;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, String key) {

        orderValidator.validateCreateRequest(request);
        idempotencyService.checkCreateRequest(request, key);
        productValidationService.validateProducts(request.getOrderItems());

        Order order = Order.builder()
                .category(Category.valueOf(request.getCategory()))
                .customerId(request.getCustomer().getCustomerId())
                .siteId(request.getSite().getSiteId())
                .state(State.DRAFT)
                .build();

        order.setOrderItems(
                orderItemService.buildItems(request.getOrderItems(), order)
        );

        order = orderRepository.save(order);

        idempotencyService.saveIfRequired(request, key, order);

        return modelMapper.map(order, OrderResponse.class);
    }

    @Override
    public OrderResponse patchOrder(String orderId, JsonNode patch) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        orderValidator.validatePatchRequest(order, patch);

        if (patch.has("state")) {
            orderStateMachine.transition(order, patch.get("state").asText());
        }

        if (patch.has("category")) {
            orderValidator.validateCategoryUpdate(patch.get("category"));
            order.setCategory(Category.valueOf(patch.get("category").asText()));
        }

        if (patch.has("orderItems")) {
            order.setOrderItems(
                    orderItemService.replaceItems(patch.get("orderItems"), order)
            );
        }

        order = orderRepository.save(order);

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

        Pageable pageable = PageRequest.of(offset / limit, limit);

        Page<Order> result = (category != null)
                ? orderRepository.findByCategory(Category.valueOf(category), pageable)
                : orderRepository.findAll(pageable);

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
}