package com.carrental.cloudgateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationTokenConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    public static final String USERNAME_CLAIM = "preferred_username";

    private final Converter<Jwt, Flux<GrantedAuthority>> jwtGrantedAuthoritiesConverter;

    @Override
    public Mono<AbstractAuthenticationToken> convert(@NonNull Jwt source) {
        return Optional.ofNullable(jwtGrantedAuthoritiesConverter.convert(source)).orElseThrow()
                .collectList()
                .map(authorities -> new JwtAuthenticationToken(source, authorities, extractUsername(source)));
    }

    public String extractUsername(Jwt jwt) {
        return (String) jwt.getClaims().get(USERNAME_CLAIM);
    }

}
