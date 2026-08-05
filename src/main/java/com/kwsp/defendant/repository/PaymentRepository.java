package com.kwsp.defendant.repository;

import com.kwsp.defendant.model.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository
        extends JpaRepository<PaymentRecord, Long> {

    Optional<PaymentRecord>
    findByReferralNumberIgnoreCaseAndIdentityNumberIgnoreCase(
            String referralNumber,
            String identityNumber
    );

    boolean existsByReferralNumberIgnoreCase(
            String referralNumber
    );
}