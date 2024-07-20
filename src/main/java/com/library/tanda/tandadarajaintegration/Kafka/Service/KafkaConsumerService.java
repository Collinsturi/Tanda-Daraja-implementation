package com.library.tanda.tandadarajaintegration.Kafka.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.tanda.tandadarajaintegration.Entities.GwRequest;
import com.library.tanda.tandadarajaintegration.Entities.PaymentRequest;
import com.library.tanda.tandadarajaintegration.Service.PaymentService;
import com.library.tanda.tandadarajaintegration.Service.ThirdPartyPaymentService;
import com.library.tanda.tandadarajaintegration.utility.ValidationService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final PaymentService paymentService;
    private final ValidationService validationService;
    private final ThirdPartyPaymentService thirdPartyPaymentService;
    private final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerService.class);

    @KafkaListener(topics = "TandaPayment", groupId = "group-uno")
    public void listen(ConsumerRecord<String, String> record) {
        LOGGER.info("Received message: {}", record.value());

        //The incoming messages are parsed into GwRequest type, logged then a payment request is initiated.
        GwRequest gwRequest = parseGwRequest(record.value());
        if (gwRequest != null && validationService.validateGwRequest(gwRequest)) {
            PaymentRequest paymentRequest = paymentService.logPaymentRequest(gwRequest);
            thirdPartyPaymentService.sendPaymentRequest(paymentRequest);
        } else {
            LOGGER.error("Invalid GwRequest received: {}", record.value());
        }
    }

    private GwRequest parseGwRequest(String recordValue) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(recordValue, GwRequest.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }
}


