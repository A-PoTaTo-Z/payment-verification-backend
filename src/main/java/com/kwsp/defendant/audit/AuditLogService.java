package com.kwsp.defendant.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
public class AuditLogService {

    private static final Logger AUDIT_LOGGER =
            LoggerFactory.getLogger("KWSP_AUDIT");

    private static final String UNKNOWN_VALUE =
            "UNKNOWN";

    public void logSearchSuccess(
            String clientIdentifier,
            String referralNumber,
            String identityNumber
    ) {
        logEvent(
                "SEARCH_SUCCESS",
                clientIdentifier,
                referralNumber,
                identityNumber
        );
    }

    public void logSearchNotFound(
            String clientIdentifier,
            String referralNumber,
            String identityNumber
    ) {
        logEvent(
                "SEARCH_NOT_FOUND",
                clientIdentifier,
                referralNumber,
                identityNumber
        );
    }

    public void logRecaptchaRejected(
            String clientIdentifier,
            String referralNumber,
            String identityNumber
    ) {
        logEvent(
                "RECAPTCHA_REJECTED",
                clientIdentifier,
                referralNumber,
                identityNumber
        );
    }

    public void logRateLimitExceeded(
            String clientIdentifier
    ) {
        if (!AUDIT_LOGGER.isWarnEnabled()) {
            return;
        }

        AUDIT_LOGGER.warn(
                "event={} clientHash={}",
                "RATE_LIMIT_EXCEEDED",
                hashValue(clientIdentifier)
        );
    }

    private void logEvent(
            String event,
            String clientIdentifier,
            String referralNumber,
            String identityNumber
    ) {
        if (!AUDIT_LOGGER.isInfoEnabled()) {
            return;
        }

        AUDIT_LOGGER.info(
                "event={} clientHash={} referral={} identity={}",
                event,
                hashValue(clientIdentifier),
                maskReferralNumber(referralNumber),
                maskIdentityNumber(identityNumber)
        );
    }

    private String maskReferralNumber(
            String referralNumber
    ) {
        if (
                referralNumber == null ||
                referralNumber.isBlank()
        ) {
            return UNKNOWN_VALUE;
        }

        String normalized =
                referralNumber.trim();

        if (normalized.length() <= 4) {
            return "****";
        }

        String visibleEnding =
                normalized.substring(
                        normalized.length() - 4
                );

        return "****" + visibleEnding;
    }

    private String maskIdentityNumber(
            String identityNumber
    ) {
        if (
                identityNumber == null ||
                identityNumber.isBlank()
        ) {
            return UNKNOWN_VALUE;
        }

        String normalized =
                identityNumber.trim();

        if (normalized.length() <= 4) {
            return "****";
        }

        String visibleEnding =
                normalized.substring(
                        normalized.length() - 4
                );

        return "********" + visibleEnding;
    }

    private String hashValue(
            String value
    ) {
        if (
                value == null ||
                value.isBlank()
        ) {
            return UNKNOWN_VALUE;
        }

        try {
            MessageDigest digest =
                    MessageDigest.getInstance(
                            "SHA-256"
                    );

            byte[] hash =
                    digest.digest(
                            value.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            return HexFormat
                    .of()
                    .formatHex(hash)
                    .substring(0, 16);

        } catch (
                NoSuchAlgorithmException exception
        ) {
            throw new IllegalStateException(
                    "SHA-256 algorithm is unavailable",
                    exception
            );
        }
    }
}