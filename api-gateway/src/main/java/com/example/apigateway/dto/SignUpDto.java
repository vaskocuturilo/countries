package com.example.apigateway.dto;

import lombok.Builder;

@Builder
public record SignUpDto(String firstName, String lastName, String login, char[] password) { }