package com.microservices.base.elastic.query.service.api;

import com.microservices.base.elastic.query.service.business.ElasticQueryService;
import com.microservices.base.elastic.query.service.model.ElasticQueryServiceRequestModel;
import com.microservices.base.elastic.query.service.model.ElasticQueryServiceResponseModel;
import com.microservices.base.elastic.query.service.model.ElasticQueryServiceResponseModelV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@RestController //mixture of @Controller and @ResponseBody
//@ResponseBody is used to deserialize the java object to json based response
//@RequestBody is used to deserialize json into java object
//@Valid -> checks for validity of attributes inside the bean object, if any of the attribute is non-null or not empty, it validates the entire request object with that.
            //Process validations for all validation annotations on the bean object.
@RequestMapping(value = "/documents", produces = "application/vnd.api.v1+json") //JSON API with custom vendor media type
public class ElasticDocumentController {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticDocumentController.class);
    private final ElasticQueryService queryService;

    public ElasticDocumentController(ElasticQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/v1")
    public @ResponseBody ResponseEntity<List<ElasticQueryServiceResponseModel>> getAllDocuments(){
        List<ElasticQueryServiceResponseModel> response = queryService.getAllDocuments();
        LOG.info("Elasticsearch returned {} no of documents ", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<ElasticQueryServiceResponseModel> getDocumentById(@PathVariable("id") @NotEmpty String id){
        ElasticQueryServiceResponseModel elasticQueryServiceResponseModel = queryService.getDocumentById(id);
        LOG.info("Elasticsearch returned document with id {} ", id);
        return ResponseEntity.ok(elasticQueryServiceResponseModel);
    }

    //Additional level of complexity for maintaining the version changes because of a major breaking change in ElasticQueryServiceRequestModel
    //we had to create a new version, because we couldn't be forward compatible
    //This change has to be included both in the docs and the api definitions
    @GetMapping(value = "/{id}", produces = "application/vnd.api.v2+json") //note the url remains the same, for switching from v1 to v2, client will just change the accept header and not the url
    public @ResponseBody ResponseEntity<ElasticQueryServiceResponseModelV2> getDocumentByIdV2(@PathVariable("id") @NotEmpty String id){
        ElasticQueryServiceResponseModel elasticQueryServiceResponseModel = queryService.getDocumentById(id);
        LOG.info("Elasticsearch returned document with id {} ", id);
        ElasticQueryServiceResponseModelV2 elasticQueryServiceResponseModelV2 = getV2Model(elasticQueryServiceResponseModel);
        return ResponseEntity.ok(elasticQueryServiceResponseModelV2);
    }

    @PostMapping("/get-document-by-text")
    public @ResponseBody ResponseEntity<List<ElasticQueryServiceResponseModel>> getDocumentByText(@RequestBody @Valid ElasticQueryServiceRequestModel elasticQueryServiceRequestModel){
        List<ElasticQueryServiceResponseModel> response = queryService.getDocumentByText(elasticQueryServiceRequestModel.getText());
        LOG.info("Elasticsearch returned {} no of documents received with text {} ", response.size(), elasticQueryServiceRequestModel.getText());
        return ResponseEntity.ok(response);
    }

    private ElasticQueryServiceResponseModelV2 getV2Model(ElasticQueryServiceResponseModel responseModel){
        ElasticQueryServiceResponseModelV2 responseModelV2 = ElasticQueryServiceResponseModelV2.builder()
                .id(Long.parseLong(responseModel.getId()))
                .userId(responseModel.getUserId())
                .text(responseModel.getText())
                .textV2("Version 2 text")
                .build();
        responseModelV2.add(responseModel.getLinks());
        return responseModelV2;
    }
}
