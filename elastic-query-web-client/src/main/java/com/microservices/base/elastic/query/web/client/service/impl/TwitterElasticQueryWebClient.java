package com.microservices.base.elastic.query.web.client.service.impl;

import com.microservices.base.config.ElasticQueryWebClientConfigData;
import com.microservices.base.elastic.query.web.client.exception.ElasticQueryWebClientException;
import com.microservices.base.elastic.query.web.client.model.ElasticQueryWebClientRequestModel;
import com.microservices.base.elastic.query.web.client.model.ElasticQueryWebClientResponseModel;
import com.microservices.base.elastic.query.web.client.service.ElasticQueryWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class TwitterElasticQueryWebClient implements ElasticQueryWebClient {
    private static final Logger LOG = LoggerFactory.getLogger(TwitterElasticQueryWebClient.class);
    private final WebClient.Builder webClientBuilder; //from spring framework reactive library
    private final ElasticQueryWebClientConfigData elasticQueryWebClientConfigData;

    //Qualifier annotation to inject a specific implementation of an interface
    //We need this where it is present in the below line because there is a default WebClient.Builder implementation(DefaultWebClientBuilder.class) of WebClient.Builder comes by spring library but here we wanted to inject our customized builder(name matters, spring searches for a bean definition with that name)
    public TwitterElasticQueryWebClient(@Qualifier("webClientBuilder") WebClient.Builder webClientBuilder, ElasticQueryWebClientConfigData elasticQueryWebClientConfigData) {
        this.webClientBuilder = webClientBuilder;
        this.elasticQueryWebClientConfigData = elasticQueryWebClientConfigData;
    }

    @Override
    public List<ElasticQueryWebClientResponseModel> getDataByText(ElasticQueryWebClientRequestModel requestModel) {
        LOG.info("Querying by text {}", requestModel.getText());
        return getWebClient(requestModel)
                .bodyToFlux(ElasticQueryWebClientResponseModel.class) // since this method will return more than 1 data hence we used bodyToFlux
                .collectList()
                .block(); //block -> get the response synchronously(immediately)
        //without block method webClient's response doesn't return immediately, and we need to create a callback method which will be called later asynchronously
    }

    private WebClient.ResponseSpec getWebClient(ElasticQueryWebClientRequestModel requestModel) {
        return webClientBuilder
                .build()
                .method(HttpMethod.valueOf(elasticQueryWebClientConfigData.getQueryByText().getMethod()))
                .uri(elasticQueryWebClientConfigData.getQueryByText().getUri())
                .accept(MediaType.valueOf(elasticQueryWebClientConfigData.getQueryByText().getAccept()))
                //BodyInserters is an abstract class responsible for populating a reactive http response output message body with a given output message and the context
                //and the publisher that is used with fromPublisher is a reactive component and provides sequenced elements that are potentially unbounded
                //Since WebClient is a reactive client and works with reactive types, in this reactive webclient we need to work with reactive types like
                //Mono for a single data and flux for a list of data
                .body(BodyInserters.fromPublisher(Mono.just(requestModel), createParameterizedTypeReference())) //this way we pass the request object using the parameterized type reference to the webClient
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus.equals(HttpStatus.UNAUTHORIZED),
                        clientResponse -> Mono.just(new BadCredentialsException("Not Authenticated!")))
                .onStatus(
                        HttpStatus::is4xxClientError,
                        clientResponse -> Mono.just(new ElasticQueryWebClientException(clientResponse.statusCode().getReasonPhrase())))
                .onStatus(
                        HttpStatus::is5xxServerError,
                        clientResponse -> Mono.just(new Exception(clientResponse.statusCode().getReasonPhrase())));
    }

    private <T>ParameterizedTypeReference<T> createParameterizedTypeReference() {
        return new ParameterizedTypeReference<T>() {
        };
    }
}
