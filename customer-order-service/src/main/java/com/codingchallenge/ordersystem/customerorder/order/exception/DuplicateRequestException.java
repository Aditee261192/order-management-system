package com.codingchallenge.ordersystem.customerorder.order.exception;

public class DuplicateRequestException extends RuntimeException{

    public DuplicateRequestException(String meesage){
        super(meesage);
    }
}
