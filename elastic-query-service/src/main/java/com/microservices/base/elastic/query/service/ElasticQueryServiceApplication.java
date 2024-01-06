package com.microservices.base.elastic.query.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
//This will be the query component of the microservices architecture being developed. We have separated the reads and the writes(to kafka event store) and this will be useful for effectively using the system.
//This is a concept of eventual consistency where reads and writes are separated to effectively use the system.
@SpringBootApplication
@ComponentScan(basePackages = "com.microservices.base")
public class ElasticQueryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ElasticQueryServiceApplication.class, args);
    }
}