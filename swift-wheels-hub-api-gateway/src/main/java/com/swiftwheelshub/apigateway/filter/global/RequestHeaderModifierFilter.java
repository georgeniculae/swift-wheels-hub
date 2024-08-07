package com.swiftwheelshub.apigateway.filter.global;

import com.swiftwheelshub.apigateway.exception.ExceptionUtil;
import com.swiftwheelshub.apigateway.security.JwtAuthenticationTokenConverter;
import com.swiftwheelshub.dto.AuthenticationInfo;
import com.swiftwheelshub.exception.SwiftWheelsHubResponseStatusException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestHeaderModifierFilter implements GlobalFilter, Ordered {

    private static final String X_API_KEY_HEADER = "X-API-KEY";

    private static final String X_USERNAME = "X-USERNAME";

    private static final String X_ROLES = "X-ROLES";

    private static final String REGISTER_PATH = "/register";

    private static final String DEFINITION_PATH = "/definition";

    @Value("${apikey-secret}")
    private String apikey;

    private final JwtAuthenticationTokenConverter jwtAuthenticationTokenConverter;

    private final NimbusReactiveJwtDecoder nimbusReactiveJwtDecoder;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return createMutatedHeaders(exchange)
                .flatMap(chain::filter)
                .onErrorResume(e -> {
                    log.error("Error while trying to mutate headers: {}", e.getMessage());

                    HttpStatusCode statusCode = ExceptionUtil.extractExceptionStatusCode(e);
                    exchange.getResponse().setStatusCode(statusCode);

                    return exchange.getResponse().setComplete();
                });
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private Mono<ServerWebExchange> createMutatedHeaders(ServerWebExchange exchange) {
        return Mono.just(exchange.getRequest())
                .filter(this::doesPathContainPattern)
                .flatMap(serverHttpRequest -> nimbusReactiveJwtDecoder.decode(getAuthorizationToken(serverHttpRequest)))
                .flatMap(this::getAuthenticationInfo)
                .switchIfEmpty(Mono.just(AuthenticationInfo.builder().build()))
                .map(authenticationInfo -> createMutatedServerWebExchange(exchange, authenticationInfo));
    }

    private boolean doesPathContainPattern(ServerHttpRequest serverHttpRequest) {
        String path = serverHttpRequest.getPath().value();

        return !path.contains(REGISTER_PATH) && !path.contains(DEFINITION_PATH);
    }

    private Mono<AuthenticationInfo> getAuthenticationInfo(Jwt jwt) {
        return Mono.zip(
                getUsername(jwt),
                getRoles(jwt),
                (username, roles) -> AuthenticationInfo.builder().username(username).roles(roles).build()
        );
    }

    private Mono<String> getUsername(Jwt jwt) {
        return Mono.just(jwtAuthenticationTokenConverter.extractUsername(jwt))
                .switchIfEmpty(Mono.defer(() -> Mono.just(StringUtils.EMPTY)));
    }

    private Mono<List<String>> getRoles(Jwt jwt) {
        return jwtAuthenticationTokenConverter.extractGrantedAuthorities(jwt)
                .map(GrantedAuthority::getAuthority)
                .collectList()
                .switchIfEmpty(Mono.just(List.of()));
    }

    private String getAuthorizationToken(ServerHttpRequest request) {
        return Optional.ofNullable(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .orElseThrow(() -> new SwiftWheelsHubResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Authorization header is missing"
                        )
                )
                .substring(7);
    }

    private ServerWebExchange createMutatedServerWebExchange(ServerWebExchange exchange,
                                                             AuthenticationInfo authenticationInfo) {
        return exchange.mutate()
                .request(mutateHeaders(authenticationInfo.username(), authenticationInfo.roles()))
                .build();
    }

    private Consumer<ServerHttpRequest.Builder> mutateHeaders(String username, List<String> roles) {
        return requestBuilder -> {
            requestBuilder.header(X_API_KEY_HEADER, apikey);

            if (ObjectUtils.isNotEmpty(username)) {
                requestBuilder.header(X_USERNAME, username);
            }

            if (ObjectUtils.isNotEmpty(roles)) {
                requestBuilder.header(X_ROLES, roles.toArray(new String[]{}));
            }

            requestBuilder.headers(headers -> headers.remove(HttpHeaders.AUTHORIZATION));
        };
    }

}
