package com.polarbookshop.orderservice.infrastructure.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;
import java.net.URI;

/**
 * This class is used to externalize the configuration
 * for the URL of other backing services in the application.
 */
@Configuration
@ConfigurationProperties(prefix = "polar")
@Data
public class ClientProperties {

    @NotNull
    public URI catalogServiceUri;   // Property for specifying the Catalog Service URL. It cannot be null

    long clientTimeOut; // Property for specifying the client timeout.
}
