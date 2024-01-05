package com.microservices.base.twitter.to.kafka.service.listener;

import com.microservices.base.config.KafkaConfigData;
import com.microservices.base.kafka.avro.model.TwitterAvroModel;
import com.microservices.base.kafkfa.producer.service.KafkaProducer;
import com.microservices.base.twitter.to.kafka.service.transformer.TwitterStatusToAvroTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.StatusAdapter;
//we didn't wanted every method of StatusAdapter to be implemented, hence we created our own class and extended the StatusAdapter class
@Component
public class TwitterKafkaStatusListener extends StatusAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterKafkaStatusListener.class);

    private final KafkaConfigData kafkaConfigData;

    private final KafkaProducer<Long, TwitterAvroModel> kafkaProducer;

    private final TwitterStatusToAvroTransformer twitterStatusToAvroTransformer;

    public TwitterKafkaStatusListener(KafkaConfigData configData,
                                      KafkaProducer<Long, TwitterAvroModel> producer,
                                      TwitterStatusToAvroTransformer transformer) {
        this.kafkaConfigData = configData;
        this.kafkaProducer = producer;
        this.twitterStatusToAvroTransformer = transformer;
    }

    @Override
    public void onStatus(Status status) {
        LOG.info("Received status text {} sending to kafka topic {}", status.getText(), kafkaConfigData.getTopicName());
        //after retrieving the tweet from twitter, convert it to TwitterAvroModel, before sending it to kafka.
        TwitterAvroModel twitterAvroModel = twitterStatusToAvroTransformer.getTwitterAvroModelFromStatus(status);
        kafkaProducer.send(kafkaConfigData.getTopicName(), twitterAvroModel.getUserId(), twitterAvroModel); //kafka partition key - the target partition for message is -> the user id from the tweet
        //partition the data based on user id of the twitterAvroModel, that way the tweets that belong to a user will be inserted in the same partition of the kafka topic.
    }
}
