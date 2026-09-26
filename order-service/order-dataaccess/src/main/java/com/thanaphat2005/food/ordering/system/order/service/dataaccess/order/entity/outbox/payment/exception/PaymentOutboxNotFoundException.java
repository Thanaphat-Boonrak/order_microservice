package com.thanaphat2005.food.ordering.system.order.service.dataaccess.order.entity.outbox.payment.exception;

public class PaymentOutboxNotFoundException extends RuntimeException {

    public PaymentOutboxNotFoundException(String message) {
        super(message);
    }
}
