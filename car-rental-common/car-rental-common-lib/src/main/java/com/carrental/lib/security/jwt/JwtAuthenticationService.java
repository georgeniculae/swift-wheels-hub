package com.carrental.lib.security.jwt;

import com.carrental.dto.AuthenticationRequest;
import com.carrental.dto.AuthenticationResponse;
import com.carrental.entity.User;
import com.carrental.lib.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Deprecated
@Service
@RequiredArgsConstructor
@ConditionalOnBean(name = "jwtAuthenticationFilter")
public class JwtAuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(request.username(), request.password());

        authenticationManager.authenticate(authentication);
        User user = userService.findByUsername(request.username());
        String jwtToken = jwtService.generateToken(user);

        return new AuthenticationResponse().token(jwtToken);
    }

}
