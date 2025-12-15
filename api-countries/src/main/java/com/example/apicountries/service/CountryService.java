package com.example.apicountries.service;

import com.example.apicountries.client.CountryApiClient;
import com.example.apicountries.dto.CountryDto;
import com.example.apicountries.entity.CountryDocument;
import com.example.apicountries.entity.CountryEntity;
import com.example.apicountries.repository.CountryJpaRepository;
import com.example.apicountries.repository.CountryMongoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryService {

    private final CountryJpaRepository countryJpaRepository;
    private final CountryMongoRepository countryMongoRepository;
    private final CountryApiClient countryApiClient;

    public CountryService(CountryJpaRepository countryJpaRepository, CountryMongoRepository countryMongoRepository, CountryApiClient countryApiClient) {
        this.countryJpaRepository = countryJpaRepository;
        this.countryMongoRepository = countryMongoRepository;
        this.countryApiClient = countryApiClient;
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
}
