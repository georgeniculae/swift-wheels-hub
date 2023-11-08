package com.carrental.cloudgateway.handler;

import com.carrental.cloudgateway.security.AuthenticationService;
import com.carrental.dto.AuthenticationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthenticationHandler {

    private final AuthenticationService authenticationService;

    public Mono<ServerResponse> authenticateUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(AuthenticationRequest.class)
                .flatMap(authenticationService::authenticateUser)
                .flatMap(authenticationResponse -> ServerResponse.ok().bodyValue(authenticationResponse))
                .switchIfEmpty(ServerResponse.badRequest().build());
    }

}
