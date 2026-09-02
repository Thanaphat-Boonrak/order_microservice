package com.thanaphat2005.food.ordering.system.customer.service.domain;


import com.thanaphat2005.food.ordering.system.customer.service.domain.entity.Customer;
import com.thanaphat2005.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;

public interface CustomerDomainService {

    CustomerCreatedEvent validateAndInitiateCustomer(Customer customer);

}
