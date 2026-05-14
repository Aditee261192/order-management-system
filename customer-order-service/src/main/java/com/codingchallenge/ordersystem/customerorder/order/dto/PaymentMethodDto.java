package com.codingchallenge.ordersystem.customerorder.order.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentMethodDto {

    private String paymentType;

    private String iban;

}
