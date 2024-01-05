package com.microservices.base.elastic.index.client.service.impl;

import com.microservices.base.config.ElasticConfigData;
import com.microservices.base.elastic.index.client.service.ElasticIndexClient;
import com.microservices.base.elastic.index.client.util.ElasticIndexUtil;
import com.microservices.base.elastic.model.index.impl.TwitterIndexModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@ConditionalOnProperty(name = "elastic-config.is-repository", havingValue = "false")
@Service
public class TwitterElasticIndexClient implements ElasticIndexClient<TwitterIndexModel> {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterElasticIndexClient.class);

    private final ElasticConfigData elasticConfigData;

    private final ElasticsearchOperations elasticsearchOperations; //to interact with elastic search by sending index queries - it index and query against elasticsearch
    //Unlike ElasticSearchRepository, ElasticSearchOperation gives a chance of running low-level queries like elasticsearch bool, must etc.

    private final ElasticIndexUtil<TwitterIndexModel> elasticIndexUtil;

    public TwitterElasticIndexClient(ElasticConfigData configData,
                                     ElasticsearchOperations elasticOperations,
                                     ElasticIndexUtil<TwitterIndexModel> indexUtil) {
        this.elasticConfigData = configData;
        this.elasticsearchOperations = elasticOperations;
        this.elasticIndexUtil = indexUtil;
    }

    //ElasticSearchOperation requires to convert input objects to query objects
    @Override
    public List<String> save(List<TwitterIndexModel> documents) {
        List<IndexQuery> indexQueries = elasticIndexUtil.getIndexQueries(documents);
        List<String> documentIds = elasticsearchOperations.bulkIndex(
                indexQueries,
                IndexCoordinates.of(elasticConfigData.getIndexName())
        );// This bulkIndex method will save the data in bulk by sending all queries to elasticsearch at once.
        //The documentIds are the list of Ids inserted to the elastic search
        LOG.info("Documents indexed successfully with type: {} and ids: {}", TwitterIndexModel.class.getName(),
                documentIds);
        return documentIds;
    }
}
