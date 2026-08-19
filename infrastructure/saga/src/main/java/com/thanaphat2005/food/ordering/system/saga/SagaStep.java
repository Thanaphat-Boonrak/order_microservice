package com.thanaphat2005.food.ordering.system.saga;

import com.thanaphat2005.food.ordering.system.domain.event.DomainEvent;

public interface SagaStep<T, S extends DomainEvent,U extends DomainEvent> {
    S process(T data);
    U rollback(T data);
}
