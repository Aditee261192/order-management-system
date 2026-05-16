package com.codingchallenge.ordersystem.customerorder.order.dto.request;

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
