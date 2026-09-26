package com.thanaphat2005.food.ordering.system.order.service.messaging.publish.publisher.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class OrderKafkaMessageHelper {

    public <K,V>  BiConsumer<SendResult<K, V>, Throwable> getKafkaCallBack(String topicName, V message) {
        return (sendResult, throwable) -> {
            if (throwable == null) {
                RecordMetadata recordMetadata = sendResult.getRecordMetadata();
                log.info("Received response from Kafka! Topic: {}; Partition: {}; Offset: {}; Timestamp: {}",
                        recordMetadata.topic(),
                        recordMetadata.partition(),
                        recordMetadata.offset(),
                        recordMetadata.timestamp());
            } else {
                log.error("Error while sending message {} to topic {}", message.toString(), topicName, throwable);
            }
        };
    }
}
