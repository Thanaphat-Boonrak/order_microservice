package com.thanaphat2005.food.ordering.system.payment.service.domain.exception;

import com.thanaphat2005.food.ordering.system.domain.exception.DomainException;

public class RestaurantDomainException extends DomainException {
    public RestaurantDomainException(String message) {
        super(message);
    }

    public RestaurantDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
