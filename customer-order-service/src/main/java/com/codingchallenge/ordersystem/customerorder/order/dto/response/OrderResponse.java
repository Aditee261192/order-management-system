package com.codingchallenge.ordersystem.customerorder.order.dto.response;

import com.codingchallenge.ordersystem.customerorder.order.dto.request.CustomerDto;
import com.codingchallenge.ordersystem.customerorder.order.dto.request.OrderItemDto;
import com.codingchallenge.ordersystem.customerorder.order.dto.request.PaymentMethodDto;
import com.codingchallenge.ordersystem.customerorder.order.dto.request.SiteDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
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