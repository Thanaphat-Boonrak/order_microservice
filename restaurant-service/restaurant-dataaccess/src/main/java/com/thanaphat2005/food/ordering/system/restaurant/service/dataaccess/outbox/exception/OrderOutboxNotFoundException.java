package com.thanaphat2005.food.ordering.system.restaurant.service.dataaccess.outbox.exception;

public class OrderOutboxNotFoundException extends RuntimeException {

    public OrderOutboxNotFoundException(String message) {
        super(message);
    }
}
