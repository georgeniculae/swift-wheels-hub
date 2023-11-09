package com.carrental.cloudgateway.service;

import com.carrental.cloudgateway.exception.CarRentalException;
import com.carrental.cloudgateway.model.SwaggerFolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "swagger-validator", name = "enabled", havingValue = "true")
@Slf4j
public class RedisService {

    private static final String SWAGGER = "swagger";
    private final ReactiveRedisOperations<String, SwaggerFolder> redisSwagger;
    private final SwaggerExtractorService swaggerExtractorService;

    public Mono<Boolean> addSwaggerFolderToRedis() {
        return swaggerExtractorService.getSwaggerIdentifierAndContent()
                .flatMap(swaggerIdentifierAndContent ->
                        redisSwagger.opsForValue()
                                .set(
                                        SWAGGER,
                                        SwaggerFolder.builder()
                                                .id("1")
                                                .swaggerIdentifierAndContent(swaggerIdentifierAndContent)
                                                .build()
                                )
                )
                .onErrorResume(e -> {
                    log.error("Error while setting swagger folder in Redis: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e));
                });
    }

    public Mono<Boolean> repopulateRedisWithSwaggerFolder() {
        return redisSwagger.delete(SWAGGER)
                .flatMap(numberOfDeletedItems -> addSwaggerFolderToRedis())
                .onErrorResume(e -> {
                    log.error("Error while repopulating swagger folder in Redis: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e));
                });
    }

}
