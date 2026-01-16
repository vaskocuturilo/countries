package com.example.apicountries.redis.service;

import com.example.apicountries.entity.CountryDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class CountryDocumentCacheService {
    private final RedisTemplate<String, CountryDocument> redisTemplate;

    public CountryDocumentCacheService(RedisTemplate<String, CountryDocument> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void cacheCountryDocument(CountryDocument country) {
        final String alpha2 = "alpha2:" + country.getAlpha2();
        final String alpha3 = "alpha3:" + country.getAlpha3();


        redisTemplate.opsForList().rightPush(alpha2, country);
        redisTemplate.opsForValue().set(alpha3, country);
        log.info("Cached country document [{}] to Redis under list [{}]", country.getAlpha2(), alpha3);

    }

    public List<CountryDocument> getCountryDocumentByAlpha2Code(String alpha2) {
        final String listKey = "alpha2::" + alpha2;
        final var cachedList = redisTemplate.opsForList().range(listKey, 0, -1);

        if (cachedList == null || cachedList.isEmpty()) {
            log.info("Redis cache miss for level: {}", alpha2);
            return Collections.emptyList();
        }

        log.info("Fetched {} countries from Redis list for level: {}", cachedList.size(), alpha2);
        return cachedList;
    }
}
