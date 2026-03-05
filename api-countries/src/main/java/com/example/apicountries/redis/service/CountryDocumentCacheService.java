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

    private static final String ALPHA2_PREFIX = "alpha2:";

    private static final String ALPHA3_PREFIX = "alpha3:";

    public CountryDocumentCacheService(RedisTemplate<String, CountryDocument> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void cacheCountryDocument(CountryDocument country) {
        final String alpha2Key = ALPHA2_PREFIX + country.getAlpha2();
        final String alpha3Key = ALPHA3_PREFIX + country.getAlpha3();


        redisTemplate.opsForList().rightPush(alpha2Key, country);
        redisTemplate.opsForValue().set(alpha3Key, country);
        log.info("Cached country document [{}] to Redis under list [{}]", country.getAlpha2(), alpha3Key);

    }

    public List<CountryDocument> getCountryDocumentByAlpha2Code(String alpha2) {
        final String listKey = ALPHA2_PREFIX + alpha2;
        final var cachedList = redisTemplate.opsForList().range(listKey, 0, -1);

        if (cachedList == null || cachedList.isEmpty()) {
            log.info("Redis cache miss for level: {}", alpha2);
            return Collections.emptyList();
        }

        log.info("Fetched {} countries from Redis list for level: {}", cachedList.size(), alpha2);
        return cachedList;
    }
}
