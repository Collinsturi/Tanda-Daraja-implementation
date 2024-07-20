package com.library.tanda.tandadarajaintegration.Entities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PendingRequestRepository extends JpaRepository<PendingRequest, UUID> {
    void deletePendingRequestByPaymentRequestId(UUID paymentRequestId);
}
