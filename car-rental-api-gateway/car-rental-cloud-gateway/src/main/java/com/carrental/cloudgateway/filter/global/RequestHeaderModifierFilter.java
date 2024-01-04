package com.carrental.cloudgateway.filter.global;

import com.carrental.cloudgateway.security.JwtAuthenticationTokenConverter;
import com.carrental.exception.CarRentalResponseStatusException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class RequestHeaderModifierFilter implements GlobalFilter, Ordered {

    private static final String X_API_KEY_HEADER = "X-API-KEY";

    private static final String X_USERNAME = "X-USERNAME";

    private static final String REGISTER_PATH = "/register";

    private static final String DEFINITION_PATH = "/definition";

    @Value("${apikey-secret}")
    private String apikey;

    private final JwtAuthenticationTokenConverter jwtAuthenticationTokenConverter;

    private final NimbusReactiveJwtDecoder nimbusReactiveJwtDecoder;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return modifyHeaders(exchange)
                .flatMap(chain::filter);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private Mono<ServerWebExchange> modifyHeaders(ServerWebExchange exchange) {
        return getUsername(exchange.getRequest())
                .map(username -> exchange.mutate()
                        .request(mutateHeaders(username))
                        .build());
    }

    private boolean doesPathContainPattern(ServerHttpRequest serverHttpRequest) {
        String path = serverHttpRequest.getPath().value();

        return !path.contains(REGISTER_PATH) && !path.contains(DEFINITION_PATH);
    }

    private Mono<String> getUsername(ServerHttpRequest request) {
        return Mono.just(request)
                .filter(this::doesPathContainPattern)
                .flatMap(serverHttpRequest -> nimbusReactiveJwtDecoder.decode(getAuthorizationToken(request)))
                .map(jwtAuthenticationTokenConverter::extractUsername)
                .switchIfEmpty(Mono.defer(() -> Mono.just(StringUtils.EMPTY)));
    }

    private String getAuthorizationToken(ServerHttpRequest request) {
        return Optional.ofNullable(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .orElseThrow(() -> new CarRentalResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Authorization header is missing"
                        )
                )
                .substring(7);
    }

    private Consumer<ServerHttpRequest.Builder> mutateHeaders(String username) {
        return requestBuilder -> {
            requestBuilder.header(X_API_KEY_HEADER, apikey);

            if (ObjectUtils.isNotEmpty(username)) {
                requestBuilder.header(X_USERNAME, username);
            }

            requestBuilder.headers(headers -> headers.remove(HttpHeaders.AUTHORIZATION));
        };
    }

}
