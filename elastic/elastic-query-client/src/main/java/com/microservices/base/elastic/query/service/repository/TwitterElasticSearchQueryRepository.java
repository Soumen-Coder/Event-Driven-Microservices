package com.microservices.base.elastic.query.service.repository;

import com.microservices.base.elastic.model.index.impl.TwitterIndexModel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TwitterElasticSearchQueryRepository extends ElasticsearchRepository<TwitterIndexModel, String> {
    List<TwitterIndexModel> findByText(String text); //to be able to query the "index" by text, this method will be implemented by spring at runtime to query the elasticsearch using the text-field on the specified index
}
