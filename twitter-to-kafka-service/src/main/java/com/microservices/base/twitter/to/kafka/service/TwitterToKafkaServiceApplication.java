package com.microservices.base.twitter.to.kafka.service;

import com.microservices.base.config.TwitterToKafkaServiceConfigData;
import com.microservices.base.twitter.to.kafka.service.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;

@SpringBootApplication
@ComponentScan(basePackages = "com.microservices.base")
public class TwitterToKafkaServiceApplication implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterToKafkaServiceApplication.class);

    private final TwitterToKafkaServiceConfigData configData;

    private final StreamRunner streamRunner;

    //This is a constructor injection
    //We haven't used @Autowired which is ued with field injection.
    //We are sure that TwitterToKafkaServiceConfigData will be initialized as soon as the main method runs.
    //Constructor injection has some advantages over field injection.
    //Field Injection uses reflection which basically slows down a program since types are resolved at runtime.
    //Forces object creation with the injected object.
    //Constructor injection favors immutability which is more stable

    public TwitterToKafkaServiceApplication(TwitterToKafkaServiceConfigData configData, StreamRunner streamRunner){
        this.configData = configData;
        this.streamRunner = streamRunner;
    }

    public static void main(String[] args) {
        SpringApplication.run(TwitterToKafkaServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
       LOG.info("App Starting!!!");
       LOG.info(Arrays.toString(configData.getTwitterKeywords().toArray(new String[] {})));
       LOG.info(configData.getWelcomeMessage());
       LOG.info("Starting to read Twitter Messages :: ");
       streamRunner.start();
    }
}
