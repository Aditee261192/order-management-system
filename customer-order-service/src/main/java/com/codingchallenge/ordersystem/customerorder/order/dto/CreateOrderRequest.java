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

    @NotNull(message = "Category is required")
    private String category;

    @NotNull(message = "Customer is required")
    @Valid
    private CustomerDto customer;

    @NotNull(message = "Site is required")
    @Valid
    private SiteDto site;

    @NotEmpty(message = "Order items must not be empty")
    @Valid
    private List<OrderItemDto> orderItems;

    @NotNull(message = "Payment method is required")
    @Valid
    private PaymentMethodDto paymentMethod;


}
