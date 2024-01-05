package com.microservices.base.kafka.to.elastic.service.consumer.impl;

import com.microservices.base.config.ElasticConfigData;
import com.microservices.base.config.KafkaConsumerConfigData;
import com.microservices.base.elastic.index.client.service.ElasticIndexClient;
import com.microservices.base.elastic.model.index.impl.TwitterIndexModel;
import com.microservices.base.kafka.to.elastic.service.consumer.KafkaConsumer;
import com.microservices.base.config.KafkaConfigData;
import com.microservices.base.kafka.admin.client.KafkaAdminClient;
import com.microservices.base.kafka.avro.model.TwitterAvroModel;
import com.microservices.base.kafka.to.elastic.service.transformer.AvroToElasticModelTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class TwitterKafkaConsumer implements KafkaConsumer<Long, TwitterAvroModel> {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterKafkaConsumer.class);
    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    private final KafkaAdminClient kafkaAdminClient;
    private final KafkaConfigData kafkaConfigData;
    private final AvroToElasticModelTransformer avroToElasticModelTransformer;
    private final ElasticIndexClient<TwitterIndexModel> elasticIndexClient;
    private final KafkaConsumerConfigData kafkaConsumerConfigData;



    public TwitterKafkaConsumer(KafkaListenerEndpointRegistry listenerEndpointRegistry,
                                KafkaAdminClient adminClient,
                                KafkaConfigData configData, AvroToElasticModelTransformer avroToElasticModelTransformer, ElasticIndexClient<TwitterIndexModel> elasticIndexClient, ElasticConfigData elasticConfigData, KafkaConsumerConfigData kafkaConsumerConfigData) {
        this.kafkaListenerEndpointRegistry = listenerEndpointRegistry;
        this.kafkaAdminClient = adminClient;
        this.kafkaConfigData = configData;
        this.avroToElasticModelTransformer = avroToElasticModelTransformer;
        this.elasticIndexClient = elasticIndexClient;
        this.kafkaConsumerConfigData = kafkaConsumerConfigData;
    }

    //Adding initialization check, when app start starts, will set auto-startup to false and trigger it from here
    @EventListener // one of the ways of running a code on a spring boot application startup
    public void onAppStarted(ApplicationStartedEvent event){
       kafkaAdminClient.checkTopicsCreated(); // check if topics are created or not
       LOG.info("Topic with name {} is ready for operations!", kafkaConfigData.getTopicNamesToCreate().toArray());
       Objects.requireNonNull(kafkaListenerEndpointRegistry.getListenerContainer(kafkaConsumerConfigData.getConsumerGroupId())).start(); // start it manually. To prevent NullPointerException, we used Objects.requireNonNull when we start the kafka consumer consumption process.
       //getListenerContainer should have the id which was marked using @KafkaListener -> should be consumer-group-id -> unique id
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.consumer-group-id}", topics = "${kafka-config.topic-name}")
    //if we don't use the id field above, the id property override the kafka consumer group id and sets the group id as the id used, but we already have a specific group id in the configuration, which should be used, and because there is a property "idIsGroup" which overrides the group id by setting it from the id property, it does so. And it is set to true by default, and we want to prevent that.
    //id should be used as consumer-group-id, if not, then by the idIsGroup property which is set to true, will override the consumer0group-id and then things will fail
    public void receive(@Payload List<TwitterAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<Integer> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        LOG.info("{} number of message received with keys {}, partitions {} and offsets {}, " +
                        "sending it to elastic: Thread id {}",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString(),
                Thread.currentThread().getId());

        List<TwitterIndexModel> twitterIndexModels = avroToElasticModelTransformer.getElasticModel(messages);
        List<String> documentIds = elasticIndexClient.save(twitterIndexModels); //List of Ids saved to elasticsearch
        LOG.info("Document saved to ElasticSearch with ids {} ", documentIds.toArray());
    }
}

//Before running this service, make sure to commit the changes, so that github repo gets the latest changes for the config-server-repository yml file and can sync