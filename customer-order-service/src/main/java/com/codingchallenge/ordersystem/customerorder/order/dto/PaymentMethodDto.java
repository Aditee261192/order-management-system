package com.codingchallenge.ordersystem.customerorder.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentMethodDto {

    @NotNull
    private String paymentType;

    private String iban;

}
