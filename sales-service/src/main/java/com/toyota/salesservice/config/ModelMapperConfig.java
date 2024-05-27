package com.toyota.salesservice.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for ModelMapper.
 * This class is responsible for creating and configuring the ModelMapper bean.
 */

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper getModelMapper() {
        return new ModelMapper();
    }
}
