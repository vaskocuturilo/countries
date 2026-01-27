package com.example.apiconsumer.kafka.service;

import com.example.apiconsumer.kafka.dto.CountryDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {

    @KafkaListener(topics = "${topic.name}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void consumeMessage(@Payload CountryDto country, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition, @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("Successfully consumed from Topic: {}, Partition: {}, Offset: {}",
                "${topic.name}", partition, offset);

        log.info("Processing Country Data: {} ({})", country.getAlpha2(), country.getAlpha3());
    }
}