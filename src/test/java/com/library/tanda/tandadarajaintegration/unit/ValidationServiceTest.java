package com.library.tanda.tandadarajaintegration.unit;

import com.library.tanda.tandadarajaintegration.Entities.GwRequest;
import com.library.tanda.tandadarajaintegration.utility.ValidationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidationServiceTest {
    private final ValidationService validationService = new ValidationService();

    @Test
    public void validateMobileNumber_validNumber() {
        assertTrue(validationService.validateMobileNumber("+254712345678"));
    }

    @Test
    public void validateMobileNumber_invalidNumber() {
        assertFalse(validationService.validateMobileNumber("+1234567890"));
    }

    @Test
    public void validateAmount_validAmount() {
        assertTrue(validationService.validateAmount(500));
    }

    @Test
    public void validateAmount_invalidAmount() {
        assertFalse(validationService.validateAmount(5));
    }

    @Test
    public void validateGwRequest_validRequest() {
        GwRequest request = new GwRequest();
        request.setTransactionId("trans123");
        request.setAmount(100);
        request.setMobileNumber("+254712345678");
        request.setStatus("Pending");

        assertTrue(validationService.validateGwRequest(request));
    }

    @Test
    public void validateGwRequest_invalidRequest() {
        GwRequest request = new GwRequest();
        request.setTransactionId("");
        request.setAmount(100);
        request.setMobileNumber("+254712345678");
        request.setStatus("Pending");

        assertFalse(validationService.validateGwRequest(request));
    }
}
