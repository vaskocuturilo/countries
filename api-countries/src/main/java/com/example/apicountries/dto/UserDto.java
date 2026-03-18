package com.example.apicountries.dto;

import com.example.apicountries.entity.ApiKeyEntity;
import com.example.apicountries.entity.OneTimePasswordEntity;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private String login;
    private String token;
    private boolean active;
    private OneTimePasswordEntity oneTimePassword;
    private ApiKeyEntity apiKey;
}
