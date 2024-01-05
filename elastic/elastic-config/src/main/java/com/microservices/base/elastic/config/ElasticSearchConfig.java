package com.microservices.base.elastic.config;

import com.microservices.base.config.ElasticConfigData;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.microservices.base.elastic") //This will be required for ElasticSearch repositories for indexing, an alternate method to save ids to elasticsearch, less code but less flexibility
//Required for spring to find elasticsearch repositories
public class ElasticSearchConfig extends AbstractElasticsearchConfiguration {
    private ElasticConfigData elasticConfigData;

    public ElasticSearchConfig(ElasticConfigData elasticConfigData) {
        this.elasticConfigData = elasticConfigData;
    }

    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        UriComponents serverUri = UriComponentsBuilder.fromHttpUrl(elasticConfigData.getConnectionUrl()).build();
        return new RestHighLevelClient(
                RestClient.builder(new HttpHost(
                        Objects.requireNonNull(serverUri.getHost()),
                        serverUri.getPort(),
                        serverUri.getScheme()
                )).setRequestConfigCallback(
                        requestConfigBuilder ->
                                requestConfigBuilder
                                        .setConnectTimeout(elasticConfigData.getConnectTimeoutMs())
                                        .setSocketTimeout(elasticConfigData.getSocketTimeoutMs())

                )
        );
    }

    @Bean
    public ElasticsearchOperations elasticSearchTemplate(){
        return new ElasticsearchRestTemplate(elasticsearchClient()); //This will be required for ElasticSearch repositories for indexing, an alternate method to save ids to elasticsearch, less code but less flexibility
    }
}
