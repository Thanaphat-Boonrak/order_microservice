package com.thanaphat2005.food.ordering.system.payment.service.dataacess.creditentry.repository;


import com.thanaphat2005.food.ordering.system.payment.service.dataacess.creditentry.entity.CreditEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CreditEntryJpaRepository extends JpaRepository<CreditEntryEntity, UUID> {

    Optional<CreditEntryEntity> findByCustomerId(UUID customerId);

}
