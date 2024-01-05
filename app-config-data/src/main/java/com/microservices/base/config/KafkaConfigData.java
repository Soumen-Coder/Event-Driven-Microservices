package com.microservices.base.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-config")
//Instead of memory, kafka relies on file-system for storing and caching messages hence it's resilient
//It is fast, because it relies on disk caching(page cache, directly with memory of underlying OS) and memory-mapped(virtual memory) files of underlying OS instead of GC eligible JVM memory
public class KafkaConfigData {
    private String bootstrapServers;
    private String schemaRegistryUrlKey;
    private String schemaRegistryUrl;
    private String topicName; // Immutable append-only structures/log to hold tweets/data/events called topics
    private List<String> topicNamesToCreate;
    private Integer numOfPartitions; //partitions in each topics, hence it can naturally scale unlike Rabbit MQ, which is useful for vertical scaling
    //if ordering is important, put all related data in the same partition, ordering only guaranteed in single partition
    //use partitioning strategy to insert all the related data to the same partition
    private Short replicationFactor; //replicates data in each partitions, on different brokers
}
