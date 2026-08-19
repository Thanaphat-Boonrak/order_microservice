package com.thanaphat2005.food.ordering.system.service.message.publisher.kafka;

import com.thanaphat2005.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.thanaphat2005.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.thanaphat2005.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.thanaphat2005.food.ordering.system.payment.service.domain.config.PaymentServiceConfigData;
import com.thanaphat2005.food.ordering.system.payment.service.domain.event.PaymentCompletedEvent;
import com.thanaphat2005.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentCompleteMessagePublisher;
import com.thanaphat2005.food.ordering.system.service.message.mapper.PaymentMessageDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class PaymentCompleteKafkaMessagePublisher implements PaymentCompleteMessagePublisher {
    private final PaymentMessageDataMapper paymentMessagingDataMapper;
    private final KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer;
    private final PaymentServiceConfigData paymentServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;

    public PaymentCompleteKafkaMessagePublisher(PaymentMessageDataMapper paymentMessagingDataMapper,
                                                KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer,
                                                PaymentServiceConfigData paymentServiceConfigData,
                                                KafkaMessageHelper kafkaMessageHelper) {
        this.paymentMessagingDataMapper = paymentMessagingDataMapper;
        this.kafkaProducer = kafkaProducer;
        this.paymentServiceConfigData = paymentServiceConfigData;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }

    @Override
    public void publish(PaymentCompletedEvent domainEvent) {
        String orderId = domainEvent.getPayment().getOrderId().getValue().toString();

        log.info("Received PaymentCompletedEvent for order id: {}", orderId);

        try {
            PaymentResponseAvroModel paymentResponseAvroModel =
                    paymentMessagingDataMapper.paymentCompletedEventToPaymentResponseAvroModel(domainEvent);

            kafkaProducer.send(paymentServiceConfigData.getPaymentResponseTopicName(),
                    orderId,
                    paymentResponseAvroModel,
                    kafkaMessageHelper.<String, PaymentResponseAvroModel>getKafkaCallBack(paymentServiceConfigData.getPaymentResponseTopicName(),paymentResponseAvroModel));

            log.info("PaymentResponseAvroModel sent to kafka for order id: {}", orderId);
        } catch (Exception e) {
            log.error("Error while sending PaymentResponseAvroModel message" +
                    " to kafka with order id: {}, error: {}", orderId, e.getMessage());
        }
    }
}
