package com.microservices.base.kafka.to.elastic.service.consumer;

import org.apache.avro.specific.SpecificRecordBase;

import java.io.Serializable;
import java.util.List;

public interface KafkaConsumer<K extends Serializable, V extends SpecificRecordBase> {
    void receive(List<V> messages, List<Integer> keys, List<Integer> partitions, List<Long> offsets); //Here V will be the TwitterAvroModel which is the subclass of SpecificRecordBase
}
