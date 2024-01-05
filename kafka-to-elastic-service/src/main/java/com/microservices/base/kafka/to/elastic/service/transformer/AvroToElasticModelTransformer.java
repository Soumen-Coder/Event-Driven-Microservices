package com.microservices.base.kafka.to.elastic.service.transformer;

import com.microservices.base.elastic.model.index.impl.TwitterIndexModel;
import com.microservices.base.kafka.avro.model.TwitterAvroModel;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;
//Converts a TwitterAvroModel object to TwitterIndexModel(Elastic Model)
@Component
public class AvroToElasticModelTransformer {
    public List<TwitterIndexModel> getElasticModel(List<TwitterAvroModel> avroModels){
        return avroModels.stream()
                .map(twitterAvroModel -> TwitterIndexModel
                        .builder()
                        .id(String.valueOf(twitterAvroModel.getId()))
                        .userId(twitterAvroModel.getUserId())
                        .text(twitterAvroModel.getText())
                        .createdAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(twitterAvroModel.getCreatedAt()),
                                ZoneId.systemDefault())) //Convert Long variable to a LocalDateTime Instant
                        .build()
                ).collect(Collectors.toList());
    }
}
