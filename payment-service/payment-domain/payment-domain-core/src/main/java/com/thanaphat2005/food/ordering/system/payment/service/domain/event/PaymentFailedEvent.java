package com.thanaphat2005.food.ordering.system.payment.service.domain.event;

import com.thanaphat2005.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.thanaphat2005.food.ordering.system.payment.service.domain.entity.Payment;

import java.time.ZonedDateTime;
import java.util.List;

public class PaymentFailedEvent extends PaymentEvent {
    private final DomainEventPublisher<PaymentFailedEvent> paymentCompletedEventDomainEventPublisher;

    public PaymentFailedEvent(Payment payment, ZonedDateTime createdAt, List<String> failureMessages, DomainEventPublisher<PaymentFailedEvent> paymentCompletedEventDomainEventPublisher) {
        super(payment, createdAt, failureMessages);
        this.paymentCompletedEventDomainEventPublisher = paymentCompletedEventDomainEventPublisher;
    }

    @Override
    public void fire() {
        paymentCompletedEventDomainEventPublisher.publish(this);
    }
}
