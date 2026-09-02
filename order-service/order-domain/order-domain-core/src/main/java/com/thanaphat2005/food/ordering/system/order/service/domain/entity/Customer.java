package com.thanaphat2005.food.ordering.system.order.service.domain.entity;

import com.thanaphat2005.food.ordering.system.domain.entity.AggregateRoot;
import com.thanaphat2005.food.ordering.system.domain.valueobject.CustomerId;

public class Customer extends AggregateRoot<CustomerId> {

    private  String username;
    private  String firstName;
    private  String lastName;

    public Customer(CustomerId customerId, String username, String firstName, String lastName) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        super.setId(customerId);
    }

    public Customer(CustomerId customerId) {
        super.setId(customerId);
    }



    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
