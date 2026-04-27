package com.example.apicountries.service;

import com.example.apicountries.client.CountryApiClient;
import com.example.apicountries.dto.CountryDto;
import com.example.apicountries.dto.PageResponse;
import com.example.apicountries.entity.CountryEntity;
import com.example.apicountries.redis.service.CountryDocumentCacheService;
import com.example.apicountries.redis.service.CountryEntityCacheService;
import com.example.apicountries.repository.CountryJpaRepository;
import com.example.apicountries.repository.CountryMongoRepository;
import com.example.apicountries.utils.DataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryServiceTests {

    @Mock
    private CountryApiClient countryApiClient;

    @InjectMocks
    private CountryServiceImplementation countryService;

    @Mock
    private CountryJpaRepository countryRepository;

    @Mock
    private CountryMongoRepository countryMongoRepository;

    @Mock
    private CountryDocumentCacheService countryDocumentCacheService;

    @Mock
    private CountryEntityCacheService countryEntityCacheService;

    @Test
    @DisplayName("Test get all countries functionality")
    void givenOneCountry_whenGetAllCountries_thenOneCountryReturned() {
        //given
        final CountryEntity countryEntity = DataUtils.getTuvValuePersisted();

        final List<CountryEntity> countriesList = List.of(countryEntity);

        final Pageable pageable = PageRequest.of(0, 10, Sort.by("alpha2").ascending());
        final Page<CountryEntity> countryPage = new PageImpl<>(countriesList, pageable, countriesList.size());

        BDDMockito.given(countryRepository.findAll(pageable)).willReturn(countryPage);
        //when
        final PageResponse<CountryDto> result = countryService.getAllCountries(pageable);

        //then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);
        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.totalPages()).isEqualTo(1);
        assertThat(result.sortBy()).isEqualTo("alpha2");
        assertThat(result.direction()).isEqualTo("ASC");
    }

    @Test
    @DisplayName("Test getAllCountries returns empty list when no countries exist")
    void givenNoCountries_whenGetAllCountries_thenEmptyListReturned() {
        // given
        final List<CountryEntity> countriesList = List.of();

        final Pageable pageable = PageRequest.of(0, 1, Sort.by("alpha2").ascending());
        final Page<CountryEntity> countryPage = new PageImpl<>(countriesList,pageable,0);

        BDDMockito.given(countryRepository.findAll(pageable)).willReturn(countryPage);

        // when
        final PageResponse<CountryDto> result = countryService.getAllCountries(pageable);

        // then
        assertThat(result.content()).isEmpty();
    }

    @Test
    @DisplayName("Test get country by alphaCode functionality")
    void givenCountryAlphaCode_whenGetByAlpaCode_thenCountryReturned() {
        //given
        BDDMockito.given(countryDocumentCacheService.getCountryDocumentByAlpha2Code(anyString())).willReturn(Collections.emptyList());

        BDDMockito.given(countryEntityCacheService.getCountryEntityByAlpha2Code(anyString())).willReturn(Collections.emptyList());

        BDDMockito.given(countryMongoRepository.findByAlpha2(anyString())).willReturn(DataUtils.getTuvValueMongoTransient());

        //when
        final CountryDto countryDto = countryService.getCountryByAlphaCode("TU");

        //then
        assertThat(countryDto).isNotNull();
        verify(countryMongoRepository, times(1)).findByAlpha2("TU");
        verify(countryRepository, never()).findByAlpha2(anyString());
    }

    @Test
    @DisplayName("Test get country from JPA when all caches miss")
    void givenCountryInJpa_whenAllCachesMiss_thenReturnedFromJpa() {
        // given
        BDDMockito.given(countryDocumentCacheService.getCountryDocumentByAlpha2Code(anyString()))
                .willReturn(Collections.emptyList());

        BDDMockito.given(countryEntityCacheService.getCountryEntityByAlpha2Code(anyString()))
                .willReturn(Collections.emptyList());

        BDDMockito.given(countryMongoRepository.findByAlpha2(anyString())).willReturn(null);

        BDDMockito.given(countryRepository.findByAlpha2(anyString()))
                .willReturn(DataUtils.getTuvValuePersisted());

        // when
        final CountryDto result = countryService.getCountryByAlphaCode("TU");

        // then
        assertThat(result).isNotNull();
        verify(countryRepository, times(1)).findByAlpha2("TU");
        verify(countryEntityCacheService, times(1)).cacheCountryEntity(any());
    }

    @Test
    @DisplayName("Test get country from cache for document")
    void givenCountryDocumentCacheId_whenGetByAlpaCode_thenCountryReturnedFromCache() {
        //given
        BDDMockito.given(countryDocumentCacheService.getCountryDocumentByAlpha2Code(anyString()))
                .willReturn(List.of(DataUtils.getTuvValueMongoTransient()));

        //when
        final CountryDto countryDto = countryService.getCountryByAlphaCode("TU");

        assertThat(countryDto).isNotNull();
        verify(countryMongoRepository, never()).findByAlpha2(anyString());
        verify(countryRepository, never()).findByAlpha2(anyString());
    }

    @Test
    @DisplayName("Test get country from cache for entity")
    void givenCountryEntityCacheId_whenGetByAlpaCode_thenCountryReturnedFromCache() {
        //given
        BDDMockito.given(countryDocumentCacheService.getCountryDocumentByAlpha2Code(anyString()))
                .willReturn(Collections.emptyList());

        BDDMockito.given(countryEntityCacheService.getCountryEntityByAlpha2Code(anyString()))
                .willReturn(List.of(DataUtils.getTuVValueEntityTransient()));

        //when
        final CountryDto countryDto = countryService.getCountryByAlphaCode("TU");

        assertThat(countryDto).isNotNull();
        verify(countryMongoRepository, never()).findByAlpha2(anyString());
        verify(countryRepository, never()).findByAlpha2(anyString());
    }


    @Test
    @DisplayName("Test get country with incorrect alphaCode name functionality")
    void givenIncorrectAlphaCodeName_whenGetByID_thenExceptionIsThrown() {
        //given

        //when
        assertThrows(IllegalArgumentException.class, () -> countryService.getCountryByAlphaCode("Test"));

        //then
    }

    @Test
    @DisplayName("Test get country with null alphaCode throws exception")
    void givenNullAlphaCode_whenGetByAlphaCode_thenExceptionIsThrown() {
        //given

        //when
        assertThrows(IllegalArgumentException.class, () -> countryService.getCountryByAlphaCode(null));

        //then
    }

    @Test
    @DisplayName("Test get country with blank alphaCode throws exception")
    void givenBlankAlphaCode_whenGetByAlphaCode_thenExceptionIsThrown() {
        //given

        //when
        assertThrows(IllegalArgumentException.class, () -> countryService.getCountryByAlphaCode("  "));

        //then
    }

    @Test
    @DisplayName("Test get country with incorrect alphaCode functionality")
    void givenIncorrectAlphaCode_whenGetByID_thenExceptionIsThrown() {
        //given

        //when
        final CountryDto result = countryService.getCountryByAlphaCode("TT");
        //then

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Test initProcess with empty API response")
    void givenEmptyApiResponse_whenInitProcess_thenAbortMessageReturned() {
        // given
        BDDMockito.given(countryApiClient.getCountries()).willReturn(Collections.emptyList());

        // when
        final Map<String, String> result = countryService.initProcess();

        // then
        assertThat(result.get("message")).contains("aborted");
        verify(countryRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("Test initProcess with valid API response")
    void givenValidApiResponse_whenInitProcess_thenSuccessMessageReturned() {
        // given
        BDDMockito.given(countryApiClient.getCountries()).willReturn(List.of(DataUtils.getTuvValueDtoPersisted()));

        // when
        final Map<String, String> result = countryService.initProcess();

        // then
        assertThat(result.get("message")).contains("successfully");
        verify(countryRepository, times(1)).saveAll(any());
        verify(countryMongoRepository, times(1)).saveAll(any());
    }
}