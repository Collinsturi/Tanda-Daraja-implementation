package com.library.tanda.tandadarajaintegration.Resources;

import com.library.tanda.tandadarajaintegration.Data.CallbackResponse;
import com.library.tanda.tandadarajaintegration.Data.Result;
import com.library.tanda.tandadarajaintegration.Entities.PaymentRequest;
import com.library.tanda.tandadarajaintegration.Entities.PendingRequestRepository;
import com.library.tanda.tandadarajaintegration.Kafka.Service.KafkaProducerService;
import com.library.tanda.tandadarajaintegration.Service.PaymentService;
import com.library.tanda.tandadarajaintegration.utility.ValidationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/callback")
public class CallbackController {
    private final PaymentService paymentService;
    private final ValidationService validationService;
    private final KafkaProducerService kafkaProducerService;
    private final PendingRequestRepository pendingRequestRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(CallbackController.class);

    @PostMapping
    public ResponseEntity<String> handleCallback(@RequestBody CallbackResponse callbackResponse) {
        try {
            if (callbackResponse == null || callbackResponse.getId() == null || callbackResponse.getStatus() == null) {
                return ResponseEntity.badRequest().body("Invalid callback payload");
            }

            PaymentRequest request = paymentService.findById(callbackResponse.getId());

            if (request == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment request not found");
            }

            request.setStatus(callbackResponse.getStatus());
            paymentService.updatePaymentRequest(request);

            pendingRequestRepository.deletePendingRequestByPaymentRequestId(request.getId());

            Result result = Result.builder()
                    .id(request.getId())
                    .status(request.getStatus())
                    .ref(request.getTransactionId())
                    .build();

            kafkaProducerService.sendResult(result);

            return ResponseEntity.ok("Callback processed successfully");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("There was an error in processing the request. PLease try again later.");
        }
    }
}

