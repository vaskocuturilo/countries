package com.example.apiconsumer.service;

import com.example.apiconsumer.kafka.dto.CountryDto;
import com.example.apiconsumer.kafka.service.KafkaConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConsumerServiceImplementation implements IConsumerService {

    private final KafkaConsumerService kafkaConsumerService;

    public ConsumerServiceImplementation(KafkaConsumerService kafkaConsumerService) {
        this.kafkaConsumerService = kafkaConsumerService;
    }


    @Override
    public void triggerAsynchronousReceiveCountry(CountryDto country) {
        kafkaConsumerService.consumeMessage(country, 0, 0);

        log.info("The message {} has been send to the Kafka broker", country);
    }
}
