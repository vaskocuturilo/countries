package com.example.apicountries.service;

import com.example.apicountries.client.CountryApiClient;
import com.example.apicountries.dto.CountryDto;
import com.example.apicountries.dto.PageResponse;
import com.example.apicountries.entity.CountryDocument;
import com.example.apicountries.entity.CountryEntity;
import com.example.apicountries.kafka.producer.KafkaProducerService;
import com.example.apicountries.redis.service.CountryDocumentCacheService;
import com.example.apicountries.redis.service.CountryEntityCacheService;
import com.example.apicountries.repository.CountryJpaRepository;
import com.example.apicountries.repository.CountryMongoRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class CountryServiceImplementation implements ICountryService {

    private final CountryJpaRepository countryJpaRepository;
    private final CountryMongoRepository countryMongoRepository;
    private final CountryApiClient countryApiClient;
    private final KafkaProducerService kafkaProducerService;
    private final CountryEntityCacheService countryEntityCacheService;
    private final CountryDocumentCacheService countryDocumentCacheService;

    private static final String MESSAGE_KEY = "message";

    public CountryServiceImplementation(CountryJpaRepository countryJpaRepository,
                                        CountryMongoRepository countryMongoRepository,
                                        CountryApiClient countryApiClient, KafkaProducerService kafkaProducerService,
                                        CountryEntityCacheService countryEntityCacheService,
                                        CountryDocumentCacheService countryDocumentCacheService) {
        this.countryJpaRepository = countryJpaRepository;
        this.countryMongoRepository = countryMongoRepository;
        this.countryApiClient = countryApiClient;
        this.kafkaProducerService = kafkaProducerService;
        this.countryEntityCacheService = countryEntityCacheService;
        this.countryDocumentCacheService = countryDocumentCacheService;
    }

    @Transactional
    public Map<String, String> initProcess() {
        final List<CountryDto> countryDtos = countryApiClient.getCountries();

        if (Objects.isNull(countryDtos) || countryDtos.isEmpty()) {
            return Map.of(MESSAGE_KEY, "Process aborted: No data received from API");
        }

        final List<CountryEntity> countryEntityList = countryDtos.stream().map(CountryDto::toJpaEntity).toList();

        final List<CountryDocument> countryDocumentList = countryDtos.stream()
                .map(CountryDto::toMongoDocument).toList();

        try {
            countryJpaRepository.deleteAll();
            countryJpaRepository.saveAll(countryEntityList);

            countryMongoRepository.deleteAll();
            countryMongoRepository.saveAll(countryDocumentList);

            return Map.of(MESSAGE_KEY, "Process completed successfully");
        } catch (Exception exception) {
            return Map.of(MESSAGE_KEY, "Process failed during database sync: " + exception.getMessage());
        }
    }

    public PageResponse<CountryDto> getAllCountries(final Pageable pageable) {
        Page<CountryEntity> countries = countryJpaRepository.findAll(pageable);

        final Page<CountryDto> countryDtos = countries.map(CountryDto::fromJpaEntity);

        return new PageResponse<>
                (countryDtos.getContent(),
                        countryDtos.getNumber(),
                        countryDtos.getSize(),
                        countryDtos.getTotalElements(),
                        countryDtos.getTotalPages(), resolveSortBy(pageable), resolveDirection(pageable));
    }

    public CountryDto getCountryByAlphaCode(final String alphaCode) {
        final String normalized = validateAlphaCode(alphaCode);

        final CountryDto fromDocumentCache = getFromDocumentCache(normalized);

        if (Objects.nonNull(fromDocumentCache)) return fromDocumentCache;

        final CountryDto fromEntityCache = getFromEntityCache(normalized);

        if (Objects.nonNull(fromEntityCache)) return fromEntityCache;

        final CountryDto fromMongo = getFromMongo(normalized);

        if (Objects.nonNull(fromMongo)) return fromMongo;

        return getFromJpa(normalized);
    }

    @Override
    public CompletableFuture<SendResult<String, CountryDto>> triggerSend(CountryDto country) {
        log.info("The message {} has been send to the Kafka broker", country);

        return kafkaProducerService.sendMessage(country);
    }

    private CountryDto getFromDocumentCache(String alphaCode) {
        List<CountryDocument> docs =
                countryDocumentCacheService.getCountryDocumentByAlpha2Code(alphaCode);

        if (!docs.isEmpty()) {
            if (docs.size() > 1) {
                log.warn("Multiple documents found in cache for alphaCode [{}], using first", alphaCode);
            }

            CountryDocument selected = docs.getFirst();
            log.info("CountryDocument [{}] fetched from Redis for alphaCode [{}]",
                    selected.getAlpha2(), alphaCode);

            return CountryDto.fromMongoDocument(selected);
        }

        return null;
    }

    private CountryDto getFromMongo(String alphaCode) {
        log.info("Fetching country document from DB for alphaCode [{}] (cache miss)", alphaCode);

        final CountryDocument mongoCountry = countryMongoRepository.findByAlpha2(alphaCode);

        if (Objects.nonNull(mongoCountry)) {
            countryDocumentCacheService.cacheCountryDocument(mongoCountry);

            return CountryDto.fromMongoDocument(mongoCountry);
        }

        return null;
    }

    private CountryDto getFromEntityCache(String alphaCode) {
        List<CountryEntity> entities =
                countryEntityCacheService.getCountryEntityByAlpha2Code(alphaCode);

        if (!entities.isEmpty()) {
            if (entities.size() > 1) {
                log.warn("Multiple entities found in cache for alphaCode [{}], using first", alphaCode);
            }

            CountryEntity selected = entities.getFirst();
            log.info("CountryEntity [{}] fetched from Redis for alphaCode [{}]",
                    selected.getAlpha2(), alphaCode);

            return CountryDto.fromJpaEntity(selected);
        }

        return null;
    }

    private CountryDto getFromJpa(String alphaCode) {
        log.info("Fetching country entity from DB for alphaCode [{}] (cache miss)", alphaCode);
        final CountryEntity dbCountry = countryJpaRepository.findByAlpha2(alphaCode);

        if (Objects.nonNull(dbCountry)) {
            countryEntityCacheService.cacheCountryEntity(dbCountry);
        }

        return CountryDto.fromJpaEntity(dbCountry);
    }

    private String validateAlphaCode(final String alphaCode) {
        if (alphaCode == null || alphaCode.isBlank()) {
            throw new IllegalArgumentException("alphaCode must not be null or blank");
        }

        final String normalized = alphaCode.trim().toUpperCase(Locale.ROOT);

        if (normalized.length() != 2) {
            throw new IllegalArgumentException("alphaCode must be ISO-2 code");
        }

        return normalized;
    }

    private String resolveSortBy(Pageable pageable) {
        return pageable.getSort().stream()
                .findFirst()
                .map(Sort.Order::getProperty)
                .orElse("unsorted");

    }

    private String resolveDirection(Pageable pageable) {
        return pageable.getSort().stream()
                .findFirst()
                .map(order -> order.getDirection().name())
                .orElse("ASC");

    }
}
