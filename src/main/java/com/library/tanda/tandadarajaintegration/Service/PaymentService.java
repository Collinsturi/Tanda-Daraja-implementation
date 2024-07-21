package com.library.tanda.tandadarajaintegration.Service;

import com.library.tanda.tandadarajaintegration.Data.B2CRequest;
import com.library.tanda.tandadarajaintegration.Entities.GwRequest;
import com.library.tanda.tandadarajaintegration.Entities.PaymentRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    void incrementRetryCount(UUID id);
    void deletePendingRequest(UUID id);
    List<PaymentRequest> findPendingRequests();
    PaymentRequest findById(UUID id);
    void updatePaymentRequest(PaymentRequest request);
    PaymentRequest logPaymentRequest(GwRequest gwRequest);
}


