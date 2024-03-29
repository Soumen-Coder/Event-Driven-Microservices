package com.microservices.base.kafka.admin.client;

import com.microservices.base.config.KafkaConfigData;
import com.microservices.base.config.RetryConfigData;
import com.microservices.base.kafka.admin.exception.KafkaClientException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
// Creates and checks kafka topics programmatically
@Component
public class KafkaAdminClient {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaAdminClient.class);
    private KafkaConfigData kafkaConfigData;
    private RetryConfigData retryConfigData;
    private AdminClient adminClient;
    private RetryTemplate retryTemplate; // we need a retry logic because when we start everything together including kafka cluster and other services, you need to wait for certain time to until kafka cluster is healthy and be able to create topics and return list of topics
    private WebClient webClient; //webClient bean created in the WebClientConfig.

    public KafkaAdminClient(KafkaConfigData config,
                            RetryConfigData retryConfigData,
                            AdminClient client,
                            RetryTemplate template,
                            WebClient webClient) {
        this.kafkaConfigData = config;
        this.retryConfigData = retryConfigData;
        this.adminClient = client;
        this.retryTemplate = template;
        this.webClient = webClient;
    }

    public void createTopics() {
        CreateTopicsResult createTopicsResult;
        try {
            //retryTemplate.execute -> call a method with retry logic configured in the RetryConfig
            createTopicsResult = retryTemplate.execute(this::doCreateTopics);
        }catch (Throwable t){
            throw new KafkaClientException("Reached maximum number of exception for creating kafka topic(s)!");
        }
        checkTopicsCreated();
    }

    //Don't want to fail at startup because of schemaRegistry is unreachable.
    public void checkSchemaRegistry(){
       int retryCount=1;
       Integer maxRetry = retryConfigData.getMaxAttempts();
       int multiplier = retryConfigData.getMultiplier().intValue();
       Long sleepTimeMs = retryConfigData.getSleepTimeMs();
       while(!getSchemaRegistryStatus().is2xxSuccessful()){
           checkMaxRetry(retryCount++, maxRetry);
           sleep(sleepTimeMs);
           sleepTimeMs*=multiplier;
       }
    }

    private void checkMaxRetry(int retry, Integer maxRetry) {
        if(retry>maxRetry){
            throw new KafkaClientException("Reached max number of retry for reading Kafka Topic(s)");
        }
    }

    private void sleep(Long sleepTimeMs) {
        try {
            Thread.sleep(sleepTimeMs);
        } catch (InterruptedException e) {
            throw new KafkaClientException("Error while sleeping for waiting to create new Topic(s)");
        }
    }

    private HttpStatus getSchemaRegistryStatus(){
        try{
            // WebClient comes from the WebFlux Dependency
            // It is non-blocking, reactive client to perform http req, creates a fluent API
            return webClient
                    .method(HttpMethod.GET)
                    .uri(kafkaConfigData.getSchemaRegistryUrl())
                    .exchange()
                    .map(ClientResponse::statusCode)
                    .block(); // get results synchronously from schema registry
        }catch(Exception e){
            return HttpStatus.SERVICE_UNAVAILABLE;
        }
    }

    //Custom retry logic -> wait until topics created or max retry reached, increasing wait time exponentially
    public void checkTopicsCreated() {
        Collection<TopicListing> topics = getTopics();
        int retryCount = 1;
        Integer maxRetry = retryConfigData.getMaxAttempts();
        int multiplier = retryConfigData.getMultiplier().intValue();
        Long sleepTimeMs = retryConfigData.getSleepTimeMs();
        for (String topic : kafkaConfigData.getTopicNamesToCreate()) {
            while (!isTopicCreated(topics, topic)) {
                checkMaxRetry(retryCount++, maxRetry);
                sleep(sleepTimeMs);
                sleepTimeMs *= multiplier;
                topics = getTopics();
            }
        }
    }

    private Collection<TopicListing> getTopics() {
        Collection<TopicListing> topics;
        try {
            topics = retryTemplate.execute(this::doGetTopics);
        } catch (Throwable t) {
            throw new KafkaClientException("Reached max number of retry for reading kafka topic(s)!", t);
        }
        return topics;
    }

    private Collection<TopicListing> doGetTopics(RetryContext retryContext)
            throws ExecutionException, InterruptedException {
        LOG.info("Reading kafka topic {}, attempt {}",
                kafkaConfigData.getTopicNamesToCreate().toArray(), retryContext.getRetryCount());
        Collection<TopicListing> topics = adminClient.listTopics().listings().get();
        if (topics != null) {
            topics.forEach(topic -> LOG.debug("Topic with name {}", topic.name()));
        }
        return topics;
    }


    private boolean isTopicCreated(Collection<TopicListing> topics, String topicName) {
        if (topics == null) {
            return false;
        }
        return topics.stream().anyMatch(topic -> topic.name().equals(topicName));
    }

    private CreateTopicsResult doCreateTopics(RetryContext retryContext) {
       List<String> topicNames = kafkaConfigData.getTopicNamesToCreate();
       LOG.info("creating topic(s) {}, attempt {} ", topicNames.size(), retryContext.getRetryCount());
       List<NewTopic> kafkaTopics = topicNames.stream().map(topic -> new NewTopic(
               topic.trim(),
               kafkaConfigData.getNumOfPartitions(),
               kafkaConfigData.getReplicationFactor()
               )).collect(Collectors.toList());
       return adminClient.createTopics(kafkaTopics); //createTopics is an async operation, takes time to create topics, wait for creation, until then checkTopicsCreated()
    }
}
