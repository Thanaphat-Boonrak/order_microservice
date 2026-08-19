package com.thanaphat2005.food.ordering.system.payment.service.dataacess.creditentry.exception;

public class CreditEntryDataAccessException extends RuntimeException{
    public CreditEntryDataAccessException(String message) {
        super(message);
    }

    public CreditEntryDataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
