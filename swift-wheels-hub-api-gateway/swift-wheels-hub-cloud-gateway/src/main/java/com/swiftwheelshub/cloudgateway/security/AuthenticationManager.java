package com.swiftwheelshub.cloudgateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtAuthenticationTokenConverter jwtAuthenticationTokenConverter;
    private final NimbusReactiveJwtDecoder nimbusReactiveJwtDecoder;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .flatMap(auth -> nimbusReactiveJwtDecoder.decode(auth.getPrincipal().toString()))
                .flatMap(jwtAuthenticationTokenConverter::convert);
    }

}
