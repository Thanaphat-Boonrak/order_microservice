package com.thanaphat2005.food.ordering.system.payment.service.domain;


import com.thanaphat2005.food.ordering.system.domain.valueobject.CustomerId;
import com.thanaphat2005.food.ordering.system.domain.valueobject.PaymentStatus;
import com.thanaphat2005.food.ordering.system.outbox.OutboxStatus;
import com.thanaphat2005.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.thanaphat2005.food.ordering.system.restaurant.service.domain.entity.CreditEntry;
import com.thanaphat2005.food.ordering.system.restaurant.service.domain.entity.CreditHistory;
import com.thanaphat2005.food.ordering.system.restaurant.service.domain.entity.Payment;
import com.thanaphat2005.food.ordering.system.restaurant.service.domain.event.PaymentEvent;
import com.thanaphat2005.food.ordering.system.payment.service.domain.exception.PaymentApplicationServiceException;
import com.thanaphat2005.food.ordering.system.payment.service.domain.mapper.PaymentDataMapper;
import com.thanaphat2005.food.ordering.system.payment.service.domain.outbox.model.OrderOutboxMessage;
import com.thanaphat2005.food.ordering.system.payment.service.domain.outbox.scheduler.OrderOutboxHelper;
import com.thanaphat2005.food.ordering.system.payment.service.domain.ports.output.repository.CreditEntryRepository;
import com.thanaphat2005.food.ordering.system.payment.service.domain.ports.output.repository.CreditHistoryRepository;
import com.thanaphat2005.food.ordering.system.payment.service.domain.ports.output.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class PaymentRequestHelper {

    private final PaymentDomainService paymentDomainService;
    private final PaymentDataMapper paymentDataMapper;
    private final PaymentRepository paymentRepository;
    private final CreditEntryRepository creditEntryRepository;
    private final CreditHistoryRepository creditHistoryRepository;
    private final OrderOutboxHelper orderOutboxHelper;


    public PaymentRequestHelper(PaymentDomainService paymentDomainService,
                                PaymentDataMapper paymentDataMapper,
                                PaymentRepository paymentRepository,
                                CreditEntryRepository creditEntryRepository,
                                CreditHistoryRepository creditHistoryRepository, OrderOutboxHelper orderOutboxHelper) {
        this.paymentDomainService = paymentDomainService;
        this.paymentDataMapper = paymentDataMapper;
        this.paymentRepository = paymentRepository;
        this.creditEntryRepository = creditEntryRepository;
        this.creditHistoryRepository = creditHistoryRepository;
        this.orderOutboxHelper = orderOutboxHelper;
    }

    @Transactional
    public void persistPayment(PaymentRequest paymentRequest) {
        log.info("Received payment complete event for order id: {}", paymentRequest.getOrderId());

        if (isOutboxMessageProcessedForPayment(paymentRequest, PaymentStatus.COMPLETED)) {
            log.info("An outbox message has been sent for order id: {}", paymentRequest.getOrderId());
            return;
        }

        Payment payment = paymentDataMapper.paymentRequestModelToPayment(paymentRequest);
        CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
        List<CreditHistory> creditHistories = getCreditHistory(payment.getCustomerId());
        List<String> failureMessages = new ArrayList<>();
        PaymentEvent paymentEvent = paymentDomainService.validateAndInitiatePayment(payment, creditEntry, creditHistories, failureMessages);
        persistDbObjects(payment, failureMessages, creditEntry, creditHistories, paymentEvent);
        orderOutboxHelper.saveOrderOutboxMessage(paymentDataMapper.paymentEventToOrderEventPayload(paymentEvent),
                paymentEvent.getPayment().getPaymentStatus(),
                OutboxStatus.STARTED,
                UUID.fromString(paymentRequest.getSagaId()));
        log.info("Payment complete for order id: {}", paymentRequest.getOrderId());

    }

    @Transactional
    public void persistCancelPayment(PaymentRequest paymentRequest) {
        log.info("Received payment rollback event for order id: {}", paymentRequest.getOrderId());

        if (isOutboxMessageProcessedForPayment(paymentRequest, PaymentStatus.CANCELLED)) {
            log.info("An outbox message has been already save in db : {}", paymentRequest.getOrderId());
            return;
        }

        Payment payment = paymentRepository.findByOrderId(UUID.fromString(paymentRequest.getOrderId()))
                .orElseThrow(() -> {
                    log.error("Payment with order id: {} could not be found!", paymentRequest.getOrderId());
                    return new PaymentApplicationServiceException(
                            "Payment with order id: " + paymentRequest.getOrderId() + " could not be found!"
                    );
                });

        CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
        List<CreditHistory> creditHistories = getCreditHistory(payment.getCustomerId());
        List<String> failureMessages = new ArrayList<>();
        PaymentEvent paymentEvent = paymentDomainService.validateAndCancelPayment(
                payment,
                creditEntry,
                creditHistories,
                failureMessages);
        persistDbObjects(payment, failureMessages, creditEntry, creditHistories, paymentEvent);
        orderOutboxHelper.saveOrderOutboxMessage(paymentDataMapper.paymentEventToOrderEventPayload(paymentEvent),
                paymentEvent.getPayment().getPaymentStatus(),
                OutboxStatus.STARTED,
                UUID.fromString(paymentRequest.getSagaId()));
    }

    private List<CreditHistory> getCreditHistory(CustomerId customerId) {
        return creditHistoryRepository.findByCustomerId(customerId)
                .orElseThrow(() -> {
                    log.error("Could not find credit history for customer: {}", customerId.getValue());
                    return new PaymentApplicationServiceException("Could not find credit history for customer: " +
                            customerId.getValue());
                });
    }

    private CreditEntry getCreditEntry(CustomerId customerId) {
        Optional<CreditEntry> creditEntry = creditEntryRepository.findByCustomerId(customerId);
        if (creditEntry.isEmpty()) {
            log.error("Could not find credit entry for customer: {}", customerId.getValue());
            throw new PaymentApplicationServiceException("Could not find credit entry for customer: " +
                    customerId.getValue());
        }
        return creditEntry.get();
    }

    private void persistDbObjects(Payment payment, List<String> failureMessages, CreditEntry creditEntry, List<CreditHistory> creditHistories, PaymentEvent paymentEvent) {
        paymentRepository.save(payment);
        if (failureMessages.isEmpty()) {
            creditEntryRepository.save(creditEntry);
            creditHistoryRepository.save(creditHistories.get(creditHistories.size() - 1));
        }
    }

    private boolean isOutboxMessageProcessedForPayment(PaymentRequest paymentRequest,
                                                              PaymentStatus paymentStatus) {
        Optional<OrderOutboxMessage> orderOutboxMessage =
                orderOutboxHelper.getCompletedOrderOutboxMessageBySagaIdAndPaymentStatus(
                        UUID.fromString(paymentRequest.getSagaId()),
                        paymentStatus);
        if (orderOutboxMessage.isPresent()) {
            return true;
        }
        return false;
    }


}