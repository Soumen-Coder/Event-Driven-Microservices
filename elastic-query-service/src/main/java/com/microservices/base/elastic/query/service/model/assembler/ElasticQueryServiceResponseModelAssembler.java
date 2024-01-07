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
        super(ElasticDocumentController.class, ElasticQueryServiceResponseModel.class); // This is to tell assembler that we are using controller and response model for hateoas
        this.transformer = transformer;
    }

    @Override
    public ElasticQueryServiceResponseModel toModel(TwitterIndexModel twitterIndexModel) {
        ElasticQueryServiceResponseModel response = transformer.getResponseModel(twitterIndexModel);
        response.add(
                //WebMvcLinkBuilder class's -> linkTo and methodOn are the static method, that are used here, which can be used with RepresentationModelAssemblerSupport
                //LinkBuilderSupport class's -> withSelfRel and withRel are the static method, that are used here, which can be used with RepresentationModelAssemblerSupport
                linkTo(methodOn(ElasticDocumentController.class)
                        .getDocumentById(twitterIndexModel.getId()))
                        .withSelfRel()); // link to own self, getDocumentById
        response.add(
                linkTo(ElasticDocumentController.class)
                        .withRel("documents")); // link to fetch all documents by document's base path
        return response;
    }

    public List<ElasticQueryServiceResponseModel> toModels(List<TwitterIndexModel> twitterIndexModels) {
        return twitterIndexModels.stream().map(this::toModel).collect(Collectors.toList());
    }
}
