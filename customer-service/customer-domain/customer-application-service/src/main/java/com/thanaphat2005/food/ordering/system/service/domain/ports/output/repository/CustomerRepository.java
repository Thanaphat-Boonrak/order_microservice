package com.thanaphat2005.food.ordering.system.service.domain.ports.output.repository;


import com.thanaphat2005.food.ordering.system.customer.service.domain.entity.Customer;

public interface CustomerRepository {

    Customer createCustomer(Customer customer);
}
