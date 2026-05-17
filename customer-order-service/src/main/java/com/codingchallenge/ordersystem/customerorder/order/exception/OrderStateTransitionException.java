package com.codingchallenge.ordersystem.customerorder.order.exception;

public class OrderStateTransitionException extends RuntimeException {

    public OrderStateTransitionException(String message){
        super(message);
    }
}
