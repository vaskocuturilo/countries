package com.example.apicountries.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class APISecurityConfig {

    @Value("${http.auth-token-header-name}")
    private String principalRequestHeader;

    @Value("${http.auth-token}")
    private String principalRequestValue;

    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        APIKeyAuthFilter filter = new APIKeyAuthFilter(principalRequestHeader);
        filter.setAuthenticationManager(authentication -> {
            String principal = (String) authentication.getPrincipal();
            if (!principalRequestValue.equals(principal)) {
                throw new BadCredentialsException(
                        String.format("The API key = %s was not found or not the expected value.", principal)
                );
            }
            authentication.setAuthenticated(true);
            return authentication;
        });

        http
                .securityMatcher("/api/v1/**")
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilter(filter)
                // TODO: Add auth.anyRequest().authenticated() with API-KEY FILTER
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());


        return http.build();
    }
}
