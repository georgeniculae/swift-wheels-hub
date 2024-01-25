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

    private final NimbusReactiveJwtDecoder nimbusReactiveJwtDecoder;
    private final JwtAuthenticationTokenConverter jwtAuthenticationTokenConverter;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .map(auth -> auth.getPrincipal().toString())
                .flatMap(nimbusReactiveJwtDecoder::decode)
                .flatMap(jwtAuthenticationTokenConverter::convert);
    }

}
