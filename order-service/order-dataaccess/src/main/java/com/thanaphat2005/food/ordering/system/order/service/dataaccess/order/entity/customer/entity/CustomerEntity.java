package com.thanaphat2005.food.ordering.system.order.service.dataaccess.order.entity.customer.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "customers")
@Entity
public class CustomerEntity {
    @Id
    private UUID id;

    private String username;
    private String firstName;
    private String lastName;

}
