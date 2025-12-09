package com.example.apicountries.repository;

import com.example.apicountries.entity.CountryEntity;
import com.example.apicountries.utils.DataUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest(properties = {
        "spring.jpa.properties.javax.persistence.validation.mode=none"
})
class CountryJpaRepositoryTest {

    @Autowired
    CountryJpaRepository countryJpaRepository;

    @BeforeEach
    void setUp() {
        countryJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Test findByAlpha2 country functionality")
    void givenCountryCreated_whenGetByAlpha2_thenCountryIsReturned() {
        //given
        final CountryEntity countryToCreate = DataUtils.getTuvaluTransient();

        countryJpaRepository.save(countryToCreate);


        //when
        final CountryEntity existCountry = countryJpaRepository.findByAlpha2(countryToCreate.getAlpha2());

        //then
        assertThat(existCountry).isNotNull();
        assertThat(existCountry.getAlpha2()).isEqualTo("TU");
    }

    @Test
    @DisplayName("Test the country was not found functionality")
    void givenCountryIsNotCreated_whenGetByAlpha2_thenOptionalIsEmpty() {
        //given

        //when
        final CountryEntity existCountry = countryJpaRepository.findByAlpha2("TEST");

        //then
        assertThat(existCountry).isNull();
    }

    @Test
    @DisplayName("Test the countries find all functionality")
    void givenCountriesAreStored_whenFindAll_thenAllCountriesAreReturned() {
        //given
        final CountryEntity tuvaluCountry = DataUtils.getTuvaluTransient();

        countryJpaRepository.saveAll(List.of(tuvaluCountry));

        //when
        final List<CountryEntity> developers = countryJpaRepository.findAll();

        //then
        assertThat(CollectionUtils.isEmpty(developers)).isFalse();
        assertThat(developers).hasSize(1).contains(tuvaluCountry);
    }
}