package com.library.tanda.tandadarajaintegration.integration;

import com.library.tanda.tandadarajaintegration.Entities.PaymentRequest;
import com.library.tanda.tandadarajaintegration.Kafka.Service.KafkaConsumerService;
import com.library.tanda.tandadarajaintegration.Service.ThirdPartyPaymentService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
public class KafkaConsumerServiceTest {

    @Mock
    private ThirdPartyPaymentService thirdPartyPaymentService;

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    @Test
    public void listen_validRequest() throws Exception {
        String kafkaMessage = "{\"transactionId\":\"trans123\",\"amount\":100,\"mobileNumber\":\"+254712345678\",\"status\":\"Pending\"}";

        PaymentRequest expectedRequest = PaymentRequest.builder()
                .transactionId("trans123")
                .amount(100.0)
                .mobileNumber("+254712345678")
                .status("Pending")
                .build();

        // Simulate Kafka listener receiving a message
        kafkaConsumerService.listen(new ConsumerRecord<>("TandaPayment", 0, 0, null, kafkaMessage));

        // Capture the argument passed to thirdPartyPaymentService.sendPaymentRequest
        ArgumentCaptor<PaymentRequest> paymentRequestCaptor = ArgumentCaptor.forClass(PaymentRequest.class);
        verify(thirdPartyPaymentService).sendPaymentRequest(paymentRequestCaptor.capture());

        // Assert the captured argument matches the expected request
        PaymentRequest actualRequest = paymentRequestCaptor.getValue();
        Assertions.assertEquals(expectedRequest.getTransactionId(), actualRequest.getTransactionId());
        Assertions.assertEquals(expectedRequest.getAmount(), actualRequest.getAmount());
        Assertions.assertEquals(expectedRequest.getMobileNumber(), actualRequest.getMobileNumber());
        Assertions.assertEquals(expectedRequest.getStatus(), actualRequest.getStatus());
    }
}
