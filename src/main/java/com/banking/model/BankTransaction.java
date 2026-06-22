package com.banking.model;

import java.time.LocalDateTime;

import com.banking.enums.TransactionType;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @Column(unique = true)
    private String transactionReference;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private BigDecimal amount;

    private LocalDateTime transactionDate;

    private String remarks;

    private String sourceAccount;

    private String destinationAccount;
}