package com.thanaphat2005.food.ordering.system.restaurant.service.domain.exception;

import com.thanaphat2005.food.ordering.system.domain.exception.DomainException;

public class PaymentNotFoundException extends DomainException {
    public PaymentNotFoundException(String message) {
        super(message);
    }

    public PaymentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
