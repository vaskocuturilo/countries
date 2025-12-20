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


    private static final String ENDPOINT_PATH = "/api/v1/";

    @BeforeEach
    void setUp() {
        countryMongoRepository.deleteAll();
    }


    @Test
    @DisplayName("Test get country by alpha code functionality")
    void givenAlphaCode_whenGetByAlphaCode_thenSuccessResponse() throws Exception {
        //given
        final CountryEntity countryEntity = DataUtils.getTuvaluPersisted();

        final CountryDocument countryDocument = DataUtils.getTuvaluMongoTransient();

        countryJpaRepository.save(countryEntity);
        countryMongoRepository.save(countryDocument);

        //when
        final ResultActions result = mockMvc.perform(get(ENDPOINT_PATH + "/" + countryDocument.getAlpha2())
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.alpha2").isNotEmpty())
                .andExpect(jsonPath("$.alpha2", CoreMatchers.notNullValue()))
                .andExpect(jsonPath("$.alpha3", CoreMatchers.notNullValue()))
                .andExpect(jsonPath("$.capital", CoreMatchers.notNullValue()))
                .andExpect(jsonPath("$.region", CoreMatchers.notNullValue()))
                .andExpect(jsonPath("$.subregion", CoreMatchers.notNullValue()))
                .andExpect(jsonPath("$.area", CoreMatchers.notNullValue()))
                .andExpect(jsonPath("$.population", CoreMatchers.notNullValue()))
                .andExpect(jsonPath("$.independent", CoreMatchers.notNullValue()));
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
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$.status", CoreMatchers.is(404)))
                .andExpect(jsonPath("$.message", CoreMatchers.is("The country with the alphaCode =  %s is not found".formatted(alphaCode))));

    }

    @Test
    @DisplayName("Test get all countries functionality")
    void givenThreeDevelopers_whenGetByAll_thenSuccessResponse() throws Exception {
        //given
        final CountryEntity countryEntity = DataUtils.getTuvaluPersisted();
        final CountryDocument countryDocument = DataUtils.getTuvaluMongoTransient();

        countryJpaRepository.save(countryEntity);
        countryMongoRepository.save(countryDocument);

        //when
        final ResultActions result = mockMvc.perform(get(ENDPOINT_PATH)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$[0].alpha2").isNotEmpty())
                .andExpect(jsonPath("$[0].alpha3").isNotEmpty())
                .andExpect(jsonPath("$[0].capital").isNotEmpty())
                .andExpect(jsonPath("$[0].capital").isNotEmpty())
                .andExpect(jsonPath("$[0].region").isNotEmpty())
                .andExpect(jsonPath("$[0].area").isNotEmpty())
                .andExpect(jsonPath("$[0].population").isNotEmpty())
                .andExpect(jsonPath("$[0].independent").isNotEmpty())
                .andExpect(jsonPath("$[*]", hasSize(1)));
    }
}
