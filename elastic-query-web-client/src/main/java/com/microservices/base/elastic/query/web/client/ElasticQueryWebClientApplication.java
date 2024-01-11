package com.microservices.base.elastic.query.web.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.microservices.base")
public class ElasticQueryWebClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(ElasticQueryWebClientApplication.class, args);
    }
}

/*
Usage and understanding :
Spring WebClient for elastic-query-service -> use spring-webclient, which is an Async and non-blocking successor of Rest Template, which supports only blocking requests, although here we will be using blocking calls in this section of code, but it is better to use webclient.
Thymeleaf -> Server-Side java template engine, well integrated with spring
Html thymeleaf with bootstrap -> Bootstrap is a html, css and javaScript framework which provides a lot of built-in capabilities to create a better frontend.
Since this includes a REST API call hence we will use Client-Side Load Balancing with spring cloud @LoadBalanced, preconfigured by spring boot for us - will be replaced by discovery-service and gateway later
*/