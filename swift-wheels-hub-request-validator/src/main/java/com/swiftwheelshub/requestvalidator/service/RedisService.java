package com.swiftwheelshub.requestvalidator.service;

import com.swiftwheelshub.exception.SwiftWheelsHubException;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.lib.exceptionhandling.ExceptionUtil;
import com.swiftwheelshub.requestvalidator.model.SwaggerFile;
import com.swiftwheelshub.requestvalidator.repository.SwaggerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            maxAttempts = 6,
            backoff = @Backoff(value = 10000L),
            listeners = "loadSwaggerCacheAtStartup"
    )
    public void addSwaggerFilesToRedis() {
        try {
            List<SwaggerFile> swaggerFiles = swaggerExtractorService.getSwaggerFiles();

            swaggerRepository.saveAll(swaggerFiles);
        } catch (Exception e) {
            log.error("Error while setting swagger folder in Redis: {}", e.getMessage());

            throw ExceptionUtil.handleException(e);
        }
    }

    public void repopulateRedisWithSwaggerFiles(String microserviceName) {
        SwaggerFile swaggerFile;

        try {
            swaggerRepository.deleteById(microserviceName);
            String swaggerContent = swaggerExtractorService.getSwaggerFileForMicroservice(microserviceName)
                    .getSwaggerContent();

            swaggerFile = SwaggerFile.builder()
                    .identifier(microserviceName)
                    .swaggerContent(swaggerContent)
                    .build();
        } catch (Exception e) {
            log.error("Error while repopulating swagger folder in Redis: {}", e.getMessage());

            if (e instanceof SwiftWheelsHubNotFoundException swiftWheelsHubNotFoundException) {
                throw swiftWheelsHubNotFoundException;
            }

            throw new SwiftWheelsHubException(e.getMessage());
        }

        swaggerRepository.save(swaggerFile);
    }

}
