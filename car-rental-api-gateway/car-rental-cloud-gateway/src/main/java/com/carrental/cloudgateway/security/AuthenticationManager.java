package com.carrental.cloudgateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationTokenConverter jwtAuthenticationTokenConverter;
    private final NimbusJwtDecoder nimbusJwtDecoder;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .map(this::getUsername)
                .flatMap(userDetailsService::findByUsername)
                .map(this::getUsernamePasswordAuthenticationToken)
                .switchIfEmpty(Mono.empty());
    }

    private String getUsername(Authentication authentication1) {
        Jwt jwt = nimbusJwtDecoder.decode(authentication1.getPrincipal().toString());

        return jwtAuthenticationTokenConverter.extractUsername(jwt);
    }

    private Authentication getUsernamePasswordAuthenticationToken(UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );
    }

}
