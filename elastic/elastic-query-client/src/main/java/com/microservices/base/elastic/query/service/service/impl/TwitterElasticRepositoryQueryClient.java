package com.microservices.base.elastic.query.service.service.impl;

import com.microservices.base.common.util.CollectionsUtil;
import com.microservices.base.elastic.model.index.impl.TwitterIndexModel;
import com.microservices.base.elastic.query.service.exception.ElasticQueryClientException;
import com.microservices.base.elastic.query.service.repository.TwitterElasticSearchQueryRepository;
import com.microservices.base.elastic.query.service.service.ElasticQueryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class TwitterElasticRepositoryQueryClient implements ElasticQueryClient<TwitterIndexModel> {
    private static final Logger LOG = LoggerFactory.getLogger(TwitterElasticRepositoryQueryClient.class);
    private final TwitterElasticSearchQueryRepository repository;

    public TwitterElasticRepositoryQueryClient(TwitterElasticSearchQueryRepository repository) {
        this.repository = repository;
    }

    @Override
    public TwitterIndexModel getIndexModelById(String id) {
        Optional<TwitterIndexModel> searchResult = repository.findById(id);
        LOG.info("Document with id {} retrieved successfully",
                searchResult.orElseThrow(() ->
                        new ElasticQueryClientException("No document found at elastic with id "+id)).getId());
        return searchResult.get();
    }

    @Override
    public List<TwitterIndexModel> getIndexModelByText(String text) {
        List<TwitterIndexModel> searchResults = repository.findByText(text);
        LOG.info("{} documents with text {} retrieved successfully", searchResults.size(), text);
        return searchResults;
    }

    @Override
    public List<TwitterIndexModel> getAllIndexModels() {
        List<TwitterIndexModel> searchResults = CollectionsUtil.getInstance().getListFromIterable(repository.findAll());
        LOG.info("{} documents retrieved successfully", searchResults.size());
        return searchResults;
    }
}
