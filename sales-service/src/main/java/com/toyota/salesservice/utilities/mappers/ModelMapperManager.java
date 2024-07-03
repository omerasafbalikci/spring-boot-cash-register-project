package com.toyota.salesservice.utilities.mappers;

import com.toyota.salesservice.domain.CampaignType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class that provides configuration for ModelMapper to map between different object models.
 * This class is used to configure the ModelMapper for both response and request mapping strategies.
 */

@Service
@AllArgsConstructor
@NoArgsConstructor
public class ModelMapperManager implements ModelMapperService {
    @Autowired
    private ModelMapper modelMapper;

    /**
     * Configures the ModelMapper for response mapping.
     * Sets the matching strategy to "LOOSE" and ignores ambiguities.
     *
     * @return the configured ModelMapper instance
     */
    @Override
    public ModelMapper forResponse() {
        this.modelMapper.getConfiguration()
                .setAmbiguityIgnored(true)
                .setMatchingStrategy(MatchingStrategies.LOOSE);

        return this.modelMapper;
    }

    /**
     * Configures the ModelMapper for request mapping.
     * Sets the matching strategy to "STANDARD" and ignores ambiguities.
     *
     * @return the configured ModelMapper instance
     */
    @Override
    public ModelMapper forRequest() {
        this.modelMapper.getConfiguration()
                .setAmbiguityIgnored(true)
                .setMatchingStrategy(MatchingStrategies.STANDARD);

        return this.modelMapper;
    }
}
