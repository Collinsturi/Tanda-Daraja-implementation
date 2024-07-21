package com.library.tanda.tandadarajaintegration.integration;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import com.library.tanda.tandadarajaintegration.Data.CallbackResponse;
import com.library.tanda.tandadarajaintegration.Data.Result;
import com.library.tanda.tandadarajaintegration.Entities.PaymentRequest;
import com.library.tanda.tandadarajaintegration.Entities.PendingRequestRepository;
import com.library.tanda.tandadarajaintegration.Service.PaymentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CallbackControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private PendingRequestRepository pendingRequestRepository;

    @MockBean
    private KafkaTemplate<String, Result> kafkaTemplate;

    @Test
    public void handleCallback_ValidRequest_Success() throws Exception {
        UUID id = UUID.randomUUID();
        CallbackResponse callbackResponse = new CallbackResponse(id, "Completed", "ref123");

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .id(id)
                .transactionId("txn123")
                .amount(1000.0)
                .mobileNumber("+254712345678")
                .status("Pending")
                .build();

        when(paymentService.findById(id)).thenReturn(paymentRequest);

        mockMvc.perform(post("/callback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(callbackResponse)))
                .andExpect(status().isOk())
                .andExpect(content().string("Callback processed successfully"));

        verify(paymentService).updatePaymentRequest(paymentRequest);
        verify(pendingRequestRepository).deletePendingRequestByPaymentRequestId(id);

        ArgumentCaptor<Result> resultCaptor = ArgumentCaptor.forClass(Result.class);
        verify(kafkaTemplate).send(anyString(), resultCaptor.capture());

        Result sentResult = resultCaptor.getValue();
        Assertions.assertEquals(id, sentResult.getId());
        Assertions.assertEquals("Completed", sentResult.getStatus());
        Assertions.assertEquals("txn123", sentResult.getRef());
    }

    @Test
    public void handleCallback_BadRequest_Error() throws Exception {
        // Create an invalid CallbackResponse (e.g., missing the required 'id' field)
        String invalidCallbackResponse = "{ \"status\": \"Completed\", \"ref\": \"ref123\" }";

        mockMvc.perform(post("/callback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidCallbackResponse))
                .andExpect(status().isBadRequest());
    }
}
