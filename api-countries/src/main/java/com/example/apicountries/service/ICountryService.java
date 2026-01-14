package com.example.apicountries.service;

import com.example.apicountries.dto.CountryDto;

import java.util.List;

public interface ICountryService {

    List<CountryDto> getAllCountries();

    CountryDto getCountryByAlphaCode(String alphaCode);

    void triggerAsynchronousSendCountry(CountryDto country);

    void initProcess();
}