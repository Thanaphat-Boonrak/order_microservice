package com.thanaphat2005.food.ordering.system.kafka.producer.service.impl;

import com.thanaphat2005.food.ordering.system.kafka.producer.service.KafkaProducer;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@Slf4j
@Component
public class KafkaProducerImpl<K extends Serializable, V extends SpecificRecordBase> implements KafkaProducer<K, V> {


    private final KafkaTemplate<K, V> kafkaTemplate;

    public KafkaProducerImpl(KafkaTemplate<K, V> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(String topicName, K key, V message, BiConsumer<SendResult<K, V>, Throwable> callback) {
        log.info("send topicName={}, key={}, message={}", topicName, key, message);
        CompletableFuture<SendResult<K, V>> future = kafkaTemplate.send(topicName, key, message);
        if (callback != null) {
            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to send message to topicName={}, key={}, message={} and exception={}",
                            topicName, key, message, ex.getMessage());
                }
                callback.accept(result, ex);
            });
        }
    }

    @PreDestroy
    public void close() {
        if(kafkaTemplate != null) {
            log.info("close kafkaTemplate Producer");
            kafkaTemplate.destroy();
        }
    }
}
