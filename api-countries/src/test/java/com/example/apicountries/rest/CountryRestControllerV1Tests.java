package com.example.apicountries.rest;

import com.example.apicountries.service.CountryServiceImplementation;
import com.example.apicountries.utils.DataUtils;
import jakarta.persistence.EntityNotFoundException;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest
class CountryRestControllerV1Tests {

    @MockitoBean
    private CountryServiceImplementation countryService;

    @Autowired
    private MockMvc mockMvc;


    private static final String ENDPOINT_PATH = "/api/v1/countries";


    @Test
    @DisplayName("Test get country by alpha code functionality")
    void givenAlphaCode_whenGetByAlphaCode_thenSuccessResponse() throws Exception {
        final String alphaCode = "TU";

        //given
        BDDMockito.given(countryService.getCountryByAlphaCode(anyString())).willReturn(DataUtils.getTuvaluDtoPersisted());

        //when
        final ResultActions result = mockMvc.perform(get(ENDPOINT_PATH + "/" + alphaCode)
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
                .andExpect(jsonPath("$.population", CoreMatchers.notNullValue()));
    }

    @Test
    @DisplayName("Test get country by alpha code with incorrect alpha code functionality")
    void givenIncorrectId_whenGetById_thenErrorResponse() throws Exception {
        final String alphaCode = "TU";

        //given
        BDDMockito.given(countryService.getCountryByAlphaCode(anyString()))
                .willThrow(new EntityNotFoundException("The country is not found"));

        //when
        final ResultActions result = mockMvc.perform(get(ENDPOINT_PATH + "/" + alphaCode)
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$.status", CoreMatchers.is(404)))
                .andExpect(jsonPath("$.message", CoreMatchers.is("The country is not found")));

    }

    @Test
    @DisplayName("Test get all countries functionality")
    void givenOneCountry_whenGetByAllCountries_thenSuccessResponse() throws Exception {
        //given
        BDDMockito.given(countryService.getAllCountries())
                .willReturn(List.of(DataUtils.getTuvaluDtoPersisted()));

        //when
        final ResultActions result = mockMvc.perform(get(ENDPOINT_PATH)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$[0].alpha2").isNotEmpty())
                .andExpect(jsonPath("$[*]", hasSize(1)));
    }
}