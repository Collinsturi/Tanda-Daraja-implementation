package com.library.tanda.tandadarajaintegration.Service;

import com.library.tanda.tandadarajaintegration.Data.B2CRequest;
import com.library.tanda.tandadarajaintegration.Entities.*;
import com.library.tanda.tandadarajaintegration.utility.MpesaConfig;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRequestRepository paymentRequestRepository;
    private final PendingRequestRepository pendingRequestRepository;

    @Transactional
    public PaymentRequest logPaymentRequest(GwRequest gwRequest) {
        PaymentRequest request = PaymentRequest.builder()
                .transactionId(gwRequest.getTransactionId())
                .amount(gwRequest.getAmount())
                .mobileNumber(gwRequest.getMobileNumber())
                .status("Pending")
                .build();


        var savedRequest = paymentRequestRepository.save(request);

        var pendingRequest = PendingRequest.builder()
                .retryCount(0)
                .paymentRequest(savedRequest)
                .build();

        pendingRequestRepository.save(pendingRequest);

        return savedRequest;
    }

    @Transactional
    public void updatePaymentRequest(PaymentRequest request) {
        paymentRequestRepository.save(request);
    }

    public PaymentRequest findById(UUID id) {
        return paymentRequestRepository.findById(id).orElse(null);
    }

    public List<PaymentRequest> findPendingRequests() {
        var pending = paymentRequestRepository.findAllByStatus("Pending");

        return pending.orElse(null);
    }

    @Transactional
    public void deletePendingRequest(UUID id) {
        pendingRequestRepository.deletePendingRequestByPaymentRequestId(id);
    }

    @Transactional
    public void incrementRetryCount(UUID id) {
        PendingRequest pendingRequest = pendingRequestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("PendingRequest not found"));
        pendingRequest.setRetryCount(pendingRequest.getRetryCount() + 1);
        pendingRequestRepository.save(pendingRequest);
    }
}
