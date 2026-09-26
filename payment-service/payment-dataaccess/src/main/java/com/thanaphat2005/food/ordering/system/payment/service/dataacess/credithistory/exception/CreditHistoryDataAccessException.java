package com.thanaphat2005.food.ordering.system.payment.service.dataacess.credithistory.exception;

public class CreditHistoryDataAccessException extends RuntimeException{
    public CreditHistoryDataAccessException(String message) {
        super(message);
    }

    public CreditHistoryDataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
