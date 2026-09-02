package com.thanaphat2005.food.ordering.system.service.message.publisher.kafka;

import com.thanaphat2005.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.thanaphat2005.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.thanaphat2005.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.thanaphat2005.food.ordering.system.outbox.OutboxStatus;
import com.thanaphat2005.food.ordering.system.payment.service.domain.config.PaymentServiceConfigData;
import com.thanaphat2005.food.ordering.system.payment.service.domain.outbox.model.OrderEventPayload;
import com.thanaphat2005.food.ordering.system.payment.service.domain.outbox.model.OrderOutboxMessage;
import com.thanaphat2005.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentResponseMessagePublisher;
import com.thanaphat2005.food.ordering.system.service.message.mapper.PaymentMessageDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class PaymentEventKafkaPublisher implements PaymentResponseMessagePublisher {

    private final PaymentMessageDataMapper paymentMessagingDataMapper;
    private final KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer;
    private final PaymentServiceConfigData paymentServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;

    public PaymentEventKafkaPublisher(PaymentMessageDataMapper paymentMessagingDataMapper,
                                      KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer,
                                      PaymentServiceConfigData paymentServiceConfigData,
                                      KafkaMessageHelper kafkaMessageHelper) {
        this.paymentMessagingDataMapper = paymentMessagingDataMapper;
        this.kafkaProducer = kafkaProducer;
        this.paymentServiceConfigData = paymentServiceConfigData;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }

    @Override
    public void publish(OrderOutboxMessage orderOutboxMessage,
                        BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback) {
        OrderEventPayload orderEventPayload =
                kafkaMessageHelper.getOrderEventPayload(orderOutboxMessage.getPayload(), OrderEventPayload.class);

        String sagaId = orderOutboxMessage.getSagaId().toString();

        log.info("Received OrderOutboxMessage for order id: {} and saga id: {}",
                orderEventPayload.getOrderId(),
                sagaId);

        try {
            PaymentResponseAvroModel paymentResponseAvroModel = paymentMessagingDataMapper
                    .orderEventPayloadToPaymentResponseAvroModel(sagaId, orderEventPayload);

            kafkaProducer.send(paymentServiceConfigData.getPaymentResponseTopicName(),
                    sagaId,
                    paymentResponseAvroModel,
                    kafkaMessageHelper.getKafkaCallBack(paymentServiceConfigData.getPaymentResponseTopicName(),
                            paymentResponseAvroModel,
                            orderOutboxMessage,
                            outboxCallback));

            log.info("PaymentResponseAvroModel sent to kafka for order id: {} and saga id: {}",
                    paymentResponseAvroModel.getOrderId(), sagaId);
        } catch (Exception e) {
            log.error("Error while sending PaymentRequestAvroModel message" +
                            " to kafka with order id: {} and saga id: {}, error: {}",
                    orderEventPayload.getOrderId(), sagaId, e.getMessage());
        }
    }
}
