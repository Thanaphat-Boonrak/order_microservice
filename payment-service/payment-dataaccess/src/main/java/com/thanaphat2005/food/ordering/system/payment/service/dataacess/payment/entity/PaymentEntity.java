package com.thanaphat2005.food.ordering.system.payment.service.dataacess.payment.entity;


import com.thanaphat2005.food.ordering.system.domain.valueobject.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
@Entity
public class PaymentEntity {

    @Id
    private UUID id;
    private UUID orderId;
    private UUID customerId;
    private BigDecimal price;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private ZonedDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PaymentEntity that = (PaymentEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
