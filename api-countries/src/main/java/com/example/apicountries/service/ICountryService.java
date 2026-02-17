package com.example.apicountries.service;

import com.example.apicountries.dto.CountryDto;
import org.springframework.kafka.support.SendResult;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface ICountryService {

    List<CountryDto> getAllCountries();

    CountryDto getCountryByAlphaCode(String alphaCode);

    CompletableFuture<SendResult<String, CountryDto>> triggerSend(CountryDto country);

    Map<String, String> initProcess();
}