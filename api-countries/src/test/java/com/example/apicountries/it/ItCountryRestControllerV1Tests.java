package com.example.apicountries.it;

import com.example.apicountries.entity.CountryDocument;
import com.example.apicountries.entity.CountryEntity;
import com.example.apicountries.repository.CountryJpaRepository;
import com.example.apicountries.repository.CountryMongoRepository;
import com.example.apicountries.utils.DataUtils;
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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    private static final String ENDPOINT_PATH = "/api/v1/countries";

    @BeforeEach
    void setUp() {
        countryMongoRepository.deleteAll();
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
    @DisplayName("Test get country with incorrect alpa code functionality")
    void givenIncorrectAlpHaCode_whenGetByAlphaCode_thenErrorResponse() throws Exception {
        final String alphaCode = "TEST";
        //given

        //when
        final ResultActions result = mockMvc.perform(get(ENDPOINT_PATH + "/" + alphaCode)
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.message", CoreMatchers.is("alphaCode must be ISO-2 code")));

    }

    @Test
    @DisplayName("Test get all countries functionality")
    void givenThreeDevelopers_whenGetByAll_thenSuccessResponse() throws Exception {
        //given
        final CountryEntity countryEntity = DataUtils.getTuvValuePersisted();
        final CountryDocument countryDocument = DataUtils.getTuvValueMongoTransient();

        countryJpaRepository.save(countryEntity);
        countryMongoRepository.save(countryDocument);

        //when
        final ResultActions result = mockMvc.perform(get(ENDPOINT_PATH)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$[0].cca2").isNotEmpty())
                .andExpect(jsonPath("$[0].cca3", CoreMatchers.notNullValue()))
                .andExpect(jsonPath("$[0].capital", CoreMatchers.notNullValue()))
                .andExpect(jsonPath("$[*]", hasSize(1)));
    }
}
