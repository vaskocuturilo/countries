package com.example.apicountries.exception;

import java.time.LocalDateTime;

public record ErrorResponseDto
        (
                String error,
                int errorCode,
                String message,
                LocalDateTime timestamp
        ) {
}
