package com.library.tanda.tandadarajaintegration.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.tanda.tandadarajaintegration.Data.CallbackResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CallbackControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void handleCallback_success() throws Exception {
        CallbackResponse callbackResponse = CallbackResponse.builder()
                .id(UUID.randomUUID())
                .status("Completed")
                .build();

        mockMvc.perform(post("/callback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(callbackResponse)))
                .andExpect(status().isOk());
    }

    @Test
    public void handleCallback_badRequest() throws Exception {
        CallbackResponse callbackResponse = new CallbackResponse();

        mockMvc.perform(post("/callback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(callbackResponse)))
                .andExpect(status().isBadRequest());
    }
}
