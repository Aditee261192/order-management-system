package com.codingchallenge.ordersystem.customerorder.order.exception;

public class ExistingOrderException extends RuntimeException {

    public ExistingOrderException(String message){
        super(message);
    }
}
