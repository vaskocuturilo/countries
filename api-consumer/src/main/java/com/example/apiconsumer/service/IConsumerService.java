package com.example.apiconsumer.service;


import com.example.apiconsumer.kafka.dto.CountryDto;

public interface IConsumerService {
    void triggerAsynchronousReceiveCountry(CountryDto country);
}