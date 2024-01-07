package com.microservices.base.elastic.query.service.business.impl;

import com.microservices.base.elastic.model.index.impl.TwitterIndexModel;
import com.microservices.base.elastic.query.service.business.ElasticQueryService;
import com.microservices.base.elastic.query.service.model.ElasticQueryServiceResponseModel;
import com.microservices.base.elastic.query.service.model.assembler.ElasticQueryServiceResponseModelAssembler;
import com.microservices.base.elastic.query.service.service.ElasticQueryClient;
//import com.microservices.base.elastic.query.service.transformer.ElasticToResponseModelTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TwitterElasticQueryService implements ElasticQueryService {
    private static final Logger LOG = LoggerFactory.getLogger(TwitterElasticQueryService.class);
    //private final ElasticToResponseModelTransformer elasticToResponseModelTransformer;
    private final ElasticQueryServiceResponseModelAssembler elasticQueryServiceResponseModelAssembler;
    private final ElasticQueryClient<TwitterIndexModel> elasticQueryClient;

    public TwitterElasticQueryService(ElasticQueryServiceResponseModelAssembler assembler, ElasticQueryClient<TwitterIndexModel> elasticQueryClient) {
        this.elasticQueryServiceResponseModelAssembler = assembler;
        this.elasticQueryClient = elasticQueryClient;
    }

    @Override
    public ElasticQueryServiceResponseModel getDocumentById(String id) {
        LOG.info("Querying elasticsearch by id {} ", id);
        //return elasticToResponseModelTransformer.toResponseModel(elasticQueryClient.getIndexModelById(id));
        return elasticQueryServiceResponseModelAssembler.toModel(elasticQueryClient.getIndexModelById(id));
    }

    @Override
    public List<ElasticQueryServiceResponseModel> getDocumentByText(String text) {
        LOG.info("Querying elasticsearch by text {} ", text);
        //return elasticToResponseModelTransformer.toResponseModels(elasticQueryClient.getIndexModelByText(text));
        return elasticQueryServiceResponseModelAssembler.toModels(elasticQueryClient.getIndexModelByText(text));
    }

    @Override
    public List<ElasticQueryServiceResponseModel> getAllDocuments() {
        LOG.info("Querying all documents in elasticsearch");
        //return elasticToResponseModelTransformer.toResponseModels(elasticQueryClient.getAllIndexModels());
        return elasticQueryServiceResponseModelAssembler.toModels(elasticQueryClient.getAllIndexModels());
    }
}
