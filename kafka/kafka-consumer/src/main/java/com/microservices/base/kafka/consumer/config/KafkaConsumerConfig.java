package com.microservices.base.kafka.consumer.config;

import com.microservices.base.config.KafkaConfigData;
import com.microservices.base.config.KafkaConsumerConfigData;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@EnableKafka //Enables detection of Kafka listener annotation, for a spring application you will mandatorily need to use this annotation, for spring boot, you can skip the @EnableKafka because spring boot will automatically enable kafka Listener annotation
@Configuration
public class KafkaConsumerConfig<K extends Serializable, V extends SpecificRecordBase> {

    private final KafkaConfigData kafkaConfigData;

    private final KafkaConsumerConfigData kafkaConsumerConfigData;

    public KafkaConsumerConfig(KafkaConfigData configData, KafkaConsumerConfigData consumerConfigData) {
        this.kafkaConfigData = configData;
        this.kafkaConsumerConfigData = consumerConfigData;
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServers());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaConsumerConfigData.getKeyDeserializer());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaConsumerConfigData.getValueDeserializer());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerConfigData.getConsumerGroupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConsumerConfigData.getAutoOffsetReset());
        props.put(kafkaConfigData.getSchemaRegistryUrlKey(), kafkaConfigData.getSchemaRegistryUrl());
        props.put(kafkaConsumerConfigData.getSpecificAvroReaderKey(), kafkaConsumerConfigData.getSpecificAvroReader());
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, kafkaConsumerConfigData.getSessionTimeoutMs());
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, kafkaConsumerConfigData.getHeartbeatIntervalMs());
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, kafkaConsumerConfigData.getMaxPollIntervalMs());
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG,
                kafkaConsumerConfigData.getMaxPartitionFetchBytesDefault() *
                        kafkaConsumerConfigData.getMaxPartitionFetchBytesBoostFactor());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, kafkaConsumerConfigData.getMaxPollRecords());
        return props;
    }

    @Bean
    public ConsumerFactory<K, V> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<K, V>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<K, V> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setBatchListener(kafkaConsumerConfigData.getBatchListener()); //allows reading data in batches
        factory.setConcurrency(kafkaConsumerConfigData.getConcurrencyLevel()); //the thread count for consumer created by spring-boot, equals to partition number, for high throughput and max concurrency
        factory.setAutoStartup(kafkaConsumerConfigData.getAutoStartup()); // as soon as the application starts and we find kafka topic, we start reading from it, but we don't want that hence it is false, we will read explicitly, that is trigger the start reading later
        factory.getContainerProperties().setPollTimeout(kafkaConsumerConfigData.getPollTimeoutMs());// how much time we wait for atleast a single record to be available, until we call the next poll method
        return factory;
    }
}

//Producer send data based on a partitioning strategy 1)hashing with key -> hash(key)%num_of_partition will give target partition number 2) RoundRobin -> write to each partition one by one
//Kafka hold the data called as logs/event in a data structure called topics and inside the topics, it has one or more partitions to store the data. Kafka Producers write the data to the end of a specific partition and
//Kafka consumers read the logs starting from the beginning and when it is done consumer sends a commit request to the broker
//so that there will be an offset per consumer per partition, to remember the latest record that is read.
//This way consumers read the latest data instead of reading the already read old data again
//Each consumer belongs to a consumer group id
//Kafka distributes partitions to consumers based on consumer group identifiers
//Each partition is strictly assigned to a single consumer in a consumer group, this is to prevent conflicts with the offset values and make consuming easier.
//Different consumer groups can read same data from the same partition
//For max concurrency, set partition number equal to consumer number, more consumer than partition will be idle.
//There is a poll timeout in kafka consumer and setting it to too high value will block it indefinitely(long delays), not too small to prevent cpu stalls(cycles).
//To start from beginning with a new consumer group we can set the auto.offset.reset property to earliest which has default value of latest(last committed offset)
//You can use assign method to set tracing offset explicitly
//You can also use custom ConsumerRebalanceListener when subscribing and override onPartitionAssigned method to use consumer.seekToBeginning method


// If you have a single consumer and multiple partitions then it will not use parallel partition consuming(no concurrent work). But you can still use multiple thread
//while consuming partitions to gain some throughput which can be scaled according to the need, this can be implemented on the client side
//Scale by partition in consumer level is limited to partition number, so adding a new partition gives us a chance to add a new consumer and adds new resources for partition consuming
//And for each consumer assigned to a dingle partition, we can create multiple threads on the client side and more threads will mean more TCS connections to kafka brokers
//Also, multiple threads will break ordering in a single partition, need to take care of it.

//Three types of delivery semantics in kafka -> Atleast once, Atmost once and exactly once. Rely on consumer commits and acks
//If you commit after processing -> atleast once, if fail, in next poll read same data and it will imly atleast once
//If you commit before processing -> atmost once, if fail, before process, might loose some data, atmost once
//Exactly once will require to coordinate b/w producer and consumer and using transactions(Transaction API) starting from producer part

//Normally auto-commit is set to true, so it does a commit after configured timer and reset the timer, but there can always be errors so you can use exactly once semantics depending on needs.