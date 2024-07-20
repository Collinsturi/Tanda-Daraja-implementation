package com.library.tanda.tandadarajaintegration.unit;

import com.library.tanda.tandadarajaintegration.Entities.*;
import com.library.tanda.tandadarajaintegration.Service.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PaymentServiceTest {
    @Mock
    private PaymentRequestRepository paymentRequestRepository;

    @Mock
    private PendingRequestRepository pendingRequestRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void logPaymentRequest_success() {
        GwRequest gwRequest = new GwRequest();
        gwRequest.setTransactionId("trans123");
        gwRequest.setAmount(100);
        gwRequest.setMobileNumber("+254712345678");
        gwRequest.setStatus("Pending");

        PaymentRequest savedRequest = new PaymentRequest();
        savedRequest.setId(UUID.randomUUID());
        savedRequest.setTransactionId("trans123");
        savedRequest.setAmount(100);
        savedRequest.setMobileNumber("+254712345678");
        savedRequest.setStatus("Pending");

        when(paymentRequestRepository.save(any(PaymentRequest.class))).thenReturn(savedRequest);

        PaymentRequest result = paymentService.logPaymentRequest(gwRequest);

        assertNotNull(result);
        assertEquals("trans123", result.getTransactionId());
        verify(pendingRequestRepository, times(1)).save(any(PendingRequest.class));
    }

    @Test
    public void updatePaymentRequest_success() {
        PaymentRequest request = new PaymentRequest();
        request.setId(UUID.randomUUID());
        request.setTransactionId("trans123");
        request.setAmount(100);
        request.setMobileNumber("+254712345678");
        request.setStatus("Submitted");

        paymentService.updatePaymentRequest(request);

        verify(paymentRequestRepository, times(1)).save(request);
    }

    @Test
    public void findById_success() {
        UUID id = UUID.randomUUID();
        PaymentRequest request = new PaymentRequest();
        request.setId(id);
        request.setTransactionId("trans123");
        request.setAmount(100);
        request.setMobileNumber("+254712345678");
        request.setStatus("Submitted");

        when(paymentRequestRepository.findById(id)).thenReturn(Optional.of(request));

        PaymentRequest result = paymentService.findById(id);

        assertNotNull(result);
        assertEquals("trans123", result.getTransactionId());
    }

    @Test
    public void findPendingRequests_success() {
        PaymentRequest request = new PaymentRequest();
        request.setId(UUID.randomUUID());
        request.setTransactionId("trans123");
        request.setAmount(100);
        request.setMobileNumber("+254712345678");
        request.setStatus("Pending");

        when(paymentRequestRepository.findAllByStatus("Pending"))
                .thenReturn((Optional.of(List.of(request))));

        List<PaymentRequest> result = paymentService.findPendingRequests();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("trans123", result.get(0).getTransactionId());
    }
}
