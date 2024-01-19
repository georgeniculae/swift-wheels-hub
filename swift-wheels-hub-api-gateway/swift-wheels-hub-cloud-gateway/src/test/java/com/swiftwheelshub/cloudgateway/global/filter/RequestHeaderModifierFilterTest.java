package com.swiftwheelshub.cloudgateway.global.filter;

import com.swiftwheelshub.cloudgateway.filter.global.RequestHeaderModifierFilter;
import com.swiftwheelshub.cloudgateway.security.JwtAuthenticationTokenConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestHeaderModifierFilterTest {

    @InjectMocks
    private RequestHeaderModifierFilter requestHeaderModifierFilter;

    @Mock
    private NimbusReactiveJwtDecoder nimbusReactiveJwtDecoder;

    @Mock
    private JwtAuthenticationTokenConverter jwtAuthenticationTokenConverter;

    @Mock
    private GatewayFilterChain chain;

    @Test
    void filterTest_success() {
        String tokenValue = """
                eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJYOHlqVkpTajA4eGV3Y2hlR3I1UWJuMV9iRnk1aWxpbGRwVkM2Nj
                IzaU9ZIn0.eyJleHAiOjE3MDQ3NTY5MDYsImlhdCI6MTcwNDc1NTEwNiwianRpIjoiNWY0NDE3NDgtMjUxNy00Yzk1LWIzNGQtYjAx
                MzI4NGQ0ZGExIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL2F1dGgvcmVhbG1zL2Nhci1yZW50YWwtc2VydmljZSIsImF1ZC
                I6ImFjY291bnQiLCJzdWIiOiJhMmU5ZDNiMy1jOWNiLTRkYzYtOGY1OS01MjUzMDBlMDljMDkiLCJ0eXAiOiJCZWFyZXIiLCJhenAi
                OiJjYXItcmVudGFsLWNsb3VkLWdhdGV3YXkiLCJzZXNzaW9uX3N0YXRlIjoiNTAwNmM3MDktNmNhNS00ZjI1LWIyZjQtMjdhMDVlYT
                dlYzczIiwiYWNyIjoiMSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsImFkbWluIiwidW1hX2F1dGhv
                cml6YXRpb24iLCJkZWZhdWx0LXJvbGVzLWNhcl9yZW50YWxfc2VydmljZSJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOn
                sicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoi
                b3BlbmlkIGVtYWlsIHByb2ZpbGUiLCJzaWQiOiI1MDA2YzcwOS02Y2E1LTRmMjUtYjJmNC0yN2EwNWVhN2VjNzMiLCJlbWFpbF92ZX
                JpZmllZCI6ZmFsc2UsIm5hbWUiOiJhZG1pbiBhZG1pbiIsInByZWZlcnJlZF91c2VybmFtZSI6ImFkbWluIiwiZ2l2ZW5fbmFtZSI6
                ImFkbWluIiwiZmFtaWx5X25hbWUiOiJhZG1pbiIsImVtYWlsIjoiZ3hnZ2VvcmdlZ3hnQGxpdmUuY29tIn0.p6wmoht0bT7H2VI_5x
                Kt_GhulkcKQDQ_K3-ir1Z8WG_kS6NcgmH3oU3mKUsPDJ1hoGkKdv8qSuFryOadmlHNNTn3poow1DDS-ckjHISAh_Do_W0Ob164J2TO
                Sjr11DbkVJFH3ALzJApAlDXcfvA3YqLd1gljm-GO8ukLS_Y--1JDMyMBh4gsomX9-9DDFN1dUeybXsUsOg1t1E8w5C7oNLLI9Y6iuL
                hyrlX81zLDJ1BBzBF-G7DaUTRWd8kNCg55nS98wfdsS2Bc_4gxfE1UDIWGLuoaKsnSK7GLHKGcpDWQzL2Hk-VxIM-9iaS7CJbofvVk
                EPRSuhLMo6nkDrCn8g""";

        MockServerHttpRequest request = MockServerHttpRequest.get("/agency/rental-offices/{id}", 1)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.builder(request).build();

        Map<String, Object> headers = Map.of(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue);
        Map<String, Object> claims = Map.of("preferred_username", "user");

        Jwt jwt = new Jwt(tokenValue, Instant.now(), Instant.now().plus(30, ChronoUnit.MINUTES), headers, claims);

        when(nimbusReactiveJwtDecoder.decode(anyString())).thenReturn(Mono.just(jwt));
        when(jwtAuthenticationTokenConverter.extractUsername(any(Jwt.class))).thenReturn("user");
        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        requestHeaderModifierFilter.filter(exchange, chain)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void filterTest_noAuthorizationHeader() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/agency/rental-offices/{id}", 1)
                .accept(MediaType.APPLICATION_JSON)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.builder(request).build();

        requestHeaderModifierFilter.filter(exchange, chain)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void filterTest_notCorrespondingPath() {
        String tokenValue = """
                eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJYOHlqVkpTajA4eGV3Y2hlR3I1UWJuMV9iRnk1aWxpbGRwVkM2Nj
                IzaU9ZIn0.eyJleHAiOjE3MDQ3NTY5MDYsImlhdCI6MTcwNDc1NTEwNiwianRpIjoiNWY0NDE3NDgtMjUxNy00Yzk1LWIzNGQtYjAx
                MzI4NGQ0ZGExIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL2F1dGgvcmVhbG1zL2Nhci1yZW50YWwtc2VydmljZSIsImF1ZC
                I6ImFjY291bnQiLCJzdWIiOiJhMmU5ZDNiMy1jOWNiLTRkYzYtOGY1OS01MjUzMDBlMDljMDkiLCJ0eXAiOiJCZWFyZXIiLCJhenAi
                OiJjYXItcmVudGFsLWNsb3VkLWdhdGV3YXkiLCJzZXNzaW9uX3N0YXRlIjoiNTAwNmM3MDktNmNhNS00ZjI1LWIyZjQtMjdhMDVlYT
                dlYzczIiwiYWNyIjoiMSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsImFkbWluIiwidW1hX2F1dGhv
                cml6YXRpb24iLCJkZWZhdWx0LXJvbGVzLWNhcl9yZW50YWxfc2VydmljZSJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOn
                sicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoi
                b3BlbmlkIGVtYWlsIHByb2ZpbGUiLCJzaWQiOiI1MDA2YzcwOS02Y2E1LTRmMjUtYjJmNC0yN2EwNWVhN2VjNzMiLCJlbWFpbF92ZX
                JpZmllZCI6ZmFsc2UsIm5hbWUiOiJhZG1pbiBhZG1pbiIsInByZWZlcnJlZF91c2VybmFtZSI6ImFkbWluIiwiZ2l2ZW5fbmFtZSI6
                ImFkbWluIiwiZmFtaWx5X25hbWUiOiJhZG1pbiIsImVtYWlsIjoiZ3hnZ2VvcmdlZ3hnQGxpdmUuY29tIn0.p6wmoht0bT7H2VI_5x
                Kt_GhulkcKQDQ_K3-ir1Z8WG_kS6NcgmH3oU3mKUsPDJ1hoGkKdv8qSuFryOadmlHNNTn3poow1DDS-ckjHISAh_Do_W0Ob164J2TO
                Sjr11DbkVJFH3ALzJApAlDXcfvA3YqLd1gljm-GO8ukLS_Y--1JDMyMBh4gsomX9-9DDFN1dUeybXsUsOg1t1E8w5C7oNLLI9Y6iuL
                hyrlX81zLDJ1BBzBF-G7DaUTRWd8kNCg55nS98wfdsS2Bc_4gxfE1UDIWGLuoaKsnSK7GLHKGcpDWQzL2Hk-VxIM-9iaS7CJbofvVk
                EPRSuhLMo6nkDrCn8g""";

        MockServerHttpRequest request = MockServerHttpRequest.get("/agency/swagger-ui.html")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.builder(request).build();

        Map<String, Object> headers = Map.of(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue);
        Map<String, Object> claims = Map.of("preferred_username", "user");

        Jwt jwt = new Jwt(tokenValue, Instant.now(), Instant.now().plus(30, ChronoUnit.MINUTES), headers, claims);

        when(nimbusReactiveJwtDecoder.decode(anyString())).thenReturn(Mono.just(jwt));
        when(jwtAuthenticationTokenConverter.extractUsername(any(Jwt.class))).thenReturn("user");
        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        requestHeaderModifierFilter.filter(exchange, chain)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

}
