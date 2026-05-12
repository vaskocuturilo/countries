package com.example.apicountries.config;

import com.example.apicountries.config.path.SecurityConstants;
import com.example.apicountries.filter.APIKeyAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final String principalRequestHeader;
    private final String principalRequestValue;
    private final JwtAuthFilter jwtAuthFilter;


    public SecurityConfig(
            @Value("${http.auth-token-header-name}") String principalRequestHeader,
            @Value("${http.auth-token}") String principalRequestValue, JwtAuthFilter jwtAuthFilter) {
        this.principalRequestHeader = principalRequestHeader;
        this.principalRequestValue = principalRequestValue;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        APIKeyAuthFilter filter = new APIKeyAuthFilter(principalRequestHeader, principalRequestValue);

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SecurityConstants.PUBLIC_ROUTES).permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtAuthFilter, APIKeyAuthFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Collections.singletonList("*"));
        config.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH"));

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
