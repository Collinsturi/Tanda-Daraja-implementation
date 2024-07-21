package com.library.tanda.tandadarajaintegration.Service;

import com.library.tanda.tandadarajaintegration.Data.B2CRequest;
import com.library.tanda.tandadarajaintegration.Entities.PaymentRequest;
import org.springframework.http.ResponseEntity;

public interface ThirdPartyPaymentService {
    void sendPaymentRequest(PaymentRequest request);
    String checkRequestStatus(PaymentRequest request);
}
