package com.kwsp.defendant.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payment_records")
public class PaymentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "referral_number",
            nullable = false,
            unique = true,
            length = 30
    )
    private String referralNumber;

    @Column(
            name = "identity_number",
            nullable = false,
            length = 20
    )
    private String identityNumber;

    @Column(
            name = "payment_reference",
            nullable = false,
            unique = true,
            length = 40
    )
    private String paymentReference;

    @Column(
            name = "payment_status",
            nullable = false,
            length = 50
    )
    private String paymentStatus;

    @Column(
            name = "amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal amount;

    @Column(
            name = "due_date",
            nullable = false
    )
    private LocalDate dueDate;

    protected PaymentRecord() {
        // Required by JPA
    }

    public PaymentRecord(
            String referralNumber,
            String identityNumber,
            String paymentReference,
            String paymentStatus,
            BigDecimal amount,
            LocalDate dueDate
    ) {
        this.referralNumber = referralNumber;
        this.identityNumber = identityNumber;
        this.paymentReference = paymentReference;
        this.paymentStatus = paymentStatus;
        this.amount = amount;
        this.dueDate = dueDate;
    }

    public Long getId() {
        return id;
    }

    public String getReferralNumber() {
        return referralNumber;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }
}