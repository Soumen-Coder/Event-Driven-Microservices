package com.microservices.base.kafka.admin.exception;

/**
 *Exception class for Kafka Client error situations.
 */
public class KafkaClientException extends RuntimeException{
    KafkaClientException(){
    }

    public KafkaClientException(String message){
        super(message);
    }

    public KafkaClientException(String message, Throwable cause){
        super(message, cause);
    }
}
