package com.library.tanda.tandadarajaintegration.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.tanda.tandadarajaintegration.Data.B2CRequest;
import com.library.tanda.tandadarajaintegration.Entities.PaymentRequest;
import com.library.tanda.tandadarajaintegration.utility.MpesaConfig;
import com.library.tanda.tandadarajaintegration.utility.ValidationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ThirdPartyPaymentServiceImpl implements ThirdPartyPaymentService {
    private final RestTemplate restTemplate;
    private final PaymentService paymentService;
    private final String darajaB2CStatusEndpoint = "https://sandbox.safaricom.co.ke/mpesa/b2c/v3/paymentstatus";
    private final ObjectMapper objectMapper;
    private final OAuthService oAuthService;
    private final MpesaConfig mpesaConfiguration;
    private final ValidationService validationService;
    private final Logger LOGGER = LoggerFactory.getLogger(ThirdPartyPaymentService.class);

    public void sendPaymentRequest(PaymentRequest request) {
        try {
            B2CRequest b2cRequest = B2CRequest.builder()
                    .shortCode(mpesaConfiguration.getShortCode())
                    .commandID("BusinessPayment")
                    .amount(String.valueOf(request.getAmount()))
                    .partyA(mpesaConfiguration.getShortCode())
                    .partyB(request.getMobileNumber())
                    .remarks("Tanda wallet withdrawal")
                    .queueTimeOutURL(mpesaConfiguration.getQueueTimeOutURL())
                    .resultURL(mpesaConfiguration.getResultURL())
                    .occasion("Tanda payment request")
                    .build();

            // Validate the B2CRequest
            validationService.validateB2CRequest(b2cRequest);

            // Initiate the B2C transaction
            ResponseEntity<String> response = initiateB2CTransaction(b2cRequest);

            // Update PaymentRequest status based on response
            if (response.getStatusCode().is2xxSuccessful()) {
                request.setStatus("Submitted");
            } else {
                request.setStatus("Failed");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            request.setStatus("Failed");
        }

        // Update the payment request in the database
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

    private ResponseEntity<String> initiateB2CTransaction(B2CRequest b2CRequest) {
        String accessToken = oAuthService.getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<B2CRequest> request = new HttpEntity<>(b2CRequest, headers);

        return restTemplate.exchange(
                mpesaConfiguration.getB2cRequestEndpoint(),
                HttpMethod.POST,
                request,
                String.class
        );
    }
}
