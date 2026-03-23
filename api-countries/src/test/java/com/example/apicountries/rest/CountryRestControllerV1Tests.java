package com.example.apicountries.rest;

import com.example.apicountries.config.SecurityConfig;
import com.example.apicountries.dto.CountryDto;
import com.example.apicountries.service.CountryServiceImplementation;
import com.example.apicountries.utils.DataUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CountryRestControllerV1.class)
@ActiveProfiles("test")
@Import(SecurityConfig.class)
class CountryRestControllerV1Tests {

    @MockitoBean
    private CountryServiceImplementation countryService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String ENDPOINT_PATH = "/api/v1/countries";

    @Value("${http.auth-token-header-name}")
    private String headerName;

    @Value("${http.auth-token}")
    private String authToken;

    @Test
    @DisplayName("Test get country by alpha code functionality")
    void givenAlphaCode_whenGetByAlphaCode_thenSuccessResponse() throws Exception {
        final String alphaCode = "TU";

        //given
        BDDMockito.given(countryService.getCountryByAlphaCode(anyString())).willReturn(DataUtils.getTuvValueDtoPersisted());

        //when
        final ResultActions result = mockMvc.perform(get(ENDPOINT_PATH + "/" + alphaCode)
                .contentType(MediaType.APPLICATION_JSON).header(headerName, authToken));

        //then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cca2").isNotEmpty())
                .andExpect(jsonPath("$.cca3", CoreMatchers.notNullValue()))
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
                .contentType(MediaType.APPLICATION_JSON).header(headerName, authToken));
        //then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", CoreMatchers.is("Unexpected error: The country is not found")));

    }

    @Test
    @DisplayName("Test get country by alpha code - incorrect alpha code returns 400")
    void givenInvalidAlphaCode_whenGetByAlphaCode_thenBadRequest() throws Exception {
        //given
        BDDMockito.given(countryService.getCountryByAlphaCode(anyString()))
                .willThrow(new IllegalArgumentException("alphaCode must be ISO-2 code"));

        //when
        final ResultActions result = mockMvc.perform(get(ENDPOINT_PATH + "/WWWWWW")
                .contentType(MediaType.APPLICATION_JSON).header(headerName, authToken));

        //then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", CoreMatchers.is("alphaCode must be ISO-2 code")));
    }

    @Test
    @DisplayName("Test get all countries functionality")
    void givenOneCountry_whenGetByAllCountries_thenSuccessResponse() throws Exception {
        //given
        BDDMockito.given(countryService.getAllCountries())
                .willReturn(List.of(DataUtils.getTuvValueDtoPersisted()));

        //when
        final ResultActions result = mockMvc.perform(get(ENDPOINT_PATH)
                .contentType(MediaType.APPLICATION_JSON).header(headerName, authToken));

        //then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cca2").isNotEmpty())
                .andExpect(jsonPath("$[*]", hasSize(1)));
    }

    @Test
    @DisplayName("Test get all countries functionality if empty list")
    void givenNoCountries_whenGetByAllCountries_thenEmptyListResponse() throws Exception {
        //given
        BDDMockito.given(countryService.getAllCountries()).willReturn(Collections.emptyList());

        //when
        final ResultActions result = mockMvc.perform(get(ENDPOINT_PATH)
                .contentType(MediaType.APPLICATION_JSON).header(headerName, authToken));

        //then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]", hasSize(0)));
    }

    @Test
    @DisplayName("Test success init process functionality")
    void givenInitProcessWithData_whenPostInitProcess_thenSuccessResponse() throws Exception {
        //given
        BDDMockito.given(countryService.initProcess()).willReturn(Map.of("message", "Process completed successfully"));

        //when
        final ResultActions result = mockMvc.perform(post(ENDPOINT_PATH + "/process")
                .contentType(MediaType.APPLICATION_JSON).header(headerName, authToken));

        //then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", CoreMatchers.is("Process completed successfully")));
    }

    @Test
    @DisplayName("Test aborted init process functionality")
    void givenInitProcessNoData_whenPostInitProcess_thenAbortedResponse() throws Exception {
        //given
        BDDMockito.given(countryService.initProcess()).willReturn(Map.of("message", "Process aborted: No data received from API"));

        //when
        final ResultActions result = mockMvc.perform(post(ENDPOINT_PATH + "/process")
                .contentType(MediaType.APPLICATION_JSON).header(headerName, authToken));

        //then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", CoreMatchers.is("Process aborted: No data received from API")));
    }

    @Test
    @DisplayName("Test send country to Kafka - success")
    void givenCountryDto_whenSend_thenSuccessResponse() throws Exception {
        final CountryDto country = DataUtils.getTuvValueDtoPersisted();

        CompletableFuture<SendResult<String, CountryDto>> future = CompletableFuture.completedFuture(mock(SendResult.class));

        BDDMockito.given(countryService.triggerSend(any())).willReturn(future);

        mockMvc.perform(post(ENDPOINT_PATH + "/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(headerName, authToken)
                        .content(objectMapper.writeValueAsString(country)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", CoreMatchers.is("Message confirmed by Kafka")));
    }

    @Test
    @DisplayName("Test send country to Kafka - timeout failure")
    void givenCountryDto_whenSendTimesOut_thenErrorResponse() throws Exception {
        final CountryDto country = DataUtils.getTuvValueDtoPersisted();

        CompletableFuture<SendResult<String, CountryDto>> future = new CompletableFuture<>();
        future.completeExceptionally(new ExecutionException("Kafka timeout", new TimeoutException()));

        BDDMockito.given(countryService.triggerSend(any())).willReturn(future);

        mockMvc.perform(post(ENDPOINT_PATH + "/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(headerName, authToken)
                        .content(objectMapper.writeValueAsString(country)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", CoreMatchers.containsString("Kafka failed")));
    }
}