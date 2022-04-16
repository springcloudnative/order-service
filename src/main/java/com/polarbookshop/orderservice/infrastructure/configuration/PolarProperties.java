package com.polarbookshop.orderservice.infrastructure.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The @ConfigurationProperties annotation marks this class as a source
 * for configuration properties starting with the prefix "polar".
 * Spring Boot will try to map all properties with that prefix to fields in the record.
 *
 * Classes annotated with @ConfigurationProperties are standard Spring beans, so
 * they can be injected wherever we need them. Spring Boot initializes all the configuration
 * beans at startup and populates them with the data provided through any of the supported
 * configuration data sources.
 */
@ConfigurationProperties(prefix = "polar")
@Data
public class PolarProperties {

    String greeting;
}
