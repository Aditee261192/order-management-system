package com.codingchallenge.ordersystem.customerorder.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class CreateOrderRequest {

    @NotNull
    private String category;

    @NotNull
    @Valid
    private CustomerDto customer;

    @NotNull
    @Valid
    private SiteDto site;

    @NotEmpty
    @Valid
    private List<OrderItemDto> orderItems;

    @NotNull
    @Valid
    private PaymentMethodDto paymentMethod;


}
