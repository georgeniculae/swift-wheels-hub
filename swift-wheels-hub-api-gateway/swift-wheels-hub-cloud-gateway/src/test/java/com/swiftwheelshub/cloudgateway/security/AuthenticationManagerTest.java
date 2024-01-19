package com.swiftwheelshub.cloudgateway.security;

import com.swiftwheelshub.cloudgateway.model.User;
import com.swiftwheelshub.cloudgateway.util.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationManagerTest {

    @InjectMocks
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtAuthenticationTokenConverter jwtAuthenticationTokenConverter;

    @Mock
    private UserDetailsService userDetailsService;

//    @Test
//    void authenticateTest_success() {
//        User user = TestUtils.getResourceAsJson("/data/UserDto.json", User.class);
//        String token = "token";
//        String username = "alexandrupopescu";
//        Collection<? extends GrantedAuthority> roles = List.of(new SimpleGrantedAuthority("ROLE_USER"));
//
//        Authentication authentication = mock(Authentication.class);
//
//        when(authentication.getCredentials()).thenReturn(token);
//        when(userDetailsService.findByUsername(anyString())).thenReturn(Mono.just(user));
//
//        StepVerifier.create(authenticationManager.authenticate(authentication))
//                .expectNextMatches(auth -> username.equals(auth.getPrincipal()) &&
//                        auth.isAuthenticated() &&
//                        roles.equals(auth.getAuthorities()))
//                .verifyComplete();
//    }
//
//    @Test
//    void authenticateTest_noResultOnFindingByUsername() {
//        String token = "token";
//
//        Authentication authentication = mock(Authentication.class);
//
//        when(authentication.getCredentials()).thenReturn(token);
//        when(userDetailsService.findByUsername(anyString())).thenReturn(Mono.empty());
//
//        StepVerifier.create(authenticationManager.authenticate(authentication))
//                .expectComplete()
//                .verify();
//    }

}
