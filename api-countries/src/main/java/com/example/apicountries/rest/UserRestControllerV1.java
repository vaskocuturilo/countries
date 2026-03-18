package com.example.apicountries.rest;

import com.example.apicountries.config.UserAuthenticationProvider;
import com.example.apicountries.dto.CredentialsDto;
import com.example.apicountries.dto.SignUpDto;
import com.example.apicountries.dto.UserActiveDto;
import com.example.apicountries.dto.UserDto;
import com.example.apicountries.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserRestControllerV1 {
    private final UserService userService;
    private final UserAuthenticationProvider userAuthenticationProvider;

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody @Valid CredentialsDto credentials) {
        final UserDto userDto = userService.login(credentials);
        userDto.setToken(userAuthenticationProvider.createToken(userDto));
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody @Valid SignUpDto userSignUp) {
        final UserDto createdUser = userService.register(userSignUp);
        createdUser.setToken(userAuthenticationProvider.createToken(createdUser));
        return ResponseEntity.created(URI.create("/users/" + createdUser.getId())).body(createdUser);
    }

    @PostMapping("/active")
    public ResponseEntity<UserActiveDto> active(@RequestParam final Integer userId, @RequestParam final Integer code) {
        final UserActiveDto userActiveDto = userService.active(userId, code);
        return ResponseEntity.ok(userActiveDto);
    }
}
