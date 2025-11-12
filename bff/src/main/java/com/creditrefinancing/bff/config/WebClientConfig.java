package com.creditrefinancing.bff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class WebClientConfig {

    @Value("${services.simulation.base-url:http://localhost:8081}")
    private String simulationServiceUrl;
    
    @Value("${services.proposal.base-url:http://localhost:8082}")
    private String proposalServiceUrl;
    
    @Value("${services.formalization.base-url:http://localhost:8083}")
    private String formalizationServiceUrl;
    
    @Value("${services.after-sales.base-url:http://localhost:8084}")
    private String afterSalesServiceUrl;
    
    @Value("${webclient.connection-timeout:5000}")
    private int connectionTimeout;
    
    @Value("${webclient.read-timeout:10000}")
    private int readTimeout;
    
    @Value("${webclient.write-timeout:10000}")
    private int writeTimeout;
    
    @Value("${webclient.max-in-memory-size:10485760}")
    private int maxMemorySize;

    private ReactorClientHttpConnector createReactorClientHttpConnector() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
                .responseTimeout(Duration.ofMillis(readTimeout))
                .doOnConnected(conn -> 
                    conn.addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(writeTimeout, TimeUnit.MILLISECONDS))
                );
        
        return new ReactorClientHttpConnector(httpClient);
    }

    @Bean
    @Qualifier("simulationWebClient")
    public WebClient simulationWebClient() {
        return WebClient.builder()
                .baseUrl(simulationServiceUrl)
                .clientConnector(createReactorClientHttpConnector())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .filter(loggingFilter("Simulation Service"))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxMemorySize))
                .build();
    }

    @Bean
    @Qualifier("proposalWebClient")
    public WebClient proposalWebClient() {
        return WebClient.builder()
                .baseUrl(proposalServiceUrl)
                .clientConnector(createReactorClientHttpConnector())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .filter(loggingFilter("Proposal Service"))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxMemorySize))
                .build();
    }

    @Bean
    @Qualifier("formalizationWebClient")
    public WebClient formalizationWebClient() {
        return WebClient.builder()
                .baseUrl(formalizationServiceUrl)
                .clientConnector(createReactorClientHttpConnector())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .filter(loggingFilter("Formalization Service"))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxMemorySize))
                .build();
    }

    @Bean
    @Qualifier("afterSalesWebClient")
    public WebClient afterSalesWebClient() {
        return WebClient.builder()
                .baseUrl(afterSalesServiceUrl)
                .clientConnector(createReactorClientHttpConnector())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .filter(loggingFilter("After Sales Service"))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxMemorySize))
                .build();
    }

    private org.springframework.web.reactive.function.client.ExchangeFilterFunction loggingFilter(String serviceName) {
        return org.springframework.web.reactive.function.client.ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.debug("Request to {}: {} {}", serviceName, clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) -> 
                log.trace("Request Header {}: {}", name, values)
            );
            return reactor.core.publisher.Mono.just(clientRequest);
        }).andThen(org.springframework.web.reactive.function.client.ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.debug("Response from {}: {}", serviceName, clientResponse.statusCode());
            return reactor.core.publisher.Mono.just(clientResponse);
        }));
    }

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .clientConnector(createReactorClientHttpConnector())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-Service-Name", "credit-refinancing-bff")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxMemorySize));
    }
}