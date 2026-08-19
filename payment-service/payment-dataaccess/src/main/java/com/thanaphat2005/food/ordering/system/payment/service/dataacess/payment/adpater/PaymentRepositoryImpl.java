package com.thanaphat2005.food.ordering.system.payment.service.dataacess.payment.adpater;

import com.thanaphat2005.food.ordering.system.payment.service.dataacess.payment.mapper.PaymentDataAccessMapper;
import com.thanaphat2005.food.ordering.system.payment.service.dataacess.payment.repository.PaymentJpaRepository;
import com.thanaphat2005.food.ordering.system.payment.service.domain.entity.Payment;
import com.thanaphat2005.food.ordering.system.payment.service.domain.ports.output.repository.PaymentRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;


@Component
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;
    private final PaymentDataAccessMapper paymentDataAccessMapper;

    public PaymentRepositoryImpl(PaymentJpaRepository paymentJpaRepository, PaymentDataAccessMapper paymentDataAccessMapper) {
        this.paymentJpaRepository = paymentJpaRepository;
        this.paymentDataAccessMapper = paymentDataAccessMapper;
    }

    @Override
    public Payment save(Payment payment) {
        return paymentDataAccessMapper.paymentEntityToPayment(paymentJpaRepository.save(paymentDataAccessMapper.paymentToPaymentEntity(payment)));
    }

    @Override
    public Optional<Payment> findByOrderId(UUID orderId) {
        return paymentJpaRepository.findByOrderId(orderId).map(paymentDataAccessMapper::paymentEntityToPayment);
    }
}
