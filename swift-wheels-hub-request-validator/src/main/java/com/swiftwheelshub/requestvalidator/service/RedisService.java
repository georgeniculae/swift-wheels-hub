package com.swiftwheelshub.requestvalidator.service;

import com.swiftwheelshub.exception.SwiftWheelsHubException;
import com.swiftwheelshub.requestvalidator.model.SwaggerFile;
import com.swiftwheelshub.requestvalidator.repository.SwaggerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final SwaggerRepository swaggerRepository;
    private final SwaggerExtractorService swaggerExtractorService;

    @Retryable(
            retryFor = SwiftWheelsHubException.class,
            maxAttempts = 10, backoff = @Backoff(value = 6000L),
            listeners = "loadSwaggerCacheAtStartup"
    )
    public void addSwaggerFilesToRedis() {
        try {
            List<Pair<String, String>> swaggerIdentifierAndContent = swaggerExtractorService.getSwaggerIdentifierAndContent();

            List<SwaggerFile> swaggerFiles = swaggerIdentifierAndContent
                    .stream()
                    .map(swaggerIdAndContent -> SwaggerFile.builder()
                            .id(swaggerIdAndContent.getFirst())
                            .swaggerContent(swaggerIdAndContent.getSecond())
                            .build())
                    .toList();

            swaggerRepository.saveAll(swaggerFiles);
        } catch (Exception e) {
            log.error("Error while setting swagger folder in Redis: {}", e.getMessage());

            throw new SwiftWheelsHubException(e);
        }
    }

    public void repopulateRedisWithSwaggerFiles(String microserviceName) {
        SwaggerFile swaggerFile;

        try {
            swaggerRepository.deleteById(microserviceName);
            String swaggerContent = swaggerExtractorService.getSwaggerFileForMicroservice(microserviceName)
                    .getSecond();

            swaggerFile = SwaggerFile.builder()
                    .id(microserviceName)
                    .swaggerContent(swaggerContent)
                    .build();
        } catch (Exception e) {
            log.error("Error while repopulating swagger folder in Redis: {}", e.getMessage());

            throw new SwiftWheelsHubException(e);
        }

        swaggerRepository.save(swaggerFile);
    }

}
