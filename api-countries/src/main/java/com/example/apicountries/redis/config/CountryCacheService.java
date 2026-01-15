package com.example.apicountries.redis.config;

import com.example.apicountries.entity.CountryEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class CountryCacheService {
    private final RedisTemplate<String, CountryEntity> redisTemplate;

    public CountryCacheService(RedisTemplate<String, CountryEntity> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void cacheCountry(CountryEntity country) {
        final String alpha2 = "alpha2:" + country.getAlpha2();
        final String alpha3 = "alpha3:" + country.getAlpha3();


        redisTemplate.opsForList().rightPush(alpha2, country);
        redisTemplate.opsForValue().set(alpha3, country);
        log.info("Cached country [{}] to Redis under list [{}]", country.getAlpha2(), alpha3);

    }

    public List<CountryEntity> getCountryByAlpha2Code(String alpha2) {
        String listKey = "alpha2::" + alpha2;
        List<CountryEntity> cachedList = redisTemplate.opsForList().range(listKey, 0, -1);

        if (cachedList == null || cachedList.isEmpty()) {
            log.info("Redis cache miss for level: {}", alpha2);
            return Collections.emptyList();
        }

        log.info("Fetched {} countries from Redis list for level: {}", cachedList.size(), alpha2);
        return cachedList;
    }
}
