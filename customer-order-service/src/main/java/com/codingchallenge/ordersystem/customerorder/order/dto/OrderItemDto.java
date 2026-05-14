package com.codingchallenge.ordersystem.customerorder.order.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDto {

    private String productOfferingId;

    private int quantity;

}
