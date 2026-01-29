package com.example.apicountries.kafka.producer;

import com.example.apicountries.dto.CountryDto;
import com.example.apicountries.utils.DataUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, CountryDto> kafkaTemplate;

    private KafkaProducerService kafkaProducerService;

    private final String topicName = "test-countries";

    @BeforeEach
    void setUp() {
        kafkaProducerService = new KafkaProducerService(topicName, kafkaTemplate);
    }

    @Test
    void shouldSendMessageSuccessfully() {
        // given
        CountryDto country = DataUtils.getTuvaluDtoPersisted();

        // when
        CompletableFuture<SendResult<String, CountryDto>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(anyString(), anyString(), any(CountryDto.class))).thenReturn(future);

        kafkaProducerService.sendMessage(country);

        //then
        verify(kafkaTemplate, times(1)).send(topicName, "TU", country);
    }
}