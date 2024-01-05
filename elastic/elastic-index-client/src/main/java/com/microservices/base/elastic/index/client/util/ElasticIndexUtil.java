package com.microservices.base.elastic.index.client.util;

import com.microservices.base.elastic.model.index.IndexModel;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
@Component
//elastic-index-client package is used for indexing operations.
//upper bound for our generic definition - Relax restrictions and allow subtypes like TwitterIndexModel.class
//In this class we will convert the list of TwitterIndexModel Objects to a List of IndexQueries
public class ElasticIndexUtil<T extends IndexModel> {
    public List<IndexQuery> getIndexQueries(List<T> documents){
        return documents.stream()
                .map(document -> new IndexQueryBuilder()
                        .withId(document.getId()) // here we needed getId() method and that is the reason we used getId() in the interface definition
                        .withObject(document)
                        .build()
                ).collect(Collectors.toList());
    }
}
