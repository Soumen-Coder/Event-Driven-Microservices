package com.microservices.base.elastic.query.service.api;

import com.microservices.base.elastic.query.service.business.ElasticQueryService;
import com.microservices.base.elastic.query.service.model.ElasticQueryServiceRequestModel;
import com.microservices.base.elastic.query.service.model.ElasticQueryServiceResponseModel;
import com.microservices.base.elastic.query.service.model.ElasticQueryServiceResponseModelV2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController //mixture of @Controller and @ResponseBody
//@ResponseBody -> is used to serialize the java response object to json, but with @RestController, we don't need to explicitly add a @ResponseBody as we did here in this controller. It was used just to show how it can used in code.
//@RequestBody is used to deserialize json in httpRequest to an object of ElasticQueryServiceRequestModel automatically
//@Valid -> checks for validity of attributes inside the bean object, if any of the attribute is non-null or not empty, it validates the entire request object with that.
            //Process validations for all validation annotations on the bean object.
//Adding media-types on the class level, because it can be easily overridden in the methods
@RequestMapping(value = "/documents", produces = "application/vnd.api.v1+json") //JSON API with custom vendor media type
public class ElasticDocumentController {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticDocumentController.class);
    private final ElasticQueryService queryService;

    @Value("${server.port}")
    private String port;

    public ElasticDocumentController(ElasticQueryService queryService) {
        this.queryService = queryService;
    }

    //Spring-docs -> Swagger Annotations below
    @Operation(summary = "Get all elastic documents.") //Sets the general description of the methods using summary
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response.", content = {
                    @Content(mediaType = "application/vnd.api.v1+json",
                            schema = @Schema(implementation = ElasticQueryServiceResponseModel.class) //Response Model Schema for different response type status code
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Not Found."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("")
    public @ResponseBody ResponseEntity<List<ElasticQueryServiceResponseModel>> getAllDocuments(){
        List<ElasticQueryServiceResponseModel> response = queryService.getAllDocuments();
        LOG.info("Elasticsearch returned {} no of documents ", response.size());
        return ResponseEntity.ok(response); //ok method will set HttpStatusCode as "200"
    }

    @Operation(summary = "Get elastic document by id.") //Sets the general description of the methods using summary
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response.", content = {
                    @Content(mediaType = "application/vnd.api.v1+json",
                            schema = @Schema(implementation = ElasticQueryServiceResponseModel.class) //Response Model Schema for different response type status code
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Not Found."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<ElasticQueryServiceResponseModel> getDocumentById(@PathVariable("id") @NotEmpty String id){
        //Mock Object -> ElasticQueryServiceResponseModel elasticQueryServiceResponseModel = ElasticQueryServiceRequestModel.builder().id(id).build();
        ElasticQueryServiceResponseModel elasticQueryServiceResponseModel = queryService.getDocumentById(id);
        LOG.info("Elasticsearch returned document with id {} ", id);
        return ResponseEntity.ok(elasticQueryServiceResponseModel);
    }

    //we had to create a new version, because we couldn't be forward compatible here
    //This change has to be included both in the docs and the api definitions
    @Operation(summary = "Get elastic document by id.") //Sets the general description of the methods using summary
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response.", content = {
                    @Content(mediaType = "application/vnd.api.v2+json",
                            schema = @Schema(implementation = ElasticQueryServiceResponseModelV2.class)
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Not Found."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping(value = "/{id}", produces = "application/vnd.api.v2+json") //note the url remains the same, other methods still serving the same old v1(see the produces in the class level, overridden by the methods of the class) url, so here we partially updated a single method to a new version for switching from v1 to v2, client will just change the accept header and not the url
    public @ResponseBody ResponseEntity<ElasticQueryServiceResponseModelV2> getDocumentByIdV2(@PathVariable("id") @NotEmpty String id){
        ElasticQueryServiceResponseModel elasticQueryServiceResponseModel = queryService.getDocumentById(id);
        LOG.info("Elasticsearch returned document with id {} ", id);
        ElasticQueryServiceResponseModelV2 elasticQueryServiceResponseModelV2 = getV2Model(elasticQueryServiceResponseModel);
        return ResponseEntity.ok(elasticQueryServiceResponseModelV2);
    }

    @Operation(summary = "Get elastic documents by text.") //Description by using summary
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response.", content = {
                    @Content(mediaType = "application/vnd.api.v1+json",
                            schema = @Schema(implementation = ElasticQueryServiceResponseModel.class)
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Not Found."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping("/get-document-by-text")
    //@Valid annotation -> process the annotations like @NotEmpty, NonNull, etc on the request objects
    public @ResponseBody ResponseEntity<List<ElasticQueryServiceResponseModel>> getDocumentByText(@RequestBody @Valid ElasticQueryServiceRequestModel elasticQueryServiceRequestModel){
        //Mock Object -> List<ElasticQueryServiceResponseModel> response = new ArrayList<>();
        //ElasticQueryServiceResponseModel elasticQueryServiceResponseModel = ElasticQueryServiceResponseModel.builder().text(elasticQueryServiceRequestModel.getText());
        //response.add(elasticQueryServiceResponseModel);
        List<ElasticQueryServiceResponseModel> response = queryService.getDocumentByText(elasticQueryServiceRequestModel.getText()); //We could have used @PathVariable here as well, but we want to show the @PostMapping and how to deserialize the json to java object here
        LOG.info("Elasticsearch returned {} no of documents received with text {} on port {}", response.size(), elasticQueryServiceRequestModel.getText(), port);
        return ResponseEntity.ok(response);
    }

    //Mapping of ElasticQueryServiceResponseModel to the new ElasticQueryServiceResponseModelV2 setting the new field as well and parsing the id to Long from String
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

//OpenAPI Specification -> (OAS) defines a standard, language-agnostic interface to the RESTful APIs which allows both humans and computers to discover
//and understand the capabilities of the service, without access to code, docs or through network traffic inspection.

//Spring docs adds the OpenAPI Specification to a Spring Boot Application comes with a single dependency -> springdoc-openapi-ui
//Uses Swagger3 as the implementation
//By including the above dependency, it automates the generation of the API documentation(via various swagger annotations like @ApiResponse, @Operation)
//It supports various formats like JSON, YAML and HTML formats.
//Finally thanks to Swagger3, Spring doc also supports Content Negotiation(Accept Headers) Versioning(application/vnd.api.v1+json) which was not possible with swagger2 version