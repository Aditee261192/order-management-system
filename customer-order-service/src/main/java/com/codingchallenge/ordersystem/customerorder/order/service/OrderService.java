package com.codingchallenge.ordersystem.customerorder.order.service;

import com.codingchallenge.ordersystem.customerorder.order.dto.request.CreateOrderRequest;
import com.codingchallenge.ordersystem.customerorder.order.dto.request.OrderListResponse;
import com.codingchallenge.ordersystem.customerorder.order.dto.response.OrderResponse;
import com.fasterxml.jackson.databind.JsonNode;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request, String idempotencyKey);

    OrderResponse getOrderById(String orderID);

    OrderListResponse listOrders(int limit, int offset, String category);

    OrderResponse patchOrder(String orderId, JsonNode patch);
}
