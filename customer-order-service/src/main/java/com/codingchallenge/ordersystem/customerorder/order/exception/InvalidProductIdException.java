package com.codingchallenge.ordersystem.customerorder.order.exception;

public class InvalidProductIdException extends RuntimeException{
    public InvalidProductIdException(String message){
        super(message);
    }
}
