package com.example.apicountries.service;

import com.example.apicountries.dto.CountryDto;
import com.example.apicountries.dto.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.support.SendResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface ICountryService {

    PageResponse<CountryDto> getAllCountries(Pageable pageable);

    CountryDto getCountryByAlphaCode(String alphaCode);

    CompletableFuture<SendResult<String, CountryDto>> triggerSend(CountryDto country);

    Map<String, String> initProcess();
}