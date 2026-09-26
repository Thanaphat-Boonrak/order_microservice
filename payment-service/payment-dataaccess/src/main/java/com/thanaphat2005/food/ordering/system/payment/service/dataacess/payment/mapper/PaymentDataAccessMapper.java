package com.thanaphat2005.food.ordering.system.payment.service.dataacess.payment.mapper;


import com.thanaphat2005.food.ordering.system.domain.valueobject.CustomerId;
import com.thanaphat2005.food.ordering.system.domain.valueobject.Money;
import com.thanaphat2005.food.ordering.system.domain.valueobject.OrderId;
import com.thanaphat2005.food.ordering.system.payment.service.dataacess.payment.entity.PaymentEntity;
import com.thanaphat2005.food.ordering.system.restaurant.service.domain.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentDataAccessMapper {


    public PaymentEntity paymentToPaymentEntity(Payment payment) {
        return PaymentEntity.builder().id(payment.getId().getValue())
                .customerId(payment.getCustomerId().getValue())
                .orderId(payment.getOrderId().getValue())
                .price(payment.getPrice().getAmount())
                .status(payment.getPaymentStatus())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    public Payment paymentEntityToPayment(PaymentEntity paymentEntity) {
        return Payment.builder().orderId(new OrderId(paymentEntity.getOrderId()))
                .customerId(new CustomerId(paymentEntity.getCustomerId()))
                .orderId(new OrderId(paymentEntity.getOrderId()))
                .price(new Money(paymentEntity.getPrice()))
                .createdAt(paymentEntity.getCreatedAt())
                .build();
    }
}
