package com.carrental.cloudgateway.handler;

import com.carrental.cloudgateway.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SwaggerInvalidationHandler {

    private final RedisService redisService;

    public Mono<ServerResponse> invalidateSwaggerFolderCache(ServerRequest serverRequest) {
        return redisService.repopulateRedisWithSwaggerFolder()
                .filter(Boolean.TRUE::equals)
                .flatMap(response -> ServerResponse.noContent().build());
    }

}
