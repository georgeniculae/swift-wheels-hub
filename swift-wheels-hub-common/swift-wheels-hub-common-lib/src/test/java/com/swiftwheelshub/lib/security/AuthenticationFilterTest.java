package com.swiftwheelshub.lib.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationFilterTest {

    @InjectMocks
    private AuthenticationFilter authenticationFilter;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private FilterChain filterChain;

    @Mock
    private Authentication authentication;

    @Test
    void doFilterInternalTest_success() {
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.addHeader("X-API-KEY", "apikey");

        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();

        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);

        assertDoesNotThrow(() -> authenticationFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain));
    }

    @Test
    void doFilterInternalTest_noApiKeyAuthenticationHeader() throws ServletException, IOException {
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();

        authenticationFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);
    }

}
