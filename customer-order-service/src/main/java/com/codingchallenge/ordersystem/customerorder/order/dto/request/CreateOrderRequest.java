package com.codingchallenge.ordersystem.customerorder.order.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
