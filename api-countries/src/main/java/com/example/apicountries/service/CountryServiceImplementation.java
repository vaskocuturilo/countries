package com.example.apicountries.service;

import com.example.apicountries.client.CountryApiClient;
import com.example.apicountries.dto.CountryDto;
import com.example.apicountries.entity.CountryDocument;
import com.example.apicountries.entity.CountryEntity;
import com.example.apicountries.kafka.producer.KafkaProducerService;
import com.example.apicountries.redis.service.CountryDocumentCacheService;
import com.example.apicountries.redis.service.CountryEntityCacheService;
import com.example.apicountries.repository.CountryJpaRepository;
import com.example.apicountries.repository.CountryMongoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class CountryServiceImplementation implements ICountryService {

    private final CountryJpaRepository countryJpaRepository;
    private final CountryMongoRepository countryMongoRepository;
    private final CountryApiClient countryApiClient;
    private final KafkaProducerService kafkaProducerService;
    private final CountryEntityCacheService countryEntityCacheService;
    private final CountryDocumentCacheService countryDocumentCacheService;

    private static final SecureRandom RANDOM = new SecureRandom();

    public CountryServiceImplementation(CountryJpaRepository countryJpaRepository,
                                        CountryMongoRepository countryMongoRepository,
                                        CountryApiClient countryApiClient, KafkaProducerService kafkaProducerService, CountryEntityCacheService countryEntityCacheService, CountryDocumentCacheService countryDocumentCacheService) {
        this.countryJpaRepository = countryJpaRepository;
        this.countryMongoRepository = countryMongoRepository;
        this.countryApiClient = countryApiClient;
        this.kafkaProducerService = kafkaProducerService;
        this.countryEntityCacheService = countryEntityCacheService;
        this.countryDocumentCacheService = countryDocumentCacheService;
    }

    public void initProcess() {
        final List<CountryDto> countryDtoList = countryApiClient.getCountries();

        final List<CountryEntity> countryEntityList = countryDtoList
                .stream()
                .map(CountryDto::toJpaEntity)
                .toList();

        final List<CountryDocument> countryDocumentList = countryDtoList.stream()
                .map(CountryDto::toMongoDocument).toList();


        countryJpaRepository.deleteAll();
        countryMongoRepository.deleteAll();

        countryJpaRepository.saveAll(countryEntityList);
        countryMongoRepository.saveAll(countryDocumentList);
    }

    public List<CountryDto> getAllCountries() {
        final List<CountryEntity> countryEntities = countryJpaRepository.findAll();

        if (CollectionUtils.isEmpty(countryEntities)) {
            return Collections.emptyList();
        }

        return countryEntities.stream().map(CountryDto::fromJpaEntity).toList();
    }

    public CountryDto getCountryByAlphaCode(final String alphaCode) {
        final List<CountryDocument> cachedDocumentCountries = countryDocumentCacheService.getCountryDocumentByAlpha2Code(alphaCode);

        if (!cachedDocumentCountries.isEmpty()) {

            List<CountryDocument> available = cachedDocumentCountries.stream()
                    .filter(country -> country.getAlpha2().equals(alphaCode))
                    .toList();

            if (!available.isEmpty()) {
                final CountryDocument selectedFromCache = available.get(RANDOM.nextInt(available.size()));

                log.info("CountryDocument [{}] fetched from Redis for alphaCode [{}]", selectedFromCache.getAlpha2(), alphaCode);

                return CountryDto.fromMongoDocument(selectedFromCache);
            }
        }

        final List<CountryEntity> cachedCountries = countryEntityCacheService.getCountryEntityByAlpha2Code(alphaCode);

        if (!cachedCountries.isEmpty()) {
            List<CountryEntity> available = cachedCountries.stream()
                    .filter(country -> country.getAlpha2().equals(alphaCode))
                    .toList();

            if (!available.isEmpty()) {
                CountryEntity selected = available.get(RANDOM.nextInt(available.size()));

                log.info("CountryEntity [{}] fetched from Redis for alphaCode [{}]", selected.getAlpha2(), alphaCode);

                return CountryDto.fromJpaEntity(selected);
            }
        }

        log.info("Fetching country document from DB for level [{}] (cache empty or all questions used)", alphaCode);
        final CountryDocument mongoCountry = countryMongoRepository.findByAlpha2(alphaCode);

        if (Objects.nonNull(mongoCountry)) {
            countryDocumentCacheService.cacheCountryDocument(mongoCountry);

            return CountryDto.fromMongoDocument(mongoCountry);
        }

        log.info("Fetching country entity from DB for level [{}] (cache empty or all questions used)", alphaCode);
        final CountryEntity dbCountry = countryJpaRepository.findByAlpha2(alphaCode);

        if (Objects.isNull(dbCountry)) {
            throw new EntityNotFoundException("The country with the alphaCode =  %s is not found".formatted(alphaCode));
        }

        countryEntityCacheService.cacheCountryEntity(dbCountry);

        return CountryDto.fromJpaEntity(dbCountry);
    }

    @Override
    public void triggerAsynchronousSendCountry(CountryDto country) {
        kafkaProducerService.sendMessage(country);

        log.info("The message {} has been send to the Kafka broker", country);
    }
}
