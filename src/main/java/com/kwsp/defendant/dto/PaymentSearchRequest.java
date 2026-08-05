package com.kwsp.defendant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PaymentSearchRequest(

        @NotBlank
        @Size(min = 8, max = 30)
        @Pattern(regexp = "^[A-Za-z0-9-]+$")
        String referralNumber,

        @NotBlank
        @Size(min = 6, max = 20)
        @Pattern(regexp = "^[A-Za-z0-9]+$")
        String identityNumber,

        @NotBlank
        String recaptchaToken
) {
}