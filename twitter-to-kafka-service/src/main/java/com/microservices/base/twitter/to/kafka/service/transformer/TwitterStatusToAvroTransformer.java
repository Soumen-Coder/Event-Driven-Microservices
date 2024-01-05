package com.microservices.base.twitter.to.kafka.service.transformer;

import com.microservices.base.kafka.avro.model.TwitterAvroModel;
import org.springframework.stereotype.Component;
import twitter4j.Status;
//Transforms the Status object from the twitter library(message) into a TwitterAvroModel, having strict schema before inserting into kafka
@Component
public class TwitterStatusToAvroTransformer {

    public TwitterAvroModel getTwitterAvroModelFromStatus(Status status) {
        return TwitterAvroModel
                .newBuilder()
                .setId(status.getId())
                .setUserId(status.getUser().getId())
                .setText(status.getText())
                .setCreatedAt(status.getCreatedAt().getTime()) // to convert date to long
                .build();
    }
}
