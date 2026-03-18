package com.example.apicountries.service;

import com.example.apicountries.entity.OneTimePasswordEntity;
import com.example.apicountries.entity.UserEntity;
import com.example.apicountries.exception.UserException;
import com.example.apicountries.repository.OneTimePasswordRepository;
import com.example.apicountries.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OneTimePasswordService {
    private static final Long EXPIRY_INTERVAL = 5L * 60 * 1000;

    private final OneTimePasswordRepository oneTimePasswordRepository;
    private final UserRepository userRepository;

    public OneTimePasswordEntity createOneTimePassword(UserEntity userId) {
        Optional<UserEntity> optionalUser = userRepository.findById(userId.getId());

        if (optionalUser.isEmpty()) {
            throw new UserException("Not Found", HttpStatus.NOT_FOUND);
        }

        OneTimePasswordEntity oneTimePassword = new OneTimePasswordEntity();
        oneTimePassword.setOneTimePasswordCode(OneTimePasswordHelper.createRandomOneTimePassword().get());
        oneTimePassword.setExpires(new Date(System.currentTimeMillis() + EXPIRY_INTERVAL));

        oneTimePassword.setUser(userId);
        oneTimePasswordRepository.save(oneTimePassword);


        return oneTimePassword;
    }

    @Transactional
    public void deleteByOneTimePasswordCode(Integer code) {
        oneTimePasswordRepository.deleteByOneTimePasswordCode(code);
    }
}
