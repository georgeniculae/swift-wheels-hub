package com.carrental.cloudgateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationTokenConverter jwtAuthenticationTokenConverter;
    private final NimbusReactiveJwtDecoder nimbusReactiveJwtDecoder;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .flatMap(this::getUsername)
                .flatMap(userDetailsService::findByUsername)
                .map(this::getUsernamePasswordAuthenticationToken)
                .switchIfEmpty(Mono.empty());
    }

    private Mono<String> getUsername(Authentication authentication) {
        return nimbusReactiveJwtDecoder.decode(authentication.getPrincipal().toString())
                .map(jwtAuthenticationTokenConverter::extractUsername);
    }

    private Authentication getUsernamePasswordAuthenticationToken(UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );
    }

}
