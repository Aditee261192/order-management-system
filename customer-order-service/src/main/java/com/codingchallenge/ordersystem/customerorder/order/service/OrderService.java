package com.codingchallenge.ordersystem.customerorder.order.service;

import com.codingchallenge.ordersystem.customerorder.order.dto.request.CreateOrderRequest;
import com.codingchallenge.ordersystem.customerorder.order.dto.response.OrderResponse;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request, String idempotencyKey);

    OrderResponse getOrderById(String orderID);
}
