package com.microservices.base;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
//One of the factor of the 12-factor methodology -> Store config in an environment, the config factor
@EnableConfigServer //spring boot application now serve as a config server
@SpringBootApplication
public class ConfigServer {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServer.class, args);
    }
}