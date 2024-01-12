package com.microservices.base.elastic.query.web.client.config;

import com.microservices.base.config.ElasticQueryWebClientConfigData;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@Primary //Use this implementation instead of other implementation that comes with spring
//Primary annotation gives implementations priority to be loaded by spring
public class ElasticQueryServiceInstanceListSupplierConfig implements ServiceInstanceListSupplier {
    private final ElasticQueryWebClientConfigData.WebClient webClientConfig;

    public ElasticQueryServiceInstanceListSupplierConfig(ElasticQueryWebClientConfigData webClientConfig) {
        this.webClientConfig = webClientConfig.getWebClient();
    }

    @Override
    public String getServiceId() {
        return webClientConfig.getServiceId();
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        //Flux is a reactive stream that can emit 0...N items
        return Flux.just(
                webClientConfig.getInstances()
                        .stream()
                        .map(instance ->
                                new DefaultServiceInstance(
                                        instance.getId(),
                                        getServiceId(),
                                        instance.getHost(),
                                        instance.getPort(),
                                        false
                                )).collect(Collectors.toList()));
    }
}
