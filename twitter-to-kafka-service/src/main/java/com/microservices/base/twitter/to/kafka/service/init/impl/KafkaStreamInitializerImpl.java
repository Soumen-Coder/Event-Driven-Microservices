package com.microservices.base.twitter.to.kafka.service.init.impl;

import com.microservices.base.config.KafkaConfigData;
import com.microservices.base.kafka.admin.client.KafkaAdminClient;
import com.microservices.base.twitter.to.kafka.service.init.StreamInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class KafkaStreamInitializerImpl implements StreamInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaStreamInitializerImpl.class);

    private final KafkaConfigData kafkaConfigData;

    private final KafkaAdminClient kafkaAdminClient;

    public KafkaStreamInitializerImpl(KafkaConfigData kafkaConfigData, KafkaAdminClient kafkaAdminClient) {
        this.kafkaConfigData = kafkaConfigData;
        this.kafkaAdminClient = kafkaAdminClient;
    }

    @Override
    public void init() {
        kafkaAdminClient.createTopics(); // create the kafka topics before the application startup
        kafkaAdminClient.checkSchemaRegistry(); // check if the schema registry is up and running fine, before running our service
        LOG.info("Topics with name {} is ready for operations!!!", kafkaConfigData.getTopicNamesToCreate().toArray());
    }
}
