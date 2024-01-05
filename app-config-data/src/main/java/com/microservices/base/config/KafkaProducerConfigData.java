package com.microservices.base.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-producer-config")
public class KafkaProducerConfigData {
    private String keySerializerClass;
    private String valueSerializerClass;
    //larger batch size makes compression more efficient
    //end-to-end compression is also possible if kafka broker config compression.type is set to producer. It can be reused by consumer.
    private String compressionType; // default is none else can be gzip, snappy(fast), lz4.
    //multiple broker nodes, hold same data to increase resiliency. by default wait for all replica to return result(ack=all)
    //if ack=1, only broker that gets the request will send confirmation, instead of waiting for all sync replicas
    //if ack=0, no ack, no resiliency. rade-off resiliency vs performance
    private String acks;
    //less batchSize, low throughput, data will not be batched || very high batch size, memory will be wasted.
    private Integer batchSize; //holds a buffer of records per topic partition sized at batch.size property.
    private Integer batchSizeBoostFactor; // to change the batchSize
    //under heavy load, data will most probably be batched || under light load data will not be batched. In that case increase linger.ms to increase throughput, by increasing batching with fewer requests and with an increased latency on producer sends.
    private Integer lingerMs; //adding a delay in milliseconds on producer for high throughput, default is 0. Trade-offs(Delay vs throughput)
    private Integer requestTimeoutMs; //client property, default is 30 ms, causes client to wait for server to respond that much time.
    private Integer retryCount;// defaults to 0. retries happen, order might be lost, to preserve ordering you have to use the below property
    //max.in.flight.requests.per.connection -> limit request number on producer, if set to 1, the subsequent send will wait for previous send result.
    //Trade-off order vs performance.

    //partitioner.class = default to DefaultPartitioner one by one in rational order
}
