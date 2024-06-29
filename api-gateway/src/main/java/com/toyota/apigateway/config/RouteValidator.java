package com.toyota.apigateway.config;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

/**
 * Component for validating routes to determine if they require security.
 */

@Component
public class RouteValidator {
    /**
     * List of open API endpoints that do not require security.
     */
    public static final List<String> openApiEndpoints = List.of(
            "/auth/login", "/auth/change-password", "/auth/forgot-password", "/auth/reset-password", "/auth/logout"
    );

    /**
     * Predicate to check if a request is secured or not.
     * Returns true if the request does not match any of the open API endpoints.
     */
    public Predicate<ServerHttpRequest> isSecured =
            serverHttpRequest -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> serverHttpRequest.getURI().getPath().contains(uri));
}
