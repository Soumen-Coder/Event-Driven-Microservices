package com.microservices.base.elastic.query.service.model.assembler;

import com.microservices.base.elastic.model.index.impl.TwitterIndexModel;
import com.microservices.base.elastic.query.service.api.ElasticDocumentController;
import com.microservices.base.elastic.query.service.model.ElasticQueryServiceResponseModel;
import com.microservices.base.elastic.query.service.transformer.ElasticToResponseModelTransformer;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ElasticQueryServiceResponseModelAssembler extends RepresentationModelAssemblerSupport<TwitterIndexModel, ElasticQueryServiceResponseModel> {
    private final ElasticToResponseModelTransformer transformer;

    public ElasticQueryServiceResponseModelAssembler(ElasticToResponseModelTransformer transformer) {
        super(ElasticDocumentController.class, ElasticQueryServiceResponseModel.class);
        this.transformer = transformer;
    }

    @Override
    public ElasticQueryServiceResponseModel toModel(TwitterIndexModel twitterIndexModel) {
        ElasticQueryServiceResponseModel response = transformer.getResponseModel(twitterIndexModel);
        response.add(
                linkTo(methodOn(ElasticDocumentController.class)
                        .getDocumentById(twitterIndexModel.getId()))
                        .withSelfRel());
        response.add(
                linkTo(ElasticDocumentController.class)
                        .withRel("documents"));
        return response;
    }

    public List<ElasticQueryServiceResponseModel> toModels(List<TwitterIndexModel> twitterIndexModels) {
        return twitterIndexModels.stream().map(this::toModel).collect(Collectors.toList());
    }
}
