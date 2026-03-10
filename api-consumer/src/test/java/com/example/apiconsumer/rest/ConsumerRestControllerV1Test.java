package com.example.apiconsumer.rest;

import com.example.apiconsumer.service.ConsumerServiceImplementation;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import utils.DataUtils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ConsumerRestControllerV1.class)
class ConsumerRestControllerV1Test {

    @MockitoBean
    private ConsumerServiceImplementation serviceImplementation;

    @Autowired
    private MockMvc mockMvc;

    private static final String ENDPOINT_PATH = "/api/v1/consumers";

    @Test
    @DisplayName("Test pull data - success with country returned")
    void whenPullData_thenSuccessResponse() throws Exception {
        // given
        BDDMockito.given(serviceImplementation.pullFromBroker())
                .willReturn(DataUtils.getTuvaluDtoPersisted());

        // when
        final ResultActions result = mockMvc.perform(
                get(ENDPOINT_PATH + "/receive")
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.cca2", CoreMatchers.notNullValue()))
                .andExpect(jsonPath("$.cca3", CoreMatchers.notNullValue()));
    }

    @Test
    @DisplayName("Test pull data - no content when broker is empty")
    void whenPullDataAndBrokerEmpty_thenNoContentResponse() throws Exception {
        // given
        BDDMockito.given(serviceImplementation.pullFromBroker())
                .willReturn(null);

        // when
        final ResultActions result = mockMvc.perform(
                get(ENDPOINT_PATH + "/receive")
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}