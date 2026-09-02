package com.thanaphat2005.food.ordering.system.customer.service.messaging.mapper;


import com.thanaphat2005.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;
import com.thanaphat2005.food.ordering.system.kafka.order.avro.model.CustomerAvroModel;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CustomerMessagingDataMapper {

    public CustomerAvroModel paymentResponseAvroModelToPaymentResponse(CustomerCreatedEvent
                                                                               customerCreatedEvent) {
        return CustomerAvroModel.newBuilder()
                .setId(UUID.fromString(customerCreatedEvent.getCustomer().getId().getValue().toString()))
                .setUsername(customerCreatedEvent.getCustomer().getUsername())
                .setFirstName(customerCreatedEvent.getCustomer().getFirstName())
                .setLastName(customerCreatedEvent.getCustomer().getLastName())
                .build();
    }
}
