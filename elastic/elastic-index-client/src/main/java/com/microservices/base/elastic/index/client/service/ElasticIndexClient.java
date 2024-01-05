package com.microservices.base.elastic.index.client.service;

import com.microservices.base.elastic.model.index.IndexModel;

import java.util.List;

public interface ElasticIndexClient<T extends IndexModel> {
    List<String> save (List<T> documents);
}
