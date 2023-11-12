package com.carrental.cloudgateway.security;

import com.carrental.dto.AuthenticationRequest;
import com.carrental.dto.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationTokenConverter jwtAuthenticationTokenConverter;
    private final PasswordEncoder passwordEncoder;

    public Mono<AuthenticationResponse> authenticateUser(AuthenticationRequest authenticationRequest) {
        return userDetailsService.findByUsername(authenticationRequest.username())
                .filter(existingUser -> doPasswordsMatch(authenticationRequest, existingUser))
                .map(user -> new AuthenticationResponse().token(jwtAuthenticationTokenConverter.generateToken(user)))
                .onErrorResume(e -> {
                    log.error("Error while processing request: {}", e.getMessage());

                    return Mono.empty();
                });
    }

    private boolean doPasswordsMatch(AuthenticationRequest authenticationRequest, UserDetails existingUser) {
        return passwordEncoder.matches(authenticationRequest.password(), existingUser.getPassword());
    }

}
