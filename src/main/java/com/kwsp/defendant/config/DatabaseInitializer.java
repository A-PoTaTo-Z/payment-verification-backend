package com.kwsp.defendant.config;

import com.kwsp.defendant.model.PaymentRecord;
import com.kwsp.defendant.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

@Configuration
public class DatabaseInitializer {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    DatabaseInitializer.class
            );

    @Bean
    CommandLineRunner initializePaymentDatabase(
            PaymentRepository paymentRepository
    ) {
        return arguments -> {
            addRecordIfMissing(
                    paymentRepository,
                    new PaymentRecord(
                            "REF-8K29-PLQ7",
                            "990101011234",
                            "PAY-2026-0001",
                            "Pending payment",
                            new BigDecimal("150.00"),
                            LocalDate.of(
                                    2026,
                                    Month.JULY,
                                    31
                            )
                    )
            );

            addRecordIfMissing(
                    paymentRepository,
                    new PaymentRecord(
                            "REF-4ABC-92XY",
                            "A12345678",
                            "PAY-2026-0002",
                            "Paid",
                            new BigDecimal("250.00"),
                            LocalDate.of(
                                    2026,
                                    Month.AUGUST,
                                    15
                            )
                    )
            );

            LOGGER.info(
                    "Payment database initialized. Total records: {}",
                    paymentRepository.count()
            );
        };
    }

    private void addRecordIfMissing(
            PaymentRepository paymentRepository,
            PaymentRecord paymentRecord
    ) {
        boolean alreadyExists =
                paymentRepository
                        .existsByReferralNumberIgnoreCase(
                                paymentRecord.getReferralNumber()
                        );

        if (alreadyExists) {
            return;
        }

        paymentRepository.save(paymentRecord);

        LOGGER.info(
                "Created demonstration payment record: {}",
                paymentRecord.getPaymentReference()
        );
    }
}