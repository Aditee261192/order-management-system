package com.codingchallenge.ordersystem.customerorder.order.exception;

public class OrderNotFoundException extends RuntimeException{

    public OrderNotFoundException(String message){
        super(message);
    }
}
