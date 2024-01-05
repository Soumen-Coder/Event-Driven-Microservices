package com.microservices.base.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
//Lombok adds getters and setters and updates the class file during compilation and the resulting byte-code that will be created by JVM will include these methods created by Lombok.
@Data
@Configuration
@ConfigurationProperties(prefix = "twitter-to-kafka-service")
public class TwitterToKafkaServiceConfigData {
    private List<String> twitterKeywords;
    private String welcomeMessage;
    private String twitterV2BaseUrl;
    private String twitterV2RulesBaseUrl;
    private String twitterV2BearerToken;
    private Boolean enableMockTweets;
    private Long mockSleepMs;
    private Integer MockMinTweetLength;
    private Integer MockMaxTweetLength;
}
