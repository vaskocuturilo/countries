package com.example.apicountries.kafka.producer;

import com.example.apicountries.dto.CountryDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProducerService {

    @Value("${topic.name}")
    private String topicName;

    private final KafkaTemplate<String, CountryDto> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, CountryDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void sendMessage(final CountryDto country) {
        final Message<CountryDto> message = MessageBuilder
                .withPayload(country)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .build();

        kafkaTemplate.send(message);

        log.info("The message {} has been send to the topic {}", country, topicName);
    }
}
