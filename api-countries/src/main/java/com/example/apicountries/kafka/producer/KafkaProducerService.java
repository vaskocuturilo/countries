package com.example.apicountries.kafka.producer;

import com.example.apicountries.dto.CountryDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class KafkaProducerService {

    private final String topicName;

    private final KafkaTemplate<String, CountryDto> kafkaTemplate;

    public KafkaProducerService(@Value("${topic.name}") String topicName, KafkaTemplate<String, CountryDto> kafkaTemplate) {
        this.topicName = topicName;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(final CountryDto country) {
        log.info("Sending country: {} to topic: {}", country, topicName);

        final String countryAlpha2 = country.getAlpha2();

        log.info("Attempting to send country: {} with key: {}", country.getAlpha2(), countryAlpha2);

        kafkaTemplate.send(topicName, countryAlpha2, country).whenComplete((result, exception) -> {
            if (Objects.isNull(exception)) {
                log.info("Successfully sent message to topic {} [partition: {}, offset: {}]",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send message: {} due to : {}", country, exception.getMessage());
            }
        });

        log.info("The message {} has been send to the topic {}", country, topicName);
    }
}
