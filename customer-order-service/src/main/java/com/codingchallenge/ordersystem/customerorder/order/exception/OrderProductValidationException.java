package com.codingchallenge.ordersystem.customerorder.order.exception;

public class OrderProductValidationException extends RuntimeException{

    public OrderProductValidationException(String message){
        super(message);
    }
}
