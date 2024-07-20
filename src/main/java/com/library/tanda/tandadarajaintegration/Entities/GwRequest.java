package com.library.tanda.tandadarajaintegration.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class GwRequest {
    @Id
    private UUID id;
    private String transactionId;
    private double amount;
    private String mobileNumber;
    private String status;

    @Override
    public String toString() {
        return "GwRequest{" +
                "id=" + id +
                ", transactionId='" + transactionId + '\'' +
                ", amount=" + amount +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
