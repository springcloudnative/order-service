package com.polarbookshop.orderservice.infrastructure.configuration;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

/**
 * This class is used to configure a WebClient bean
 * with the base URL provided by ClientProperties.
 */
@Configuration
public class ClientConfig {

    @Bean
    WebClient webClient(ClientProperties clientProperties,
                        WebClient.Builder webClientBuilder) {

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2_000)
                .doOnConnected(connection ->
                        connection.addHandlerLast(new ReadTimeoutHandler(2))
                                .addHandlerLast(new WriteTimeoutHandler(2)));

        // Configures the WebClient base URL to the Catalog Service URL defined
        // as a custom property
        return webClientBuilder
                .baseUrl(clientProperties.catalogServiceUri.toString())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
