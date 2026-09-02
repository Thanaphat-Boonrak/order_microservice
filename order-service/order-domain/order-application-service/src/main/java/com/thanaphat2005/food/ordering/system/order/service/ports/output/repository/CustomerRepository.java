package com.thanaphat2005.food.ordering.system.order.service.ports.output.repository;

import com.thanaphat2005.food.ordering.system.order.service.domain.entity.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {


    Optional<Customer> findCustomer(UUID customerId);


    Customer save(Customer customer);
}
