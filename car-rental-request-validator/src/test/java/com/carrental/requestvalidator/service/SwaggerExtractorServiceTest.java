package com.carrental.requestvalidator.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SwaggerExtractorServiceTest {

    @InjectMocks
    private SwaggerExtractorService swaggerExtractorService;

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
//        StepVerifier.create(requestValidatorFilter.filter(exchange, chain))
//                .expectComplete()
//                .verify();
//    }

}
