package com.codingchallenge.ordersystem.customerorder.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDto {

    @NotNull
    private String productOfferingId;

    private int quantity;

}
