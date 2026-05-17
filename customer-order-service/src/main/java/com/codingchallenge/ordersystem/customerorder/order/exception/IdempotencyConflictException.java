package com.codingchallenge.ordersystem.customerorder.order.exception;

public class IdempotencyConflictException extends RuntimeException{

    public IdempotencyConflictException(String message){
        super(message);
    }
}
