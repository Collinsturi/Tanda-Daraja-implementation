package com.library.tanda.tandadarajaintegration.Service;

import com.library.tanda.tandadarajaintegration.Entities.PaymentRequest;

public interface ThirdPartyPaymentService {
    void sendPaymentRequest(PaymentRequest request);
    String checkRequestStatus(PaymentRequest request);
}
