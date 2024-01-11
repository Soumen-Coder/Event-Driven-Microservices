package com.microservices.base.elastic.query.web.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
//You may think these are the same models in the query service so couldn't we reuse them here?
//But trust me, It is not a good idea
//Because those classes will be used in the different part of the project and will be changed for different reasons for different times.
//And using a shared class in that case wouldn't be a good idea.
@Data
@Builder
@NoArgsConstructor //Spring requires a no args constructor to create java object during deserialize json to java object in httpBody
//During deserialization of JSON to Java, spring first create java object first with no-args constructor and then populate the obj with values in the json
@AllArgsConstructor //required by Builder by default
//Only with NoArgsConstructor, the application won;t compile, need an all args constructor as well
//Set only two fields, since we only use these two fields to search
public class ElasticQueryWebClientRequestModel {
    private String id;
    @NotEmpty // Validation for the java bean using hibernate validator -> Non-Null + no empty string, size of string>0
    private String text;
}
