package com.kwsp.defendant.controller;

import com.kwsp.defendant.audit.AuditLogService;
import com.kwsp.defendant.dto.ErrorResponse;
import com.kwsp.defendant.dto.PaymentSearchRequest;
import com.kwsp.defendant.dto.PaymentSearchResponse;
import com.kwsp.defendant.security.RateLimitService;
import com.kwsp.defendant.security.RecaptchaService;
import com.kwsp.defendant.service.PaymentSearchService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/public-payments")
public class PaymentSearchController {

    private static final String GENERIC_ERROR =
            "The information entered does not match our records.";

    private final PaymentSearchService paymentSearchService;
    private final RecaptchaService recaptchaService;
    private final RateLimitService rateLimitService;
    private final AuditLogService auditLogService;

    public PaymentSearchController(
            PaymentSearchService paymentSearchService,
            RecaptchaService recaptchaService,
            RateLimitService rateLimitService,
            AuditLogService auditLogService
    ) {
        this.paymentSearchService = paymentSearchService;
        this.recaptchaService = recaptchaService;
        this.rateLimitService = rateLimitService;
        this.auditLogService = auditLogService;
    }

    @PostMapping("/search")
    public ResponseEntity<Object> search(
            @Valid @RequestBody PaymentSearchRequest request,
            HttpServletRequest servletRequest
    ) {
        String clientIdentifier =
                servletRequest.getRemoteAddr();

        if (!rateLimitService.allow(clientIdentifier)) {
            auditLogService.logRateLimitExceeded(
                    clientIdentifier
            );

            return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(
                            ErrorResponse.of(
                                    "Too many attempts. Please try again later."
                            )
                    );
        }

        boolean recaptchaValid =
                recaptchaService.verify(
                        request.recaptchaToken()
                );

        if (!recaptchaValid) {
            auditLogService.logRecaptchaRejected(
                    clientIdentifier,
                    request.referralNumber(),
                    request.identityNumber()
            );

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            ErrorResponse.of(GENERIC_ERROR)
                    );
        }

        Optional<PaymentSearchResponse> paymentResult =
                paymentSearchService.search(
                        request.referralNumber(),
                        request.identityNumber()
                );

        if (paymentResult.isEmpty()) {
            auditLogService.logSearchNotFound(
                    clientIdentifier,
                    request.referralNumber(),
                    request.identityNumber()
            );

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            ErrorResponse.of(GENERIC_ERROR)
                    );
        }

        auditLogService.logSearchSuccess(
                clientIdentifier,
                request.referralNumber(),
                request.identityNumber()
        );

        return ResponseEntity.ok(
                paymentResult.get()
        );
    }
}