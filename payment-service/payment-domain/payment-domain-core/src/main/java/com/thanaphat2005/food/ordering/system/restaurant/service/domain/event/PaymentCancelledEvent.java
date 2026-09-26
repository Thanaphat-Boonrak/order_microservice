package com.thanaphat2005.food.ordering.system.restaurant.service.domain.event;

import com.thanaphat2005.food.ordering.system.restaurant.service.domain.entity.Payment;

import java.time.ZonedDateTime;
import java.util.Collections;

public class PaymentCancelledEvent extends PaymentEvent {


    public PaymentCancelledEvent(Payment payment, ZonedDateTime createdAt) {
        super(payment, createdAt, Collections.emptyList());
    }


}
