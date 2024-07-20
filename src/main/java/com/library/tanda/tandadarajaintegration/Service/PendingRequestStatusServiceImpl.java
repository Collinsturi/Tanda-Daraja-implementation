package com.library.tanda.tandadarajaintegration.Service;

import com.library.tanda.tandadarajaintegration.Data.Result;
import com.library.tanda.tandadarajaintegration.Entities.PaymentRequest;
import com.library.tanda.tandadarajaintegration.Kafka.Service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PendingRequestStatusServiceImpl implements PendingRequestStatusService{
    private final PaymentService paymentService;
    private final ThirdPartyPaymentService thirdPartyPaymentService;
    private final KafkaProducerService kafkaProducerService;

    // Check pending request status every 1 minute
    @Scheduled(fixedRate = 60000)
    public void checkPendingRequests() {
        List<PaymentRequest> pendingRequests = paymentService.findPendingRequests();

        for (PaymentRequest request : pendingRequests) {
            String status = thirdPartyPaymentService.checkRequestStatus(request);

            if (!"Pending".equals(status)) {
                request.setStatus(status);
                paymentService.updatePaymentRequest(request);

                paymentService.deletePendingRequest(request.getId());

                Result result = Result.builder()
                        .id(request.getId())
                        .status(request.getStatus())
                        .ref(request.getTransactionId())
                        .build();

                kafkaProducerService.sendResult(result);
            } else {
                paymentService.incrementRetryCount(request.getId());
            }
        }
    }
}
