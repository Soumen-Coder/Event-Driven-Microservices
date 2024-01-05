package com.microservices.base.elastic.query.service.service;

import com.microservices.base.elastic.model.index.IndexModel;

import java.util.List;

public interface ElasticQueryClient<T extends IndexModel> {
    T getIndexModelById(String id);
    List<T> getIndexModelByText(String text);
    List<T> getAllIndexModels();
}
