package com.thanaphat2005.food.ordering.system.order.service.dataaccess.order.entity.customer.adpater;

import com.thanaphat2005.food.ordering.system.order.service.dataaccess.order.entity.customer.mapper.CustomerDataAccessMapper;
import com.thanaphat2005.food.ordering.system.order.service.dataaccess.order.entity.customer.repository.CustomerJpaRepository;
import com.thanaphat2005.food.ordering.system.order.service.domain.entity.Customer;
import com.thanaphat2005.food.ordering.system.order.service.ports.output.repository.CustomerRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;


@Component
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerJpaRepository customerJpaRepository;
    private final CustomerDataAccessMapper customerDataAccessMapper;
    public CustomerRepositoryImpl(CustomerJpaRepository customerJpaRepository, CustomerDataAccessMapper customerDataAccessMapper) {
        this.customerJpaRepository = customerJpaRepository;
        this.customerDataAccessMapper = customerDataAccessMapper;
    }

    @Override
    public Optional<Customer> findCustomer(UUID customerId) {
        return customerJpaRepository.findById(customerId).map(customerDataAccessMapper::customerEntityToCustomer);
    }
}
