package com.example.apiconsumer.kafka.service;

import com.example.apiconsumer.kafka.dto.CountryDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import utils.DataUtils;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@EmbeddedKafka(
        partitions = 1,
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:9092",
                "port=9092"
        })
@ActiveProfiles("test")
class KafkaConsumerServiceTest {

    @Autowired
    private KafkaTemplate<String, CountryDto> testKafkaTemplate;

    @MockitoSpyBean
    private KafkaConsumerService kafkaConsumerService;

    @Value("${topic.name}")
    private String topic;

    @Test
    void shouldConsumeMessageSuccessfully() {
        // given
        CountryDto payload = DataUtils.getTuvaluDtoPersisted();

        //then
        testKafkaTemplate.send(topic, "TU", payload);

        //when
        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(kafkaConsumerService, times(1))
                        .consumeMessage(eq(payload), anyInt(), anyLong()));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public KafkaTemplate<String, CountryDto> testKafkaTemplate(ProducerFactory<String, CountryDto> pf) {
            return new KafkaTemplate<>(pf);
        }
    }
}