package com.library.tanda.tandadarajaintegration.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.tanda.tandadarajaintegration.Entities.PaymentRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ThirdPartyPaymentServiceImpl implements ThirdPartyPaymentService {
    private final RestTemplate restTemplate;
    private final PaymentService paymentService;
    private final String darajaB2CEndpoint = "https://sandbox.safaricom.co.ke/mpesa/b2c/v1/paymentrequest";
    private final String darajaB2CStatusEndpoint = "https://sandbox.safaricom.co.ke/mpesa/b2c/v1/paymentstatus";
    private final ObjectMapper objectMapper;
    private final Logger LOGGER = LoggerFactory.getLogger(ThirdPartyPaymentService.class);

    public void sendPaymentRequest(PaymentRequest request) {
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(darajaB2CEndpoint, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                request.setStatus("Submitted");
            } else {
                request.setStatus("Failed");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());

            request.setStatus("Failed");
        }
        paymentService.updatePaymentRequest(request);
    }


    public String checkRequestStatus(PaymentRequest request) {
        String url = darajaB2CStatusEndpoint + "?transactionId=" + request.getTransactionId();

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode responseBody = objectMapper.readTree(response.getBody());
                String status = responseBody.get("status").asText();

                if ("SUCCESS".equals(status)) {
                    return "Completed";
                } else if ("FAILED".equals(status)) {
                    return "Failed";
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        // Default status to pending if there's an issue
        return "Pending";
    }
}
