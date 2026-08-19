package com.thanaphat2005.food.ordering.system.order.service.dataaccess.order.entity.customer.repository;


import com.thanaphat2005.food.ordering.system.order.service.dataaccess.order.entity.customer.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, UUID> {
}
