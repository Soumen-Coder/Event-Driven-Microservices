package com.microservices.base.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "retry-config")
public class RetryConfigData {
    private Long initialIntervalMs;
    private Long maxIntervalMs;
    private Double multiplier; // for exponential backoff policy
    private Integer maxAttempts; // for simple retry policy
    private Long sleepTimeMs;
}
