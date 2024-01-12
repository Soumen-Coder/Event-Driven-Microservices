package com.microservices.base.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "elastic-query-web-client")
//Please check out the config-client-elastic_query_web.yml file to know how and when we construct subclasses according to the properties defined in yml
public class ElasticQueryWebClientConfigData {
    private WebClient webClient;
    private Query queryByText;
    @Data
    public static class WebClient {
         private Integer connectTimeoutMs;
         private Integer readTimeoutMs;
         private Integer writeTimeoutMs;
         private Integer maxInMemorySize;
         private String contentType;
         private String acceptType;
         private String baseUrl;
         private String serviceId;
         private List<Instance> instances;
    }
    @Data
    public static class Query {
        private String method;
        private String uri;
        private String accept;
    }
    @Data
    public static class Instance {
        private String id;
        private String host;
        private Integer port;
    }
}
