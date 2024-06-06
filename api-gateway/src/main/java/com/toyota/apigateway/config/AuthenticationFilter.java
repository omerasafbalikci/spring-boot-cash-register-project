package com.toyota.apigateway.config;

import com.toyota.apigateway.utilities.exception.UnauthorizedException;
import com.toyota.apigateway.utilities.exception.UnexpectedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Authentication filter for validating JWT tokens and user roles.
 */

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    private final WebClient.Builder webClientBuilder;
    private final RouteValidator routeValidator;
    private final Logger logger = LogManager.getLogger(AuthenticationFilter.class);

    /**
     * Constructor for AuthenticationFilter.
     *
     * @param webClientBuilder WebClient.Builder instance for creating WebClient.
     * @param routeValidator   RouteValidator instance for validating routes.
     */
    public AuthenticationFilter(WebClient.Builder webClientBuilder, RouteValidator routeValidator) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
        this.routeValidator = routeValidator;
    }

    /**
     * Applies the authentication filter.
     *
     * @param config The filter configuration.
     * @return The gateway filter.
     */
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (this.routeValidator.isSecured.test(exchange.getRequest())) {
                String authHeader = extractToken(exchange.getRequest());
                if (authHeader == null) {
                    logger.warn("Bearer token is missing!");
                    throw new UnauthorizedException("Bearer token is missing");
                }

                Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
                assert route != null;
                String requiredRole = route.getMetadata().get("requiredRole").toString();
                String authenticationUrl = "http://authentication-authorization-service/auth/verify";
                
                logger.debug("Verifying user and role: {}", requiredRole);
                
                return this.webClientBuilder.build().get().uri(authenticationUrl)
                        .headers(headers -> headers.setBearerAuth(authHeader))
                        .retrieve()
                        .bodyToMono(Map.class)
                        .flatMap(result -> {
                            String routeId = route.getId();
                            if (result == null) {
                                logger.warn("Invalid bearer!");
                                return Mono.error(new UnauthorizedException("Invalid bearer token!"));
                            } else if (result.containsKey(requiredRole) || (routeId.equals("product-service") && !result.isEmpty())) {
                                logger.info("User authorized: {}", result.get("Username").toString());
                                return chain.filter(exchange.mutate().request(
                                        exchange.getRequest().mutate().header("Username", result.get("Username").toString())
                                                .build())
                                        .build());
                            } else {
                                logger.warn("User not authorized! User: {}", result.get("Username").toString());
                                return Mono.error(new UnauthorizedException("User not authorized!"));
                            }
                        })
                        .onErrorResume(WebClientResponseException.class, e -> {
                            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                                logger.warn("Invalid bearer token!");
                                throw new UnauthorizedException("Invalid bearer token!");
                            } else {
                                logger.warn("WebClient response exception occurred: {}", e.getMessage());
                                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                                throw new UnexpectedException("WebClient response exception occurred: " + e.getMessage());
                            }
                        });
            }
            return chain.filter(exchange);
        };
    }

    /**
     * Extracts the JWT token from the Authorization header.
     *
     * @param request The server HTTP request.
     * @return The JWT token if present, otherwise null.
     */
    private String extractToken(ServerHttpRequest request) {
        String authorizationHeader = request.getHeaders().getFirst("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    /**
     * Configuration class for AuthenticationFilter.
     */
    public static class Config {

    }
}
