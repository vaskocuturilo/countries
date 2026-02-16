package com.example.apicountries.service;

import com.example.apicountries.dto.CountryDto;

import java.util.List;
import java.util.Map;

public interface ICountryService {

    List<CountryDto> getAllCountries();

    CountryDto getCountryByAlphaCode(String alphaCode);

    void triggerAsynchronousSendCountry(CountryDto country);

    Map<String, String> initProcess();
}