package com.microservices.base.kafkfa.producer.service.impl;

import com.microservices.base.kafka.avro.model.TwitterAvroModel;
import com.microservices.base.kafkfa.producer.service.KafkaProducer;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.PreDestroy;

@Service
//K -> key is Long and V is TwitterAvroModel, a class from the kafka-model
// Key parameter is used to determine the target partition of a message
public class TwitterKafkaProducerImpl implements KafkaProducer<Long, TwitterAvroModel> {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterKafkaProducerImpl.class);

    private KafkaTemplate<Long, TwitterAvroModel> kafkaTemplate;

    public TwitterKafkaProducerImpl(KafkaTemplate<Long, TwitterAvroModel> template) {
        this.kafkaTemplate = template;
    }
    @Override
    public void send(String topicName, Long key, TwitterAvroModel message) {
        LOG.info("Sending message='{}' to topic='{}'", message, topicName);
        //ListenableFuture -> Registers callBack methods(addCallback()) for handling events(onSuccess and onFailure) when response return
        ListenableFuture<SendResult<Long, TwitterAvroModel>> kafkaResultFuture =
                kafkaTemplate.send(topicName, key, message); //the send method from kafkaTemplate is an async operation and hence it returns a Future object, ListenableFuture, and to get the response back async, we simply added a callback and override its onSuccess and onFailure methods.
        addCallback(topicName, message, kafkaResultFuture);
    }

    @PreDestroy
    public void close() {
        if (kafkaTemplate != null) {
            LOG.info("Closing kafka producer!");
            kafkaTemplate.destroy(); //destroyed successfully before shutdown of the application
        }
    }

    private void addCallback(String topicName, TwitterAvroModel message,
                             ListenableFuture<SendResult<Long, TwitterAvroModel>> kafkaResultFuture) {
        kafkaResultFuture.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable throwable) {
                LOG.error("Error while sending message {} to topic {}", message.toString(), topicName, throwable);
            }

            @Override
            public void onSuccess(SendResult<Long, TwitterAvroModel> result) {
                RecordMetadata metadata = result.getRecordMetadata();
                LOG.debug("Received new metadata. Topic: {}; Partition {}; Offset {}; Timestamp {}, at time {}",
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset(),
                        metadata.timestamp(),
                        System.nanoTime());
            }
        });
    }
}
