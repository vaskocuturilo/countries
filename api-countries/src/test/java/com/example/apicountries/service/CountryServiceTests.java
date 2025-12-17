package com.example.apicountries.service;

import com.example.apicountries.dto.CountryDto;
import com.example.apicountries.entity.CountryEntity;
import com.example.apicountries.repository.CountryJpaRepository;
import com.example.apicountries.repository.CountryMongoRepository;
import com.example.apicountries.utils.DataUtils;
import jakarta.persistence.EntityNotFoundException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryServiceTests {

    @InjectMocks
    private CountryServiceImplementation countryService;

    @Mock
    private CountryJpaRepository countryRepository;

    @Mock
    private CountryMongoRepository countryMongoRepository;

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

    @Test
    @DisplayName("Test get country by alphaCode functionality")
    void givenCountryAlphaCode_whenGetByAlpaCode_thenCountryReturned() {
        //given
        BDDMockito.given(countryMongoRepository.findByAlpha2(anyString())).willReturn(DataUtils.getTuvaluMongoTransient());

        //when
        final CountryDto countryDto = countryService.getCountryByAlphaCode("TU");

        //then
        assertThat(countryDto).isNotNull();
        verify(countryMongoRepository, times(1)).findByAlpha2("TU");
        verify(countryRepository, never()).findByAlpha2(anyString());
    }

    @Test
    @DisplayName("Test get country with incorrect alphaCode functionality")
    void givenIncorrectId_whenGetByID_thenExceptionIsThrown() {
        //given

        //when
        assertThrows(EntityNotFoundException.class, () -> countryService.getCountryByAlphaCode("Test"));

        //then
    }
}