package com.example.apiconsumer.service;

import com.example.apiconsumer.dto.CountryDto;
import com.example.apiconsumer.kafka.service.KafkaConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class ConsumerServiceImplementation implements IConsumerService {

    private final KafkaConsumerService kafkaConsumerService;

    private final String topicName;

    public ConsumerServiceImplementation(KafkaConsumerService kafkaConsumerService, @Value("${topic.name}") String topicName) {
        this.kafkaConsumerService = kafkaConsumerService;
        this.topicName = topicName;
    }

    @Override
    public CountryDto pullFromBroker() {
        final CountryDto countryDto = kafkaConsumerService.receiveNextMessage(topicName);

        if (Objects.isNull(countryDto)) {
            log.info("IN pullFromBroker:  The message from Kafka has not been received");
            return null;
        }
        log.info("IN pullFromBroker:  The message has been received from the Kafka => {}", countryDto);

        return countryDto;
    }
}
