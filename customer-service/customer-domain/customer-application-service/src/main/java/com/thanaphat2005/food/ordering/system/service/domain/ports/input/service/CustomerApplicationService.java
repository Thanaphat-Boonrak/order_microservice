package com.thanaphat2005.food.ordering.system.service.domain.ports.input.service;


import com.thanaphat2005.food.ordering.system.service.domain.create.CreateCustomerCommand;
import com.thanaphat2005.food.ordering.system.service.domain.create.CreateCustomerResponse;
import jakarta.validation.Valid;

public interface CustomerApplicationService {

    CreateCustomerResponse createCustomer(@Valid CreateCustomerCommand createCustomerCommand);

}
