package com.example.apicountries.redis.service;

import com.example.apicountries.entity.CountryEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountryEntityCacheServiceTests {

    @Mock
    private RedisTemplate<String, CountryEntity> redisTemplate;

    @Mock
    private ListOperations<String, CountryEntity> listOps;

    @Mock
    private ValueOperations<String, CountryEntity> valueOps;

    private CountryEntityCacheService cacheService;

    @BeforeEach
    void setUp() {
        Mockito.lenient().when(redisTemplate.opsForList()).thenReturn(listOps);
        Mockito.lenient().when(redisTemplate.opsForValue()).thenReturn(valueOps);
        cacheService = new CountryEntityCacheService(redisTemplate);
    }

    @Test
    void shouldCacheCountryCorrectly() {
        // Arrange
        CountryEntity countryEntity = new CountryEntity();
        countryEntity.setAlpha2("TU");
        countryEntity.setAlpha3("TUV");
        countryEntity.setCapital("Funafuti");

        // Act
        cacheService.cacheCountryEntity(countryEntity);

        // Assert
        String alpha2 = "alpha2:";

        verify(listOps).rightPush(alpha2, countryEntity);
        verify(valueOps).set(alpha2, countryEntity);
    }

    @Test
    void shouldReturnCountryFromCache() {
        // Arrange
        CountryEntity countryEntity = new CountryEntity();
        countryEntity.setAlpha2("TU");
        countryEntity.setAlpha3("TUV");
        countryEntity.setCapital("Funafuti");

        List<CountryEntity> mockList = List.of(countryEntity);

        when(listOps.range("alpha2:TU", 0, -1)).thenReturn(mockList);

        // Act
        List<CountryEntity> result = cacheService.getCountryEntityByAlpha2Code("TU");

        // Assert
        assertEquals(1, result.size());
        assertEquals(countryEntity.getAlpha2(), result.getFirst().getAlpha2());
    }

    @Test
    void shouldReturnEmptyListWhenCacheIsEmpty() {
        // Arrange
        when(listOps.range("alpha2:WWWWWW", 0, -1)).thenReturn(null);

        // Act
        List<CountryEntity> result = cacheService.getCountryEntityByAlpha2Code("WWWWWW");

        // Assert
        assertTrue(result.isEmpty());
    }

}