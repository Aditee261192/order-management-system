package com.codingchallenge.ordersystem.customerorder.order.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDto {

    @NotNull
    private String customerId;
}
