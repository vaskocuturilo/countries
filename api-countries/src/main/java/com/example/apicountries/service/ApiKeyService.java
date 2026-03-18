package com.example.apicountries.service;

import com.example.apicountries.entity.ApiKeyEntity;
import com.example.apicountries.entity.UserEntity;
import com.example.apicountries.exception.UserException;
import com.example.apicountries.repository.ApiKeyRepository;
import com.example.apicountries.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApiKeyService {
    private static final Long EXPIRY_INTERVAL = 5L * 60 * 1000;

    private final ApiKeyRepository apiKeyRepository;

    private final UserRepository userRepository;

    public ApiKeyEntity createApiKey(UserEntity userId) {
        final Optional<UserEntity> optionalUser = userRepository.findById(userId.getId());

        if (optionalUser.isEmpty()) {
            throw new UserException("Not Found", HttpStatus.NOT_FOUND);
        }

        final ApiKeyEntity apiKeyEntity = new ApiKeyEntity();

        apiKeyEntity.setApiKey(ApiKeyHelper.createApiKey().get());
        apiKeyEntity.setExpires(new Date(System.currentTimeMillis() + EXPIRY_INTERVAL));

        apiKeyEntity.setUser(userId);
        apiKeyRepository.save(apiKeyEntity);

        return apiKeyEntity;
    }

    @Transactional
    public void deleteByApiKey(Integer code) {
        apiKeyRepository.deleteByApiKey(code);
    }
}
