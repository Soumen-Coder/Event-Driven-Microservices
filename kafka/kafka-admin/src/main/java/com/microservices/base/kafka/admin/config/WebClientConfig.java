package com.microservices.base.kafka.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    // To use webclient to checkSchemaRegistry up and running
    @Bean //creates a bean at runtime
    WebClient webClient(){
       return WebClient.builder().build();
    }
}
