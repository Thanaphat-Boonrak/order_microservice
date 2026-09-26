package com.thanaphat2005.food.ordering.system.service.domain.ports.output.message.publisher;


import com.thanaphat2005.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;

public interface CustomerMessagePublisher {

    void publish(CustomerCreatedEvent customerCreatedEvent);

}