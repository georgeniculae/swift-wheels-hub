package service;

import com.carrental.cloudgateway.model.SwaggerFolder;
import com.carrental.cloudgateway.service.RedisService;
import com.carrental.cloudgateway.service.SwaggerExtractorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisServiceTest {

    @InjectMocks
    private RedisService redisService;

    @Mock
    private ReactiveRedisOperations<String, SwaggerFolder> redisSwagger;

    @Mock
    private SwaggerExtractorService swaggerExtractorService;

    @Mock
    private ReactiveValueOperations<String, SwaggerFolder> reactiveValueOperations;

    @Mock
    private ObjectMapper objectMapper;

    @Test
    void addSwaggerFolderToRedisTest_success() throws IOException {
        Map<String, String> expectedResult = new HashMap<>();
        expectedResult.put("agency", objectMapper.readValue(new File("src/test/resources/swagger-definition/car-rental-agency.yaml"), String.class));

        when(swaggerExtractorService.getSwaggerIdentifierAndContent()).thenReturn(Mono.just(expectedResult));
        when(redisSwagger.opsForValue()).thenReturn(reactiveValueOperations);
        when(reactiveValueOperations.set(anyString(), any(SwaggerFolder.class))).thenReturn(Mono.just(true));

        StepVerifier.create(redisService.addSwaggerFolderToRedis())
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void addSwaggerFolderToRedisTest_errorOnGettingSwaggerFolder() {
        when(swaggerExtractorService.getSwaggerIdentifierAndContent()).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(redisService.addSwaggerFolderToRedis())
                .expectError()
                .verify();
    }

    @Test
    void repopulateRedisWithSwaggerFolderTest_success() throws IOException {
        Map<String, String> expectedResult = new HashMap<>();
        expectedResult.put("agency", objectMapper.readValue(new File("src/test/resources/swagger-definition/car-rental-agency.yaml"), String.class));

        when(redisSwagger.delete(anyString())).thenReturn(Mono.just(1L));
        when(swaggerExtractorService.getSwaggerIdentifierAndContent()).thenReturn(Mono.just(expectedResult));
        when(redisSwagger.opsForValue()).thenReturn(reactiveValueOperations);
        when(reactiveValueOperations.set(anyString(), any(SwaggerFolder.class))).thenReturn(Mono.just(true));

        StepVerifier.create(redisService.repopulateRedisWithSwaggerFolder())
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void repopulateRedisWithSwaggerFolderTest_errorOnDeleteInRedis() {
        when(redisSwagger.delete(anyString())).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(redisService.repopulateRedisWithSwaggerFolder())
                .expectError()
                .verify();
    }

}
