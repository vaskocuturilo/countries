package com.example.apicountries.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.apicountries.dto.UserDto;
import com.example.apicountries.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Data
@Component
public class UserAuthenticationProvider {

    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    private final UserService userService;


    @PostConstruct
    protected void init() {
        // this is to avoid having the raw secret key available in the JVM
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(UserDto user) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + 3600000); // 1 hour

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        return JWT.create()
                .withSubject(user.getLogin())
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .withClaim("firstName", user.getFirstName())
                .withClaim("lastName", user.getLastName())
                .sign(algorithm);
    }

    public String extractEmail(String token) {
        final Algorithm algorithm = Algorithm.HMAC256(secretKey);
        return JWT.require(algorithm)
                .build()
                .verify(token)
                .getSubject();
    }
}