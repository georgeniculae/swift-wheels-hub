package handler;

import com.carrental.cloudgateway.handler.SwaggerInvalidationHandler;
import com.carrental.cloudgateway.service.RedisService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SwaggerInvalidationHandlerTest {

    @InjectMocks
    private SwaggerInvalidationHandler swaggerInvalidationHandler;

    @Mock
    private RedisService redisService;

    @Test
    void invalidateSwaggerFolderCacheTest_success() {
        MockServerRequest serverRequest = MockServerRequest.builder().build();

        when(redisService.repopulateRedisWithSwaggerFolder()).thenReturn(Mono.just(true));

        StepVerifier.create(swaggerInvalidationHandler.invalidateSwaggerFolderCache(serverRequest))
                .assertNext(serverResponse -> serverResponse.statusCode().isSameCodeAs(HttpStatusCode.valueOf(204)))
                .verifyComplete();
    }

}
