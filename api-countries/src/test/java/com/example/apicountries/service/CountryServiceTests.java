package com.example.apicountries.service;

import com.example.apicountries.dto.CountryDto;
import com.example.apicountries.entity.CountryEntity;
import com.example.apicountries.repository.CountryJpaRepository;
import com.example.apicountries.utils.DataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CountryServiceTests {

    @InjectMocks
    private CountryServiceImplementation countryService;

    @Mock
    private CountryJpaRepository countryRepository;

    @Test
    @DisplayName("Test get all countries functionality")
    void givenOneCountry_whenGetAllCountries_thenOneCountryReturned() {
        //given
        final CountryEntity countryEntity = DataUtils.getTuvaluPersisted();

        final List<CountryEntity> countriesList = List.of(countryEntity);

        BDDMockito.given(countryRepository.findAll()).willReturn(countriesList);
        //when
        final List<CountryDto> allCountries = countryService.getAllCountries();

        //then
        assertThat(CollectionUtils.isEmpty(countriesList)).isFalse();
        assertThat(allCountries).hasSize(1);
    }
}