package com.carrental.cloudgateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationTokenConverter jwtAuthenticationTokenConverter;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        return Mono.just(token)
                .map(jwtAuthenticationTokenConverter::extractUsername)
                .flatMap(userDetailsService::findByUsername)
                .filter(user -> jwtAuthenticationTokenConverter.isTokenValid(token, user))
                .map(this::getUsernamePasswordAuthenticationToken)
                .switchIfEmpty(Mono.empty());
    }

    private Authentication getUsernamePasswordAuthenticationToken(UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );
    }

}
