package com.swiftwheelshub.lib.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
@ConditionalOnBean(ApiKeyAuthenticationProvider.class)
public class SecurityConfig {

    private final AuthenticationFilter authenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request.requestMatchers("/agency/definition/**",
                                "/bookings/definition/**",
                                "/customers/definition/**",
                                "/customers/register",
                                "/expense/definition/**",
                                "/agency/actuator/**",
                                "/bookings/actuator/**",
                                "/customers/actuator/**",
                                "/expense/actuator/**",
                                "/favicon.ico",
                                "/actuator/**").permitAll()
                        .requestMatchers("/agency/**",
                                "/bookings/**",
                                "/customers/**",
                                "/expense/**").authenticated()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authenticationFilter, AnonymousAuthenticationFilter.class)
                .build();
    }

}
