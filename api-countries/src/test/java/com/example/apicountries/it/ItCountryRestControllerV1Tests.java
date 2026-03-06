package com.example.apicountries.it;

import com.example.apicountries.dto.CountryDto;
import com.example.apicountries.entity.CountryDocument;
import com.example.apicountries.entity.CountryEntity;
import com.example.apicountries.repository.CountryJpaRepository;
import com.example.apicountries.repository.CountryMongoRepository;
import com.example.apicountries.utils.DataUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ItCountryRestControllerV1Tests extends AbstractRestControllerBaseTest {

    @Autowired
    private CountryJpaRepository countryJpaRepository;

    @Autowired
    private CountryMongoRepository countryMongoRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String ENDPOINT_PATH = "/api/v1/countries";

    @BeforeEach
    void setUp() {
        countryMongoRepository.deleteAll();
        countryJpaRepository.deleteAll();
    }


    @Test
    @DisplayName("Test get country by alpha code functionality")
    void givenAlphaCode_whenGetByAlphaCode_thenSuccessResponse() throws Exception {
        //given
        final CountryEntity countryEntity = DataUtils.getTuvValuePersisted();

        final CountryDocument countryDocument = DataUtils.getTuvValueMongoTransient();

        countryJpaRepository.save(countryEntity);
        countryMongoRepository.save(countryDocument);

        //when
        final ResultActions result = mockMvc.perform(get(ENDPOINT_PATH + "/" + countryDocument.getAlpha2())
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.cca2").isNotEmpty())
                .andExpect(jsonPath("$.cca3", CoreMatchers.notNullValue()))
                .andExpect(jsonPath("$.capital", CoreMatchers.notNullValue()));
    }

    @Test
    @DisplayName("Test get country by alpha code - success from JPA when mongo empty")
    void givenAlphaCode_whenMongoEmpty_thenReturnFromJpa() throws Exception {
        // given
        final CountryEntity countryEntity = DataUtils.getTuvValuePersisted();
        countryJpaRepository.save(countryEntity);

        // when
        final ResultActions result = mockMvc.perform(
                get(ENDPOINT_PATH + "/" + countryEntity.getAlpha2())
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.alpha2", CoreMatchers.is(countryEntity.getAlpha2())))
                .andExpect(jsonPath("$.alpha3", CoreMatchers.notNullValue()))
                .andExpect(jsonPath("$.capital", CoreMatchers.notNullValue()));
    }

    @Test
    @DisplayName("Test get country by alpha code - not found returns 404")
    void givenNonExistentAlphaCode_whenGetByAlphaCode_thenNotFoundResponse() throws Exception {
        // given

        // when
        final ResultActions result = mockMvc.perform(
                get(ENDPOINT_PATH + "/WW")
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$.message",
                        CoreMatchers.is("The country with the alphaCode = WW is not found")));
    }

    @Test
    @DisplayName("Test get country by alpha code - invalid code too long returns 400")
    void givenInvalidAlphaCodeTooLong_whenGetByAlphaCode_thenBadRequest() throws Exception {
        // given

        // when
        final ResultActions result = mockMvc.perform(
                get(ENDPOINT_PATH + "/TEST")
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.message",
                        CoreMatchers.is("alphaCode must be ISO-2 code")));
    }

    @Test
    @DisplayName("Test get all countries - returns list with one country")
    void givenOneCountry_whenGetAll_thenSuccessResponse() throws Exception {
        // given
        final CountryEntity countryEntity = DataUtils.getTuvValuePersisted();
        countryJpaRepository.save(countryEntity);

        // when
        final ResultActions result = mockMvc.perform(
                get(ENDPOINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$[*]", hasSize(1)))
                .andExpect(jsonPath("$[0].alpha2").isNotEmpty())
                .andExpect(jsonPath("$[0].alpha3", CoreMatchers.notNullValue()))
                .andExpect(jsonPath("$[0].capital", CoreMatchers.notNullValue()));
    }

    @Test
    @DisplayName("Test get all countries - returns empty list when no data")
    void givenNoCountries_whenGetAll_thenEmptyListResponse() throws Exception {
        // given

        // when
        final ResultActions result = mockMvc.perform(
                get(ENDPOINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$[*]", hasSize(0)));
    }

    @Test
    @DisplayName("Test get all countries - returns correct count with multiple countries")
    void givenMultipleCountries_whenGetAll_thenReturnsAll() throws Exception {
        // given
        countryJpaRepository.saveAll(List.of(
                DataUtils.getTuvValuePersisted(),
                DataUtils.getZimbabwePersisted()
        ));

        // when
        final ResultActions result = mockMvc.perform(
                get(ENDPOINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$[*]", hasSize(2)));
    }

    @Test
    @DisplayName("Test init process endpoint - returns 200 with message")
    void whenInitProcess_thenReturnsMessage() throws Exception {
        //given

        // when
        final ResultActions result = mockMvc.perform(
                post(ENDPOINT_PATH + "/process")
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.message", CoreMatchers.notNullValue()));
    }

    @Test
    @DisplayName("Test send country to Kafka - success")
    void givenCountryDto_whenSend_thenSuccessResponse() throws Exception {
        // given
        final CountryDto country = DataUtils.getTuvValueDtoPersisted();

        // when
        final ResultActions result = mockMvc.perform(
                post(ENDPOINT_PATH + "/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(country)));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.message",
                        CoreMatchers.is("Message confirmed by Kafka")));
    }
}
