package com.microservices.base.elastic.query.web.client.config;

import com.microservices.base.config.ElasticQueryWebClientConfigData;
import com.microservices.base.config.UserConfigData;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.util.concurrent.TimeUnit;

@Configuration
@LoadBalancerClient(value = "elastic-query-service", configuration = ElasticQueryServiceInstanceListSupplierConfig.class) //service id is used here which is set in the property of elastic-query-web-client
public class WebClientConfig {
    private final ElasticQueryWebClientConfigData.WebClient elasticQueryWebClientConfigData;
    private final UserConfigData userConfigData;

    public WebClientConfig(ElasticQueryWebClientConfigData.WebClient elasticQueryWebClientConfigData, UserConfigData userConfigData) {
        this.elasticQueryWebClientConfigData = elasticQueryWebClientConfigData;
        this.userConfigData = userConfigData;
    }

    @LoadBalanced
    @Bean("webClientBuilder") // created a bean of Builder of WebClient directly and not WebClient because it is required to use @LoadBalanced option which works only on a Builder and not on WebClient directly
    //Usage of Qualifier annotation ->we  used a qualifier in the bean annotation
        // so that we can use that name "webClientBuilder" with Qualifier annotation to pick up this builder implementation when injecting it
    WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .filter(ExchangeFilterFunctions
                        .basicAuthentication(userConfigData.getUsername(), userConfigData.getPassword()))
                .baseUrl(elasticQueryWebClientConfigData.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, elasticQueryWebClientConfigData.getContentType())
                .defaultHeader(HttpHeaders.ACCEPT, elasticQueryWebClientConfigData.getAcceptType())
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(getTcpClient()))) // we don't need to but we will set TcpClient explicitly to show the options that can be overriden
                //codecs method to set the maxInMemorySize
                .codecs(clientCodecConfigurer ->
                        clientCodecConfigurer
                                .defaultCodecs()
                                .maxInMemorySize(elasticQueryWebClientConfigData.getMaxInMemorySize())); //10MB size // if not set, we will get error, when browsing large data on browser, mind it!!!
    }

    //Returns TcpClient object for our webClient
    private TcpClient getTcpClient() {
        return TcpClient.create() // from project reactor library
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, elasticQueryWebClientConfigData.getConnectTimeoutMs())
                .doOnConnected(connection -> {
                    connection.addHandlerLast(
                            new ReadTimeoutHandler(elasticQueryWebClientConfigData.getReadTimeoutMs(), //readTimeOut value here explicitly
                                    TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(
                            new WriteTimeoutHandler(elasticQueryWebClientConfigData.getWriteTimeoutMs(), //writeTimeOut value here explicitly
                                    TimeUnit.MILLISECONDS));
                });
    }
}
