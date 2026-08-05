package com.kwsp.defendant.exception;

import com.kwsp.defendant.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception
    ) {
        LOGGER.warn(
                "Request validation failed with {} field error(s)",
                exception.getBindingResult().getFieldErrorCount()
        );

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Please check the information entered."
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableRequest(
            HttpMessageNotReadableException exception
    ) {
        LOGGER.warn("Unreadable request body received");

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "The request could not be processed."
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
            Exception exception,
            HttpServletRequest request
    ) {
        LOGGER.error(
                "Unexpected error while processing {} {}",
                request.getMethod(),
                request.getRequestURI(),
                exception
        );

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "The service is temporarily unavailable."
        );
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String message
    ) {
        ErrorResponse response = new ErrorResponse(
                message,
                Instant.now()
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }
}