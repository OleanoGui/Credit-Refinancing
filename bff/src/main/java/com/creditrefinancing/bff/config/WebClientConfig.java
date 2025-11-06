package com.creditrefinancing.bff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;
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
    
    @Value("${webclient.timeout.connection:5000}")
    private int connectionTimeoutMs;
    
    @Value("${webclient.timeout.read:30000}")
    private int readTimeoutMs;
    
    @Value("${webclient.timeout.write:30000}")
    private int writeTimeoutMs;
    
    @Value("${webclient.max-memory-size:1048576}") // 1MB default
    private int maxMemorySize;

    @Bean("simulationWebClient")
    public WebClient simulationWebClient() {
        log.info("Creating WebClient for Simulation Service: {}", simulationServiceUrl);
        
        return WebClient.builder()
                .baseUrl(simulationServiceUrl)
                .clientConnector(createReactorClientHttpConnector())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-Service-Name", "credit-refinancing-bff")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxMemorySize))
                .filter(loggingFilter("SimulationService"))
                .build();
    }

    @Bean("proposalWebClient")
    public WebClient proposalWebClient() {
        log.info("Creating WebClient for Proposal Service: {}", proposalServiceUrl);
        
        return WebClient.builder()
                .baseUrl(proposalServiceUrl)
                .clientConnector(createReactorClientHttpConnector())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-Service-Name", "credit-refinancing-bff")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxMemorySize))
                .filter(loggingFilter("ProposalService"))
                .build();
    }

    @Bean("formalizationWebClient")
    public WebClient formalizationWebClient() {
        log.info("Creating WebClient for Formalization Service: {}", formalizationServiceUrl);
        
        return WebClient.builder()
                .baseUrl(formalizationServiceUrl)
                .clientConnector(createReactorClientHttpConnector())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-Service-Name", "credit-refinancing-bff")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxMemorySize))
                .filter(loggingFilter("FormalizationService"))
                .build();
    }

    @Bean("afterSalesWebClient")
    public WebClient afterSalesWebClient() {
        log.info("Creating WebClient for After Sales Service: {}", afterSalesServiceUrl);
        
        return WebClient.builder()
                .baseUrl(afterSalesServiceUrl)
                .clientConnector(createReactorClientHttpConnector())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-Service-Name", "credit-refinancing-bff")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxMemorySize))
                .filter(loggingFilter("AfterSalesService"))
                .build();
    }

    /**
     * Creates a ReactorClientHttpConnector with custom timeout and connection settings
     */
    private ReactorClientHttpConnector createReactorClientHttpConnector() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeoutMs)
                .responseTimeout(Duration.ofMillis(readTimeoutMs))
                .doOnConnected(conn -> 
                    conn.addHandlerLast(new ReadTimeoutHandler(readTimeoutMs, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(writeTimeoutMs, TimeUnit.MILLISECONDS))
                );
        
        return new ReactorClientHttpConnector(httpClient);
    }

    /**
     * Creates a logging filter for WebClient requests and responses
     */
    private org.springframework.web.reactive.function.client.ExchangeFilterFunction loggingFilter(String serviceName) {
        return org.springframework.web.reactive.function.client.ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.debug("Request to {}: {} {}", serviceName, clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) -> 
                log.trace("Request Header {}: {}", name, values)
            );
            return reactor.core.publisher.Mono.just(clientRequest);
        }).andThen(org.springframework.web.reactive.function.client.ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.debug("Response from {}: {} {}", serviceName, clientResponse.statusCode(), clientResponse.statusCode().getReasonPhrase());
            clientResponse.headers().asHttpHeaders().forEach((name, values) -> 
                log.trace("Response Header {}: {}", name, values)
            );
            return reactor.core.publisher.Mono.just(clientResponse);
        }));
    }

    /**
     * Generic WebClient builder for custom services
     */
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
