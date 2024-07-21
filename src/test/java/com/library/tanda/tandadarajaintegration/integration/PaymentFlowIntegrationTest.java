package com.library.tanda.tandadarajaintegration.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.tanda.tandadarajaintegration.Data.CallbackResponse;
import com.library.tanda.tandadarajaintegration.Entities.GwRequest;
import com.library.tanda.tandadarajaintegration.Entities.PaymentRequest;
import com.library.tanda.tandadarajaintegration.Entities.PaymentRequestRepository;
import com.library.tanda.tandadarajaintegration.Entities.PendingRequestRepository;
import com.library.tanda.tandadarajaintegration.Kafka.Service.KafkaConsumerService;
import com.library.tanda.tandadarajaintegration.Kafka.Service.KafkaProducerService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class PaymentFlowIntegrationTest {

    @Autowired
    private KafkaConsumerService kafkaConsumerService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private PaymentRequestRepository paymentRequestRepository;

    @Autowired
    private PendingRequestRepository pendingRequestRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void endToEndPaymentFlow_Success() throws Exception {
        // Step 1: Simulate Kafka message consumption
        GwRequest gwRequest = GwRequest.builder()
                .id(UUID.randomUUID())
                .transactionId("transactionId")
                .amount(100.0)
                .mobileNumber("+254700000000")
                .status("Pending")
                .build();

        String kafkaMessage = new ObjectMapper().writeValueAsString(gwRequest);

        kafkaConsumerService.listen(new ConsumerRecord<>("TandaPayment", 0, 0, null, kafkaMessage));

        PaymentRequest savedRequest = paymentRequestRepository.findAll().get(0);

        assertNotNull(savedRequest);
        assertEquals("Pending", savedRequest.getStatus());

        // Step 2: Simulate callback processing
        CallbackResponse callbackResponse = new CallbackResponse(savedRequest.getId(), "Completed", "ref");

        mockMvc.perform(post("/callback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(callbackResponse)))
                .andExpect(status().isOk())
                .andExpect(content().string("Callback processed successfully"));

        PaymentRequest updatedRequest = paymentRequestRepository.findById(savedRequest.getId()).orElse(null);

        assertNotNull(updatedRequest);
        assertEquals("Completed", updatedRequest.getStatus());

        // Step 3: Verify Kafka producer sent the result
        // Mocking Kafka to verify message sent to the topic (this requires a Mocked Kafka setup or a custom listener)
    }
}
