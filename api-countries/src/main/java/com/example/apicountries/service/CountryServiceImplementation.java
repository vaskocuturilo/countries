package com.example.apicountries.service;

import com.example.apicountries.client.CountryApiClient;
import com.example.apicountries.dto.CountryDto;
import com.example.apicountries.entity.CountryDocument;
import com.example.apicountries.entity.CountryEntity;
import com.example.apicountries.kafka.producer.KafkaProducerService;
import com.example.apicountries.repository.CountryJpaRepository;
import com.example.apicountries.repository.CountryMongoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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

    public CountryServiceImplementation(CountryJpaRepository countryJpaRepository,
                                        CountryMongoRepository countryMongoRepository,
                                        CountryApiClient countryApiClient, KafkaProducerService kafkaProducerService) {
        this.countryJpaRepository = countryJpaRepository;
        this.countryMongoRepository = countryMongoRepository;
        this.countryApiClient = countryApiClient;
        this.kafkaProducerService = kafkaProducerService;
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
        final CountryDocument mongoCountry = countryMongoRepository.findByAlpha2(alphaCode);

        if (Objects.nonNull(mongoCountry)) {
            return CountryDto.fromMongoDocument(mongoCountry);
        }

        final CountryEntity jpaCountry = countryJpaRepository.findByAlpha2(alphaCode);

        if (Objects.isNull(jpaCountry)) {
            throw new EntityNotFoundException("The country with the alphaCode =  %s is not found".formatted(alphaCode));
        }

        return CountryDto.fromJpaEntity(jpaCountry);
    }

    @Override
    public void triggerAsynchronousSendCountry(CountryDto country) {
        kafkaProducerService.sendMessage(country);

        log.info("The message {} has been send to the Kafka broker", country);
    }
}
