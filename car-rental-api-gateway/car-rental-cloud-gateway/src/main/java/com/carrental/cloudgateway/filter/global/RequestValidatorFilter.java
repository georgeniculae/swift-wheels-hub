package com.carrental.cloudgateway.filter.global;

import com.carrental.dto.RequestValidationReport;
import com.carrental.exception.CarRentalResponseStatusException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestValidatorFilter implements GlobalFilter, Ordered {

    @Value("${apikey-secret}")
    private String apikey;

    @Value("${request-validator-url}")
    private String requestValidatorUrl;

    private final WebClient webClient;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return getValidationReport(exchange)
                .flatMap(requestValidationReport -> filterRequest(exchange, chain, requestValidationReport));
    }

    @Override
    public int getOrder() {
        return 1;
    }

    private Mono<Void> filterRequest(ServerWebExchange exchange, GatewayFilterChain chain, RequestValidationReport requestValidationReport) {
        if (ObjectUtils.isEmpty(requestValidationReport)) {
            return chain.filter(exchange);
        }

        return Mono.error(
                new CarRentalResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        requestValidationReport.errorMessage()
                )
        );
    }

    private Mono<RequestValidationReport> getValidationReport(ServerWebExchange exchange) {
        return webClient.post()
                .uri(requestValidatorUrl)
                .header(HttpHeaders.AUTHORIZATION, apikey)
                .bodyValue(exchange.getRequest())
                .retrieve()
                .bodyToMono(RequestValidationReport.class)
                .onErrorResume(e -> {
                    log.error("Error while making REST call: {}", e.getMessage());

                    return Mono.empty();
                });
    }

}
