package com.thanaphat2005.food.ordering.system.order.service;

import com.thanaphat2005.food.ordering.system.order.service.domain.entity.Customer;
import com.thanaphat2005.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.thanaphat2005.food.ordering.system.order.service.dto.message.CustomerModel;
import com.thanaphat2005.food.ordering.system.order.service.mapper.OrderDataMapper;
import com.thanaphat2005.food.ordering.system.order.service.ports.input.message.listener.customer.CustomerMessageListener;
import com.thanaphat2005.food.ordering.system.order.service.ports.output.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class CustomerMessageListenerImpl implements CustomerMessageListener {

    private final CustomerRepository customerRepository;
    private final OrderDataMapper orderDataMapper;

    public CustomerMessageListenerImpl(CustomerRepository customerRepository, OrderDataMapper orderDataMapper) {
        this.customerRepository = customerRepository;
        this.orderDataMapper = orderDataMapper;
    }

    @Override
    public void customerCreated(CustomerModel customerModel) {

        Customer customer = customerRepository.save(orderDataMapper.customerModelToCustomer(customerModel));
        if (customer == null) {
            log.error("Customer could not be created in order database with id: {}", customerModel.getId());
            throw new OrderDomainException("Customer could not be created in order database with id " +
                    customerModel.getId());
        }
        log.info("Customer is created in order database with id: {}", customer.getId());
    }
}
