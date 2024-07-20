package com.library.tanda.tandadarajaintegration.utility;

import com.library.tanda.tandadarajaintegration.Entities.GwRequest;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {

    public boolean validateMobileNumber(String mobileNumber) {
        // Validating that the number is a valid Kenyan Safaricom number
        String regex = "^\\+2547[0-9]{8}$";
        return mobileNumber.matches(regex);
    }

    public boolean validateAmount(double amount) {
        // Validating the amount is between KSh 10 and KSh 150,000
        return amount >= 10 && amount <= 150000;
    }

    public boolean validateGwRequest(GwRequest gwRequest) {
        return gwRequest != null &&
                gwRequest.getTransactionId() != null && !gwRequest.getTransactionId().isEmpty() &&
                validateAmount(gwRequest.getAmount()) &&
                validateMobileNumber(gwRequest.getMobileNumber()) &&
                gwRequest.getStatus() != null && !gwRequest.getStatus().isEmpty();
    }
}
