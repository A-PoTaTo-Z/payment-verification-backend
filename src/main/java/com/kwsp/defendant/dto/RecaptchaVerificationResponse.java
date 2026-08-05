package com.kwsp.defendant.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RecaptchaVerificationResponse(

        boolean success,

        Double score,

        String action,

        @JsonProperty("challenge_ts")
        OffsetDateTime challengeTimestamp,

        String hostname,

        @JsonProperty("error-codes")
        List<String> errorCodes
) {
}