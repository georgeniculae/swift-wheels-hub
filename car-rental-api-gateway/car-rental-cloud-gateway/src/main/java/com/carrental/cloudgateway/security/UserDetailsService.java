package com.carrental.cloudgateway.security;

import com.carrental.exception.CarRentalResponseStatusException;
import com.carrental.cloudgateway.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserDetailsService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(
                        Mono.error(
                                () -> new CarRentalResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "User with username " + username + " was not found"
                                )
                        )
                )
                .cast(UserDetails.class);
    }

}
