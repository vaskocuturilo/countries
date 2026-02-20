package com.example.apiconsumer.service;


import com.example.apiconsumer.dto.CountryDto;

public interface IConsumerService {
    CountryDto pullFromBroker();
}