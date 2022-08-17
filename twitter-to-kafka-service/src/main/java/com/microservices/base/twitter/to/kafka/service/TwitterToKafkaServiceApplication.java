package com.microservices.base.twitter.to.kafka.service;

import com.microservices.base.twitter.to.kafka.service.config.TwitterToKafkaServiceConfigData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
public class TwitterToKafkaServiceApplication implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterToKafkaServiceApplication.class);

    private final TwitterToKafkaServiceConfigData configData;

    public TwitterToKafkaServiceApplication(TwitterToKafkaServiceConfigData configData){
        this.configData = configData;
    }

    public static void main(String[] args) {
        SpringApplication.run(TwitterToKafkaServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
       LOG.info("App Starting!!!");
       LOG.info(Arrays.toString(configData.getTwitterKeywords().toArray(new String[] {})));
    }
}