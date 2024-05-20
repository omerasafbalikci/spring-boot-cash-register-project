package com.toyota.productservice.utilities.mappers;

import org.modelmapper.ModelMapper;

/**
 * Interface for a service that provides configured instances of {@link ModelMapper}.
 * It is used to map between different object models in response and request scenarios.
 */
public interface ModelMapperService {
    /**
     * Provides a ModelMapper instance configured for response mapping.
     *
     * @return a configured ModelMapper instance for response mapping
     */

    ModelMapper forResponse();

    /**
     * Provides a ModelMapper instance configured for request mapping.
     *
     * @return a configured ModelMapper instance for request mapping
     */
    ModelMapper forRequest();
}
