package com.thanaphat2005.food.ordering.system.kafka.consumer;

import org.apache.avro.specific.SpecificRecordBase;

import java.util.List;

public interface KafkaConsumer<T extends SpecificRecordBase> {
    void receive(List<T> messages,List<String> key,List<Integer> partitions , List<Long> offsets);
}
