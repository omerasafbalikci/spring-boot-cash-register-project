package com.toyota.salesservice.config;

import com.toyota.salesservice.domain.CampaignType;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for ModelMapper.
 * This class is responsible for creating and configuring the ModelMapper bean.
 */

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Registering custom converter for CampaignType
        Converter<String, CampaignType> toEnumConverter = context -> CampaignType.valueOf(context.getSource());

        modelMapper.addConverter(toEnumConverter);
        modelMapper.getConfiguration()
                .setAmbiguityIgnored(true)
                .setMatchingStrategy(MatchingStrategies.STRICT);

        return modelMapper;
    }
}
