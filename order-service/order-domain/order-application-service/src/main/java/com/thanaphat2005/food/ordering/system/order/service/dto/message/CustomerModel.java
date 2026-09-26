package com.thanaphat2005.food.ordering.system.order.service.dto.message;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CustomerModel {

    private String id;
    private String username;
    private String firstName;
    private String lastName;
}
