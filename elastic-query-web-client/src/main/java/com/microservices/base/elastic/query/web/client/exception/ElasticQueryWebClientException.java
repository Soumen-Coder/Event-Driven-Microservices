package com.microservices.base.elastic.query.web.client.exception;
//Use this class to throw RuntimeException in the elastic-query-web-client
public class ElasticQueryWebClientException extends RuntimeException {
    public ElasticQueryWebClientException(){
        super();
    }

    public ElasticQueryWebClientException(String message){
        super(message);
    }

    public ElasticQueryWebClientException(String message, Throwable t){
        super(message, t);
    }
}
