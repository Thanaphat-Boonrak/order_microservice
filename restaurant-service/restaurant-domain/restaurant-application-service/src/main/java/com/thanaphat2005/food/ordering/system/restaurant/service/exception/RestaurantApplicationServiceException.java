package com.thanaphat2005.food.ordering.system.restaurant.service.exception;

import com.thanaphat2005.food.ordering.system.domain.exception.DomainException;

public class RestaurantApplicationServiceException extends DomainException {
    public RestaurantApplicationServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestaurantApplicationServiceException(String message) {
        super(message);
    }
}
