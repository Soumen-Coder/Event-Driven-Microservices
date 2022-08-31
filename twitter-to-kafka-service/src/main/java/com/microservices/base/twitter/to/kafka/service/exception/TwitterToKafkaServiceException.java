package com.microservices.base.twitter.to.kafka.service.exception;

public class TwitterToKafkaServiceException extends RuntimeException{
    TwitterToKafkaServiceException(){
        super();
    }

    public TwitterToKafkaServiceException(String message){
        super(message);
    }

    TwitterToKafkaServiceException(String message, Throwable cause){
        super(message,cause);
    }
}
