package com.thanaphat2005.food.ordering.system.order.service.dataaccess.order.entity.order.entity;


import com.thanaphat2005.food.ordering.system.domain.valueobject.OrderPreferences;
import com.thanaphat2005.food.ordering.system.domain.valueobject.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    private UUID id;

    private UUID customerId;
    private UUID restaurantId;
    private UUID trackingId;
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "order_preferences")
    private OrderPreferences orderPreferences;

    private String failureMessages;


    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private OrderAddressEntity address;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
    private List<OrderItemEntity> items;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderEntity that = (OrderEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
