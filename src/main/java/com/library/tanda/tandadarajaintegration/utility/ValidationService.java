package com.library.tanda.tandadarajaintegration.utility;

import com.library.tanda.tandadarajaintegration.Data.B2CRequest;
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
    public boolean validateB2CRequest(B2CRequest b2CRequest) {
        return b2CRequest != null &&
                b2CRequest.getShortCode() != null && !b2CRequest.getShortCode().isEmpty() &&
                b2CRequest.getCommandID() != null && !b2CRequest.getCommandID().isEmpty() &&
                b2CRequest.getAmount() != null && validateAmount(Double.parseDouble(b2CRequest.getAmount())) &&
                b2CRequest.getPartyA() != null && validateMobileNumber(b2CRequest.getPartyA()) &&
                b2CRequest.getPartyB() != null && !b2CRequest.getPartyB().isEmpty();
    }
}
