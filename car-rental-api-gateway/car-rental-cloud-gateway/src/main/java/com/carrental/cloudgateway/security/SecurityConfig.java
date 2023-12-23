package com.carrental.cloudgateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkUri;
    private final AuthenticationManager authenticationManager;
    private final JwtAuthenticationTokenConverter jwtAuthenticationTokenConverter;
    private final SecurityContextRepositoryImpl securityContextRepositoryImpl;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(request ->
                        request.pathMatchers("/agency/definition/**",
                                        "/bookings/definition/**",
                                        "/customers/definition/**",
                                        "/customers/register",
                                        "/expense/definition/**").permitAll()
                                .pathMatchers("/agency/**",
                                        "/bookings/**",
                                        "/customers/**",
                                        "/expense/**").authenticated()
                                .anyExchange().authenticated())
                .exceptionHandling(request ->
                        request.authenticationEntryPoint((response, error) ->
                                        Mono.fromRunnable(() -> response.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
                                .accessDeniedHandler((response, error) ->
                                        Mono.fromRunnable(() -> response.getResponse().setStatusCode(HttpStatus.FORBIDDEN))))
                .oauth2ResourceServer(resourceServerSpec ->
                        resourceServerSpec.jwt(jwtSpec -> jwtSpec.jwkSetUri(jwkUri)
                                .authenticationManager(authenticationManager)
                                .jwtAuthenticationConverter(jwtAuthenticationTokenConverter)))
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .securityContextRepository(securityContextRepositoryImpl)
                .requestCache(request -> request.requestCache(NoOpServerRequestCache.getInstance()))
                .build();
    }

}
