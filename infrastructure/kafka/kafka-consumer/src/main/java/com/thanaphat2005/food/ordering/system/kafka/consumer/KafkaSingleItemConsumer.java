package com.thanaphat2005.food.ordering.system.kafka.consumer;

import org.apache.avro.specific.SpecificRecordBase;

public interface KafkaSingleItemConsumer<T extends SpecificRecordBase> {
    void receive(T messages,String key,Integer partitions , Long offsets);
}
