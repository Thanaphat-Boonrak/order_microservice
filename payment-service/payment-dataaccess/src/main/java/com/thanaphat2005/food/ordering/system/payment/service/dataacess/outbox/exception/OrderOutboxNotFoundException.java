package com.thanaphat2005.food.ordering.system.payment.service.dataacess.outbox.exception;

public class OrderOutboxNotFoundException extends RuntimeException {

    public OrderOutboxNotFoundException(String message) {
        super(message);
    }
}
