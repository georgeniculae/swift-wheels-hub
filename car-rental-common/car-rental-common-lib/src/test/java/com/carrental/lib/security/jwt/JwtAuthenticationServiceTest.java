package com.carrental.lib.security.jwt;

import com.carrental.dto.AuthenticationRequest;
import com.carrental.dto.AuthenticationResponse;
import com.carrental.entity.User;
import com.carrental.lib.service.UserService;
import com.carrental.lib.util.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationServiceTest {

    @InjectMocks
    private JwtAuthenticationService jwtAuthenticationService;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Test
    void authenticateTest_success() {
        AuthenticationRequest authenticationRequest =
                TestUtils.getResourceAsJson("/data/AuthenticationRequest.json", AuthenticationRequest.class);

        User user = TestUtils.getResourceAsJson("/data/User.json", User.class);

        String token = "token";

        when(jwtService.generateToken(any())).thenReturn(token);
        when(userService.findByUsername(anyString())).thenReturn(user);

        AuthenticationResponse authenticationResponse =
                assertDoesNotThrow(() -> jwtAuthenticationService.authenticate(authenticationRequest));

        assertEquals(token, authenticationResponse.token());

        verify(authenticationManager).authenticate(any());
    }

}
