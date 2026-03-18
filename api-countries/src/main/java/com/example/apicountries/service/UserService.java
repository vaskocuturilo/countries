package com.example.apicountries.service;


import com.example.apicountries.dto.CredentialsDto;
import com.example.apicountries.dto.SignUpDto;
import com.example.apicountries.dto.UserActiveDto;
import com.example.apicountries.dto.UserDto;
import com.example.apicountries.entity.ApiKeyEntity;
import com.example.apicountries.entity.OneTimePasswordEntity;
import com.example.apicountries.entity.UserEntity;
import com.example.apicountries.exception.OneTimePasswordException;
import com.example.apicountries.exception.UserAlreadyActive;
import com.example.apicountries.exception.UserException;
import com.example.apicountries.model.UserMapper;
import com.example.apicountries.repository.OneTimePasswordRepository;
import com.example.apicountries.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    private final OneTimePasswordService oneTimePasswordService;

    private final OneTimePasswordRepository oneTimePasswordRepository;

    private final ApiKeyService apiKeyService;

    private static final String UNKNOWN_USER = "Unknown user";

    public UserDto login(final CredentialsDto credentials) {
        UserEntity user = userRepository.findByLogin(credentials.login())
                .orElseThrow(() -> new UserException(UNKNOWN_USER, HttpStatus.NOT_FOUND));

        if (!user.isActive()) {
            throw new UserException("The user is not activated", HttpStatus.FORBIDDEN);
        }

        if (passwordEncoder.matches(CharBuffer.wrap(credentials.password()), user.getPassword())) {
            return userMapper.toUserDto(user);
        }

        throw new UserException("Invalid password", HttpStatus.BAD_REQUEST);
    }

    public UserDto register(final SignUpDto userDto) {
        final Optional<UserEntity> optionalUser = userRepository.findByLogin(userDto.login());

        if (optionalUser.isPresent()) {
            throw new UserException("Login already exists", HttpStatus.BAD_REQUEST);
        }

        final UserEntity user = userMapper.signUpToUser(userDto);

        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(userDto.password())));

        final UserEntity savedUser = userRepository.save(user);

        final OneTimePasswordEntity oneTimePassword = oneTimePasswordService.createOneTimePassword(savedUser);

        final ApiKeyEntity apiKeyEntity = apiKeyService.createApiKey(savedUser);

        savedUser.setOneTimePassword(oneTimePassword);

        savedUser.setApiKey(apiKeyEntity);

        return userMapper.toUserDto(savedUser);
    }

    public UserActiveDto active(final Integer userId, final Integer code) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UNKNOWN_USER, HttpStatus.NOT_FOUND));

        Integer oneTimePasswordCodeExist = oneTimePasswordRepository.findByOneTimePasswordCode(userId);

        if (user.isActive()) {
            throw new UserAlreadyActive("The user is active now", HttpStatus.BAD_REQUEST);
        }
        if (!oneTimePasswordCodeExist.equals(code)) {
            throw new OneTimePasswordException("The code is incorrect", HttpStatus.NOT_FOUND);
        }

        user.setActive(true);

        final UserEntity savedUser = userRepository.save(user);

        oneTimePasswordService.deleteByOneTimePasswordCode(code);

        return userMapper.toUserActiveDto(savedUser);
    }


    public UserDto findByLogin(final String login) {
        final UserEntity user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UserException(UNKNOWN_USER, HttpStatus.NOT_FOUND));
        return userMapper.toUserDto(user);
    }
}
