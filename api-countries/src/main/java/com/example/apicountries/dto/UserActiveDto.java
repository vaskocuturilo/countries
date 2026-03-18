package com.example.apicountries.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data

public class UserActiveDto {
    private String firstName;
    private String lastName;
    private String login;
    private boolean active;
}
