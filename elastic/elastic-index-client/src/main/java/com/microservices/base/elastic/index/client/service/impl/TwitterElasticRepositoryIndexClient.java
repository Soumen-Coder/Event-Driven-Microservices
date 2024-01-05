package com.microservices.base.elastic.index.client.service.impl;

import com.microservices.base.elastic.index.client.service.ElasticIndexClient;
import com.microservices.base.elastic.model.index.impl.TwitterIndexModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@ConditionalOnProperty(name = "elastic-config.is-repository", havingValue = "true", matchIfMissing = true)
//replaced by @ConditionalOnProperty annotation -> @Primary //Primary implementation of ElasticIndexClient interface, loading one of the implementations, one has priority over the other
@Service
public class TwitterElasticRepositoryIndexClient implements ElasticIndexClient<TwitterIndexModel> {
    private static final Logger LOG = LoggerFactory.getLogger(TwitterElasticIndexClient.class);
    private final ElasticsearchRepository elasticsearchRepository; //provides convenient methods like save, find etc.

    public TwitterElasticRepositoryIndexClient(ElasticsearchRepository elasticsearchRepository) {
        this.elasticsearchRepository = elasticsearchRepository;
    }

    @Override
    public List<String> save(List<TwitterIndexModel> documents) {
        List<TwitterIndexModel> repositoryResponse = (List<TwitterIndexModel>) elasticsearchRepository.saveAll(documents);
        List<String> documentIds = repositoryResponse.stream().map(TwitterIndexModel::getId).collect(Collectors.toList());
        LOG.info("Documents indexed successfully with type: {} and ids: {}", TwitterIndexModel.class.getName(),
                documentIds);
        return documentIds;
    }
}
