package com.polarbookshop.orderservice.infrastructure.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;

/**
 * This configuration class is used to enable
 * R2DBC auditing via annotation.
 */
@Configuration
@EnableR2dbcAuditing
public class DataConfig {
}
