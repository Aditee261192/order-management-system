package com.codingchallenge.ordersystem.customerorder.order.dto.request;

import com.codingchallenge.ordersystem.customerorder.order.dto.response.OrderResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class OrderListResponse {

    private List<OrderResponse> items;

    private long total;

    private int limit;

    private int offset;
}
