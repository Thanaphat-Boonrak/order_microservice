package com.thanaphat2005.food.ordering.system.payment.service.domain.exception;

import com.thanaphat2005.food.ordering.system.domain.exception.DomainException;

public class PaymentDomainException extends DomainException {
    public PaymentDomainException(String message, Throwable cause) {
        super(message, cause);
    }

    public PaymentDomainException(String message) {
        super(message);
    }
}
