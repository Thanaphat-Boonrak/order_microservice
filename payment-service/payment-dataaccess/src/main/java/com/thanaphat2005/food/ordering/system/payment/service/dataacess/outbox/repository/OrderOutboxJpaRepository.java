package com.thanaphat2005.food.ordering.system.payment.service.dataacess.outbox.repository;

import com.thanaphat2005.food.ordering.system.domain.valueobject.PaymentStatus;
import com.thanaphat2005.food.ordering.system.outbox.OutboxStatus;
import com.thanaphat2005.food.ordering.system.payment.service.dataacess.outbox.entity.OrderOutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderOutboxJpaRepository extends JpaRepository<OrderOutboxEntity, UUID> {

    Optional<List<OrderOutboxEntity>> findByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);

    Optional<OrderOutboxEntity> findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(String type,
                                                                    UUID sagaId,
                                                                    PaymentStatus paymentStatus,
                                                                    OutboxStatus outboxStatus);

    void deleteByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);

}
