package com.microservices.base.elastic.query.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor //used to construct java object while deserialize json
@AllArgsConstructor //required by Builder
public class ElasticQueryServiceResponseModel extends RepresentationModel<ElasticQueryServiceResponseModel> {
    private String id;
    private Long userId;
    private String text;
    private String textV2; // think if this is a breaking change? actually no, because of the extensible nature of json, by adding a field, we are okay on the client side and deserialization will be done correctly
    //if any client need this field, then client can change their code, to include this field during deserialization process
    //This change doesn't require a new creation of a version
    //We will deploy the changes and mention them in the documentation
    //This is enough safe and easy for both server and client
    //Now let's say we want to change the type of id field to Long and also remove the createdAt field, then it will be breaking change and we have to create a new version
    private LocalDateTime createdAt;
}
