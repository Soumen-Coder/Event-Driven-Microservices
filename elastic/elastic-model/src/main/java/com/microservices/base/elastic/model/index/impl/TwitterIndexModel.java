package com.microservices.base.elastic.model.index.impl;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.microservices.base.elastic.model.index.IndexModel;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Data // we don't need to override the getId() method from the interface IndexModel, since lombok does that in the compiled class for us.
@Builder
@Document(indexName = "#{elasticConfigData.indexName}")// indicates this class is a candidate for mapping to elasticsearch, indexName is parsed using SpEL Template Expression
public class TwitterIndexModel implements IndexModel {
    @JsonProperty
    private String id;
    @JsonProperty
    private Long userId;
    @JsonProperty
    private String text;

    //@Field -> This converts date type(LocalDateTime) to elastic date during indexing operation, temporalAccessor should have field annotation or custom converters
    //Sets the field type
    //formats date using the provided pattern
    @Field(type = FieldType.Date, format= DateFormat.custom, pattern="uuuu-MM-dd'T'HH:mm:ssZZ") //"u" instead of "y" represent year for custom elastic search date, custom date pattern because we used custom pattern in Date in AVRO
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="uuuu-MM-dd'T'HH:mm:ssZZ") //parsing a json to this object using the provided shape
    @JsonProperty
    private LocalDateTime createdAt;
}
