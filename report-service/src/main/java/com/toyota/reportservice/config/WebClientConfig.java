package com.toyota.reportservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class for setting up a {@link WebClient} bean with load balancing capabilities.
 */

@Configuration
public class WebClientConfig {
    /**
     * Creates a {@link WebClient.Builder} bean configured with load balancing and increased memory size for data buffering.
     *
     * @return a configured {@link WebClient.Builder} instance
     */
    @Bean
    @LoadBalanced
    public WebClient.Builder webClient() {
        final int size = 16 * 1024 * 1024;
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs().maxInMemorySize(size))
                .build();
        return WebClient.builder().exchangeStrategies(strategies);
    }
}
