package com.example.apiconsumer.kafka;

import com.example.apiconsumer.dto.CountryDto;
import com.example.apiconsumer.kafka.service.KafkaConsumerService;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import utils.DataUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;

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

        // when - send message
        testKafkaTemplate.send(topic, "TU", payload);

        // then - explicitly call receiveNextMessage and verify the result
        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    CountryDto result = kafkaConsumerService.receiveNextMessage(topic);

                    assertThat(result).isNotNull();
                    assertThat(result.getAlpha2()).isEqualTo(payload.getAlpha2());
                    assertThat(result.getAlpha3()).isEqualTo(payload.getAlpha3());
                });
    }

    @Test
    void shouldReturnNullWhenNoMessageAvailable() {
        CountryDto result = kafkaConsumerService.receiveNextMessage(topic);

        // then
        assertThat(result).isNull();
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public ProducerFactory<String, CountryDto> testProducerFactory(
                @Value("${spring.kafka.consumer.bootstrap-servers}") String bootstrapServers) {

            Map<String, Object> props = new HashMap<>();
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

            return new DefaultKafkaProducerFactory<>(props);
        }

        @Bean
        public KafkaTemplate<String, CountryDto> testKafkaTemplate(
                ProducerFactory<String, CountryDto> testProducerFactory) {
            return new KafkaTemplate<>(testProducerFactory);
        }
    }
}