package com.library.tanda.tandadarajaintegration.Data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class B2CRequest {
    private String shortCode;
    private String commandID;
    private String amount;
    private String partyA;
    private String partyB;
    private String remarks;
    private String queueTimeOutURL;
    private String resultURL;
    private String occasion;
}
