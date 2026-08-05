package com.kwsp.defendant.security;

import com.kwsp.defendant.dto.RecaptchaVerificationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;

@Service
public class RecaptchaService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(RecaptchaService.class);

    private final RestClient restClient;
    private final String secretKey;
    private final String expectedAction;
    private final double minimumScore;
    private final boolean enabled;

    public RecaptchaService(
            @Value("${app.recaptcha.secret-key}")
            String secretKey,

            @Value("${app.recaptcha.expected-action:public_payment_search}")
            String expectedAction,

            @Value("${app.recaptcha.minimum-score:0.5}")
            double minimumScore,

            @Value("${app.recaptcha.enabled:true}")
            boolean enabled
    ) {
        this.secretKey = secretKey;
        this.expectedAction = expectedAction;
        this.minimumScore = minimumScore;
        this.enabled = enabled;

        HttpClient httpClient = HttpClient
                .newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        JdkClientHttpRequestFactory requestFactory =
                new JdkClientHttpRequestFactory(httpClient);

        requestFactory.setReadTimeout(Duration.ofSeconds(5));

        this.restClient = RestClient
                .builder()
                .baseUrl(
                        "https://www.google.com/recaptcha/api"
                )
                .requestFactory(requestFactory)
                .build();
    }

    public boolean verify(String token) {
        /*
         * This switch is useful for automated tests.
         * Keep it enabled in normal development and production.
         */
        if (!enabled) {
            LOGGER.warn(
                    "reCAPTCHA verification is disabled."
            );

            return true;
        }

        if (secretKey == null || secretKey.isBlank()) {
            LOGGER.error(
                    "The reCAPTCHA secret key is not configured."
            );

            return false;
        }

        if (token == null || token.isBlank()) {
            LOGGER.warn(
                    "The reCAPTCHA token is missing."
            );

            return false;
        }

        MultiValueMap<String, String> requestBody =
                new LinkedMultiValueMap<>();

        requestBody.add(
                "secret",
                secretKey
        );

        requestBody.add(
                "response",
                token
        );

        try {
            RecaptchaVerificationResponse response =
                    restClient
                            .post()
                            .uri("/siteverify")
                            .contentType(
                                    MediaType.APPLICATION_FORM_URLENCODED
                            )
                            .body(requestBody)
                            .retrieve()
                            .body(
                                    RecaptchaVerificationResponse.class
                            );

            return isResponseAccepted(response);

        } catch (Exception exception) {
            /*
             * Fail closed:
             * if Google cannot be reached or the response
             * cannot be processed, reject the public search.
             */
            LOGGER.error(
                    "Unable to complete reCAPTCHA verification.",
                    exception
            );

            return false;
        }
    }

    private boolean isResponseAccepted(
            RecaptchaVerificationResponse response
    ) {
        if (response == null) {
            LOGGER.warn(
                    "Google returned an empty reCAPTCHA response."
            );

            return false;
        }

        if (!response.success()) {
            List<String> errorCodes =
                    response.errorCodes() == null
                            ? List.of()
                            : response.errorCodes();

            LOGGER.warn(
                    "reCAPTCHA rejected the token. Error codes: {}",
                    errorCodes
            );

            return false;
        }

        boolean actionMatches =
                expectedAction.equals(response.action());

        if (!actionMatches) {
            LOGGER.warn(
                    "Unexpected reCAPTCHA action. Expected={}, received={}",
                    expectedAction,
                    response.action()
            );

            return false;
        }

        double score =
                response.score() == null
                        ? 0.0
                        : response.score();

        if (score < minimumScore) {
            LOGGER.warn(
                    "reCAPTCHA score is below the threshold. Score={}, threshold={}",
                    score,
                    minimumScore
            );

            return false;
        }

        LOGGER.info(
                "reCAPTCHA verification accepted. Action={}, score={}, hostname={}",
                response.action(),
                score,
                response.hostname()
        );

        return true;
    }
}