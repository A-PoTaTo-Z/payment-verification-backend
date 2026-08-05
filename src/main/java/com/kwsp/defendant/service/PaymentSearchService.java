package com.kwsp.defendant.service;

import com.kwsp.defendant.dto.PaymentSearchResponse;
import com.kwsp.defendant.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PaymentSearchService {

    private final PaymentRepository paymentRepository;

    public PaymentSearchService(
            PaymentRepository paymentRepository
    ) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional(readOnly = true)
    public Optional<PaymentSearchResponse> search(
            String referralNumber,
            String identityNumber
    ) {
        String normalizedReferralNumber =
                referralNumber.trim();

        String normalizedIdentityNumber =
                identityNumber.trim();

        return paymentRepository
                .findByReferralNumberIgnoreCaseAndIdentityNumberIgnoreCase(
                        normalizedReferralNumber,
                        normalizedIdentityNumber
                )
                .map(paymentRecord ->
                        new PaymentSearchResponse(
                                paymentRecord.getPaymentReference(),
                                paymentRecord.getPaymentStatus(),
                                paymentRecord.getAmount(),
                                paymentRecord.getDueDate()
                        )
                );
    }
}