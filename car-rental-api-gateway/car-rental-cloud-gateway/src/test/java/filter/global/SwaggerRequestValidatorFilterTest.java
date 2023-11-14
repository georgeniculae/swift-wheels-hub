package filter.global;

import com.carrental.cloudgateway.filter.global.SwaggerRequestValidatorFilter;
import com.carrental.cloudgateway.model.SwaggerFolder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveValueOperations;

@ExtendWith(MockitoExtension.class)
class SwaggerRequestValidatorFilterTest {

    @InjectMocks
    private SwaggerRequestValidatorFilter swaggerRequestValidatorFilter;

    @Mock
    private GatewayFilterChain chain;

    @Mock
    private ReactiveRedisOperations<String, SwaggerFolder> redisSwagger;

    @Mock
    private ReactiveValueOperations<String, SwaggerFolder> reactiveValueOperations;

//    @Test
//    void filterTest_getRequest_success() {
//        Map<String, OpenAPI> expectedResult = new HashMap<>();
//        expectedResult.put("agency", new OpenAPIV3Parser().read("src/main/resources/swagger-definitions/car-rental-agency.yaml"));
//
//        SwaggerFolder swaggerFolder = SwaggerFolder.builder()
//                .id("1")
//                .swaggerIdentifierAndContent(expectedResult)
//                .build();
//
//        MockServerHttpRequest request = MockServerHttpRequest.get("/agency/rental-offices/{id}", 1)
//                .accept(MediaType.APPLICATION_JSON)
//                .build();
//        ServerWebExchange exchange = MockServerWebExchange.builder(request).build();
//
//        when(redisSwagger.opsForValue()).thenReturn(reactiveValueOperations);
//        when(reactiveValueOperations.get(any())).thenReturn(Mono.just(swaggerFolder));
//        when(chain.filter(any())).thenReturn(Mono.empty());
//
//        StepVerifier.create(swaggerRequestValidatorFilter.filter(exchange, chain))
//                .expectComplete()
//                .verify();
//    }

//    @Test
//    void filterTest_postRequest_success() {
//        Map<String, OpenAPI> expectedResult = new HashMap<>();
//        expectedResult.put("agency", new OpenAPIV3Parser().read("src/main/resources/swagger-definitions/car-rental-agency.yaml"));
//
//        RentalOfficeDto rentalOfficeDto = TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);
//        String valueAsString = TestUtils.writeValueAsString(rentalOfficeDto);
//
//        SwaggerFolder swaggerFolder = SwaggerFolder.builder()
//                .id("1")
//                .swaggerIdentifierAndContent(expectedResult)
//                .build();
//
//        MockServerHttpRequest request = MockServerHttpRequest.post("/agency/rental-offices")
//                .accept(MediaType.APPLICATION_JSON)
//                .body(valueAsString);
//        ServerWebExchange exchange = MockServerWebExchange.builder(request).build();
//
//        when(redisSwagger.opsForValue()).thenReturn(reactiveValueOperations);
//        when(reactiveValueOperations.get(any())).thenReturn(Mono.just(swaggerFolder));
//        when(chain.filter(any())).thenReturn(Mono.empty());
//
//        StepVerifier.create(swaggerRequestValidatorFilter.filter(exchange, chain))
//                .expectComplete()
//                .verify();
//    }

//    @Test
//    void filterTest_postRequest_error_emptyBody() {
//        Map<String, OpenAPI> expectedResult = new HashMap<>();
//        expectedResult.put("agency", new OpenAPIV3Parser().read("src/main/resources/swagger-definitions/car-rental-agency.yaml"));
//
//        SwaggerFolder swaggerFolder = SwaggerFolder.builder()
//                .id("1")
//                .swaggerIdentifierAndContent(expectedResult)
//                .build();
//
//        MockServerHttpRequest request = MockServerHttpRequest.post("/agency/rental-offices")
//                .accept(MediaType.APPLICATION_JSON)
//                .build();
//        ServerWebExchange exchange = MockServerWebExchange.builder(request).build();
//
//        when(redisSwagger.opsForValue()).thenReturn(reactiveValueOperations);
//        when(reactiveValueOperations.get(any())).thenReturn(Mono.just(swaggerFolder));
//
//        StepVerifier.create(swaggerRequestValidatorFilter.filter(exchange, chain))
//                .expectError()
//                .verify();
//    }

}
