package com.microservices.base.elastic.query.web.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor //used to construct java object while deserialize json
@AllArgsConstructor //required by Builder
//You may think these are the same models in the query service so couldn't we reuse them here?
//But trust me, It is not a good idea
//Because those classes will be used in the different part of the project and will be changed for different reasons for different times.
//And using a shared class in that case wouldn't be a good idea.
public class ElasticQueryWebClientResponseModel {
    //Defining all fields as ElasticQueryServiceResponseModel
    private String id;
    private Long userId;
    private String text;
    private LocalDateTime createdAt;
}