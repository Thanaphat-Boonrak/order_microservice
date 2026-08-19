package com.thanaphat2005.food.ordering.system.payment.service.domain.ports.output.message.publisher;

import com.thanaphat2005.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.thanaphat2005.food.ordering.system.payment.service.domain.event.PaymentCancelledEvent;

public interface PaymentCancelledMessagePublisher extends DomainEventPublisher<PaymentCancelledEvent> {
}
