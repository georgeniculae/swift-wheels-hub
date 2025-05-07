package com.autohub.requestvalidator.service;

import com.autohub.exception.AutoHubException;
import com.autohub.exception.AutoHubNotFoundException;
import com.autohub.lib.exceptionhandling.ExceptionUtil;
import com.autohub.requestvalidator.model.SwaggerFile;
import com.autohub.requestvalidator.repository.SwaggerRepository;
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
            retryFor = AutoHubException.class,
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

            if (e instanceof AutoHubNotFoundException autoHubNotFoundException) {
                throw autoHubNotFoundException;
            }

            throw new AutoHubException(e.getMessage());
        }

        swaggerRepository.save(swaggerFile);
    }

}
