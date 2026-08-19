package com.thanaphat2005.food.ordering.system.order.service.dataaccess.order.entity.customer.mapper;


import com.thanaphat2005.food.ordering.system.order.service.dataaccess.order.entity.customer.entity.CustomerEntity;
import com.thanaphat2005.food.ordering.system.domain.valueobject.CustomerId;
import com.thanaphat2005.food.ordering.system.order.service.domain.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerDataAccessMapper {

    public Customer customerEntityToCustomer(CustomerEntity customerEntity){
        return new Customer(new CustomerId(customerEntity.getId()));
    }
}
