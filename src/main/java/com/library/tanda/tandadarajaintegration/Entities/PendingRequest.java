package com.library.tanda.tandadarajaintegration.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PendingRequest {
    @Id
    @GeneratedValue
    private UUID id;
    private int retryCount;

    @ManyToOne
    @JoinColumn(name = "payment_request_id")
    private PaymentRequest paymentRequest;
}
