package com.example.apicountries.redis.service;

import com.example.apicountries.entity.CountryDocument;
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
class CountryDocumentCacheServiceTest {

    @Mock
    private RedisTemplate<String, CountryDocument> redisTemplate;

    @Mock
    private ListOperations<String, CountryDocument> listOps;

    @Mock
    private ValueOperations<String, CountryDocument> valueOps;

    private CountryDocumentCacheService cacheService;

    @BeforeEach
    void setUp() {
        Mockito.lenient().when(redisTemplate.opsForList()).thenReturn(listOps);
        Mockito.lenient().when(redisTemplate.opsForValue()).thenReturn(valueOps);
        cacheService = new CountryDocumentCacheService(redisTemplate);
    }

    @Test
    void shouldCacheCountryCorrectly() {
        // Arrange
        CountryDocument countryDocument = new CountryDocument();
        countryDocument.setAlpha2("TU");
        countryDocument.setAlpha3("TUV");
        countryDocument.setCapital("Funafuti");

        // Act
        cacheService.cacheCountryDocument(countryDocument);

        // Assert
        String alpha2 = "alpha2:";

        verify(listOps).rightPush(alpha2, countryDocument);
        verify(valueOps).set(alpha2, countryDocument);
    }

    @Test
    void shouldReturnCountryFromCache() {
        // Arrange
        CountryDocument countryDocument = new CountryDocument();
        countryDocument.setAlpha2("TU");
        countryDocument.setAlpha3("TUV");
        countryDocument.setCapital("Funafuti");

        List<CountryDocument> mockList = List.of(countryDocument);

        when(listOps.range("alpha2:TU", 0, -1)).thenReturn(mockList);

        // Act
        List<CountryDocument> result = cacheService.getCountryDocumentByAlpha2Code("TU");

        // Assert
        assertEquals(1, result.size());
        assertEquals(countryDocument.getAlpha2(), result.getFirst().getAlpha2());
    }

    @Test
    void shouldReturnEmptyListWhenCacheIsEmpty() {
        // Arrange
        when(listOps.range("alpha2:WWWWWW", 0, -1)).thenReturn(null);

        // Act
        List<CountryDocument> result = cacheService.getCountryDocumentByAlpha2Code("WWWWWW");

        // Assert
        assertTrue(result.isEmpty());
    }
}