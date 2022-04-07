package com.polarbookshop.orderservice.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * This class is used to configure a WebClient bean
 * with the base URL provided by ClientProperties.
 */
@Configuration
public class ClientConfig {

    @Bean
    WebClient webClient(ClientProperties clientProperties,
                        WebClient.Builder webClientBuilder) {

        // Configures the WebClient base URL to the Catalog Service URL defined
        // as a custom property
        return webClientBuilder
                .baseUrl(clientProperties.catalogServiceUri.toString())
                .build();
    }
}
