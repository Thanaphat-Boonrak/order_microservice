package com.thanaphat2005.food.ordering.system.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thanaphat2005.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.thanaphat2005.food.ordering.system.outbox.OutboxStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class KafkaMessageHelper {

    private final ObjectMapper objectMapper;

    public KafkaMessageHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <V,U>  BiConsumer<SendResult<String, V>, Throwable> getKafkaCallBack(String topicName, V message,U outboxMessage, BiConsumer<U, OutboxStatus> outboxCallback) {
        return (sendResult, throwable) -> {
            if (throwable == null) {
                RecordMetadata recordMetadata = sendResult.getRecordMetadata();
                log.info("Received response from Kafka! Topic: {}; Partition: {}; Offset: {}; Timestamp: {}",
                        recordMetadata.topic(),
                        recordMetadata.partition(),
                        recordMetadata.offset(),
                        recordMetadata.timestamp());
                outboxCallback.accept(outboxMessage, OutboxStatus.COMPLETED);
            } else {
                log.error("Error while sending message {} to topic {}", message.toString(), topicName, throwable);
                outboxCallback.accept(outboxMessage, OutboxStatus.FAILED);

            }
        };
    }


    public <T> T getOrderEventPayload(String payload,Class<T> type) {

        try {

            return objectMapper.readValue(payload, type);

        } catch (JsonProcessingException e) {
            log.error("Could not read {} object!",type, e);
            throw new OrderDomainException("Could not read " + type.getName() + " object", e);
        }

    }
}
