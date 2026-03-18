package com.example.apicountries.service;


import jakarta.xml.bind.DatatypeConverter;

import java.security.SecureRandom;
import java.util.function.Supplier;

public class ApiKeyHelper {
    private static final SecureRandom NUMBERS = new SecureRandom();

    private ApiKeyHelper() {
    }

    public static Supplier<String> createApiKey() {
        return () -> {
            final byte[] bytes = new byte[128 / 28];
            NUMBERS.nextBytes(bytes);
            return DatatypeConverter.printHexBinary(bytes).toLowerCase();
        };
    }
}
