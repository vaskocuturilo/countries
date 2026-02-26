package com.example.apiconsumer.kafka.service;

import com.example.apiconsumer.dto.CountryDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Objects;

@Service
@Slf4j
public class KafkaConsumerService {

    private final DefaultKafkaConsumerFactory<String, CountryDto> defaultKafkaConsumerFactory;

    public KafkaConsumerService(DefaultKafkaConsumerFactory<String, CountryDto> defaultKafkaConsumerFactory) {
        this.defaultKafkaConsumerFactory = defaultKafkaConsumerFactory;
    }

    public CountryDto receiveNextMessage(String topic) {
        try (Consumer<String, CountryDto> consumer = defaultKafkaConsumerFactory.createConsumer()) {
            consumer.subscribe(Collections.singletonList(topic));

            ConsumerRecords<String, CountryDto> records = consumer.poll(Duration.ofSeconds(5));

            if (Objects.nonNull(records)) {

                ConsumerRecord<String, CountryDto> receive = records.iterator().next();

                consumer.commitSync();

                log.info("IN receiveNextMessage:  The message from Kafka has been received => {}", receive.value());

                return receive.value();
            }
        }
        log.info("IN receiveNextMessage:  The message from Kafka has not been received, return null");

        return null;
    }
}