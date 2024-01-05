package com.microservices.base.elastic.index.client.repository;

import com.microservices.base.elastic.model.index.impl.TwitterIndexModel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository //used for classes that are in persistence layer, and has data interactions
public interface TwitterElasticSearchIndexRepository extends ElasticsearchRepository<TwitterIndexModel, String> {
}
