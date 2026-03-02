package com.example.apicountries.repository;

import com.example.apicountries.entity.CountryDocument;
import com.example.apicountries.utils.DataUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.CollectionUtils;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataMongoTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class CountryMongoRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @Container
    @ServiceConnection
    static MongoDBContainer mongo = new MongoDBContainer("mongo:8");

    @Autowired
    CountryMongoRepository countryMongoRepository;

    @BeforeEach
    void setUp() {
        countryMongoRepository.deleteAll();
    }

    @Test
    @DisplayName("Test findByAlpha2 country functionality")
    void givenCountryCreated_whenGetByAlpha2_thenCountryIsReturned() {
        //given
        final CountryDocument countryToCreate = DataUtils.getTuvaluMongoTransient();

        countryMongoRepository.save(countryToCreate);


        //when
        final CountryDocument existCountry = countryMongoRepository.findByAlpha2(countryToCreate.getAlpha2());

        //then
        assertThat(existCountry).isNotNull();
        assertThat(existCountry.getAlpha2()).isEqualTo("TU");
    }

    @Test
    @DisplayName("Test the country was not found functionality")
    void givenCountryIsNotCreated_whenGetByAlpha2_thenOptionalIsEmpty() {
        //given

        //when
        final CountryDocument existCountry = countryMongoRepository.findByAlpha2("TEST");

        //then
        assertThat(existCountry).isNull();
    }

    @Test
    @DisplayName("Test the countries find all functionality")
    void givenCountriesAreStored_whenFindAll_thenAllCountriesAreReturned() {
        //given
        final CountryDocument tuvaluCountry = DataUtils.getTuvaluMongoTransient();

        countryMongoRepository.saveAll(List.of(tuvaluCountry));

        //when
        final List<CountryDocument> developers = countryMongoRepository.findAll();

        //then
        assertThat(CollectionUtils.isEmpty(developers)).isFalse();
        assertThat(developers).hasSize(1).contains(tuvaluCountry);
    }
}