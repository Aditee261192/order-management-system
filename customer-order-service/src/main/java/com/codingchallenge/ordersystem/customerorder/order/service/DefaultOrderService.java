package com.codingchallenge.ordersystem.customerorder.order.service;

import com.codingchallenge.ordersystem.customerorder.order.dao.OrderRepository;
import com.codingchallenge.ordersystem.customerorder.order.dto.CreateOrderRequest;
import com.codingchallenge.ordersystem.customerorder.order.dto.OrderItemDto;
import com.codingchallenge.ordersystem.customerorder.order.dto.OrderResponse;
import com.codingchallenge.ordersystem.customerorder.order.dto.PaymentMethodDto;
import com.codingchallenge.ordersystem.customerorder.order.entity.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DefaultOrderService implements OrderService {

    private OrderRepository orderRepository;
    private ModelMapper modelMapper;

    @Autowired
    public DefaultOrderService(OrderRepository orderRepository, ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest createOrderRequest, String idempotencyKey) {

        Order order = orderRepository.save(buildOrder(createOrderRequest));

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


}
