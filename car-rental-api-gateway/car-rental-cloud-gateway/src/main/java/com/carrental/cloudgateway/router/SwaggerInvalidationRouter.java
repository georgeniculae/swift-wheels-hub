package com.carrental.cloudgateway.router;

import com.carrental.cloudgateway.handler.SwaggerInvalidationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class SwaggerInvalidationRouter {

    @Bean
    public RouterFunction<ServerResponse> swaggerInvalidationRoute(SwaggerInvalidationHandler swaggerInvalidationHandler) {
        return RouterFunctions.route()
                .DELETE("/invalidate-swagger-cache", swaggerInvalidationHandler::invalidateSwaggerFolderCache)
                .build();
    }

}
