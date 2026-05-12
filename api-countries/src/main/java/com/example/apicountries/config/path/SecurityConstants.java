package com.example.apicountries.config.path;

public final class SecurityConstants {
    private SecurityConstants() {
        /* This utility class should not be instantiated */
    }

    public static final String[] PUBLIC_ROUTES = {
            "/api/v1/users/login",
            "/api/v1/users/register",
            "/api/v1/users/active",
            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };
}