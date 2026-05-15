package com.codingchallenge.ordersystem.customerorder.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Setter
public class OrderResponse {

    private String id;

    private String state;

    private String category;

    private CustomerDto customer;

    private SiteDto site;

    private List<OrderItemDto> orderItems;

    private PaymentMethodDto paymentMethod;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}