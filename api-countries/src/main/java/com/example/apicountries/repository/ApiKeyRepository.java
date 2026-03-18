package com.example.apicountries.repository;


import com.example.apicountries.entity.ApiKeyEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ApiKeyRepository extends CrudRepository<ApiKeyEntity, UUID> {
    @Modifying
    @Query("delete from ApiKeyEntity key where key.apiKey = :code")
    void deleteByApiKey(final Integer code);
}
