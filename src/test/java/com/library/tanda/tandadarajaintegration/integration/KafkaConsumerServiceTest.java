package com.library.tanda.tandadarajaintegration.integration;

import com.library.tanda.tandadarajaintegration.Entities.GwRequest;
import com.library.tanda.tandadarajaintegration.Entities.PaymentRequest;
import com.library.tanda.tandadarajaintegration.Kafka.Service.KafkaConsumerService;
import com.library.tanda.tandadarajaintegration.Service.PaymentService;
import com.library.tanda.tandadarajaintegration.Service.ThirdPartyPaymentService;
import com.library.tanda.tandadarajaintegration.utility.ValidationService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class KafkaConsumerServiceTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private ValidationService validationService;

    @Mock
    private ThirdPartyPaymentService thirdPartyPaymentService;

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void listen_validRequest() {
        String recordValue = "{\"transactionId\":\"trans123\",\"amount\":100,\"mobileNumber\":\"+254712345678\",\"status\":\"Pending\"}";
        ConsumerRecord<String, String> record = new ConsumerRecord<>("TandaPayment", 0, 0, null, recordValue);

        GwRequest gwRequest = new GwRequest();
        gwRequest.setTransactionId("trans123");
        gwRequest.setAmount(100);
        gwRequest.setMobileNumber("+254712345678");
        gwRequest.setStatus("Pending");

        when(validationService.validateGwRequest(any(GwRequest.class))).thenReturn(true);

        kafkaConsumerService.listen(record);

        verify(paymentService, times(1)).logPaymentRequest(any(GwRequest.class));
        verify(thirdPartyPaymentService, times(1)).sendPaymentRequest(any(PaymentRequest.class));
    }

    @Test
    public void listen_invalidRequest() {
        String recordValue = "{\"transactionId\":\"trans123\",\"amount\":100,\"mobileNumber\":\"+1234567890\",\"status\":\"Pending\"}";
        ConsumerRecord<String, String> record = new ConsumerRecord<>("TandaPayment", 0, 0, null, recordValue);

        when(validationService.validateGwRequest(any(GwRequest.class))).thenReturn(false);

        kafkaConsumerService.listen(record);

        verify(paymentService, times(0)).logPaymentRequest(any(GwRequest.class));
        verify(thirdPartyPaymentService, times(0)).sendPaymentRequest(any(PaymentRequest.class));
    }
}
