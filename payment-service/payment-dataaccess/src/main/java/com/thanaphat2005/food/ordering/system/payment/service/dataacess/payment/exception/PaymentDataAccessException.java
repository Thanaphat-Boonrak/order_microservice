package com.thanaphat2005.food.ordering.system.payment.service.dataacess.payment.exception;

public class PaymentDataAccessException extends RuntimeException{
    public PaymentDataAccessException(String message) {
        super(message);
    }

    public PaymentDataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
