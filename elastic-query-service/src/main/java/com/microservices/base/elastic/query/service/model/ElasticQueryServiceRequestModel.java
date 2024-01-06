package com.microservices.base.elastic.query.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor //Spring requires a no args constructor to create java object during deserialize json to java object in httpBody
//During deserialization of JSON to Java, spring first create java object first with no-args constructor and then populate the obj with values in the json
@AllArgsConstructor //required by Builder by default
//Only with NoArgsConstructor, the application won;t compile, need an all args constructor as well
public class ElasticQueryServiceRequestModel {
    private String id;
    @NotEmpty // Validation for the java bean using hibernate validator -> Non-Null + no empty string, size of string>0
    private String text;
}
