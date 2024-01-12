package com.microservices.base.elastic.query.web.client.service;

import com.microservices.base.elastic.query.web.client.model.ElasticQueryWebClientRequestModel;
import com.microservices.base.elastic.query.web.client.model.ElasticQueryWebClientResponseModel;

import java.util.List;

public interface ElasticQueryWebClient {
    List<ElasticQueryWebClientResponseModel> getDataByText(ElasticQueryWebClientRequestModel elasticQueryWebClientRequestModel);
}
