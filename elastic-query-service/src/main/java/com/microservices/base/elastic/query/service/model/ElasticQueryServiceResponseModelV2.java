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
public class ElasticQueryServiceResponseModelV2 extends RepresentationModel<ElasticQueryServiceResponseModelV2> {
    private Long id; //Introducing a breaking change
    private Long userId;
    private String text;
    private String textV2; //not a breaking if added solely
    //removal of the createdAt, a breaking change
}
