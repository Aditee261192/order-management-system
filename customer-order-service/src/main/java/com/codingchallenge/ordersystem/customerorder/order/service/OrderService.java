package com.codingchallenge.ordersystem.customerorder.order.service;

import com.codingchallenge.ordersystem.customerorder.order.dto.CreateOrderRequest;
import com.codingchallenge.ordersystem.customerorder.order.dto.OrderResponse;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request, String idempotencyKey);
}
