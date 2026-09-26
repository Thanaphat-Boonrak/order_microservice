package com.thanaphat2005.food.ordering.system.service.domain;



import com.thanaphat2005.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;
import com.thanaphat2005.food.ordering.system.service.domain.create.CreateCustomerCommand;
import com.thanaphat2005.food.ordering.system.service.domain.create.CreateCustomerResponse;
import com.thanaphat2005.food.ordering.system.service.domain.mapper.CustomerDataMapper;
import com.thanaphat2005.food.ordering.system.service.domain.ports.input.service.CustomerApplicationService;
import com.thanaphat2005.food.ordering.system.service.domain.ports.output.message.publisher.CustomerMessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
class CustomerApplicationServiceImpl implements CustomerApplicationService {

    private final CustomerCreateCommandHandler customerCreateCommandHandler;

    private final CustomerDataMapper customerDataMapper;

    private final CustomerMessagePublisher customerMessagePublisher;

    public CustomerApplicationServiceImpl(CustomerCreateCommandHandler customerCreateCommandHandler,
                                          CustomerDataMapper customerDataMapper,
                                          CustomerMessagePublisher customerMessagePublisher) {
        this.customerCreateCommandHandler = customerCreateCommandHandler;
        this.customerDataMapper = customerDataMapper;
        this.customerMessagePublisher = customerMessagePublisher;
    }

    @Override
    public CreateCustomerResponse createCustomer(CreateCustomerCommand createCustomerCommand) {
        CustomerCreatedEvent customerCreatedEvent = customerCreateCommandHandler.createCustomer(createCustomerCommand);
        customerMessagePublisher.publish(customerCreatedEvent);
        return customerDataMapper
                .customerToCreateCustomerResponse(customerCreatedEvent.getCustomer(),
                        "Customer saved successfully!");
    }
}
