package com.example.apigateway.rest;

import com.example.apigateway.client.UserClient;
import com.example.apigateway.dto.SignUpDto;
import com.example.apigateway.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class ApiGatewayUsersRestControllerV1 {

    private final UserClient userClient;

    @PostMapping("/register")
    public Mono<ResponseEntity<UserDto>> registerNewUser(final @RequestBody SignUpDto sign) {
        return userClient.registerNewUser(sign).map(ResponseEntity::ok);
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<UserDto>> loginUser(final @RequestBody SignUpDto sign) {
        return userClient.registerNewUser(sign).map(ResponseEntity::ok);
    }
}
