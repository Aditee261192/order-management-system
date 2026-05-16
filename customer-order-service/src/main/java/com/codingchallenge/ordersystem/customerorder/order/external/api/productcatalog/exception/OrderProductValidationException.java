package com.codingchallenge.ordersystem.customerorder.order.external.api.productcatalog.exception;

public class OrderProductValidationException extends RuntimeException{

    public OrderProductValidationException(String message) {
        super(message);
    }

    public OrderProductValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
