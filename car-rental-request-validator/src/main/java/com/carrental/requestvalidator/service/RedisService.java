package com.carrental.requestvalidator.service;

import com.carrental.exception.CarRentalException;
import com.carrental.requestvalidator.model.SwaggerFolder;
import com.carrental.requestvalidator.repository.SwaggerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private static final String SWAGGER = "Swagger";
    private final SwaggerRepository swaggerRepository;
    private final SwaggerExtractorService swaggerExtractorService;

    public void addSwaggerFolderToRedis() {
        try {
            Map<String, String> swaggerIdentifierAndContent = swaggerExtractorService.getSwaggerIdentifierAndContent();

            SwaggerFolder swaggerFolder = SwaggerFolder.builder()
                    .id(SWAGGER)
                    .swaggerIdentifierAndContent(swaggerIdentifierAndContent)
                    .build();

            swaggerRepository.save(swaggerFolder);
        } catch (Exception e) {
            log.error("Error while setting swagger folder in Redis: {}", e.getMessage());

            throw new CarRentalException(e);
        }
    }

    public void repopulateRedisWithSwaggerFolder() {
        try {
            swaggerRepository.deleteAll();
        } catch (Exception e) {
            log.error("Error while repopulating swagger folder in Redis: {}", e.getMessage());

            throw new CarRentalException(e);
        }

        addSwaggerFolderToRedis();
    }

}
