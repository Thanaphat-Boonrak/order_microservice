package com.thanaphat2005.food.ordering.system.outbox;

public interface OutboxScheduler {
    void processOutboxMessage();
}
