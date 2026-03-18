package com.example.apicountries.dto;

public record SignUpDto(String firstName, String lastName, String login, char[] password) { }