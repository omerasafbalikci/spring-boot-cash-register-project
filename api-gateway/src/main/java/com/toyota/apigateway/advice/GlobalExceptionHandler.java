package com.toyota.apigateway.advice;

import com.toyota.apigateway.utilities.exception.UnauthorizedException;
import com.toyota.apigateway.utilities.exception.UnexpectedException;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

/**
 * Global exception handler for handling exceptions in a reactive web application.
 * This handler manages Unauthorized and Unexpected exceptions specifically and provides
 * appropriate HTTP responses.
 */

@Component
@Order(-2)
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {
    /**
     * Constructs a new GlobalExceptionHandler with the specified error attributes, web properties, and application context.
     *
     * @param errorAttributes    the error attributes
     * @param webProperties      the web properties
     * @param applicationContext the application context
     */
    public GlobalExceptionHandler(ErrorAttributes errorAttributes, WebProperties webProperties, ApplicationContext applicationContext) {
        super(errorAttributes, webProperties.getResources(), applicationContext);
        setMessageWriters(ServerCodecConfigurer.create().getWriters());
    }

    /**
     * Provides the routing function to route requests to the appropriate error handling method.
     *
     * @param errorAttributes the error attributes
     * @return the router function
     */
    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    /**
     * Renders the error response based on the exception type.
     *
     * @param serverRequest the server request
     * @return the server response
     */
    private Mono<ServerResponse> renderErrorResponse(ServerRequest serverRequest) {
        Throwable throwable = getError(serverRequest);
        if (throwable instanceof UnauthorizedException) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                    .bodyValue(new ErrorResponse(HttpStatus.UNAUTHORIZED, throwable.getMessage(), serverRequest.path()));
        } else if (throwable instanceof UnexpectedException) {
            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .bodyValue(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, throwable.getMessage(), serverRequest.path()));
        }
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .bodyValue(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected Exception", serverRequest.path()));
    }
}
