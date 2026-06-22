package com.banking.model;

import java.math.BigDecimal;
import com.banking.enums.AccountStatus;
import com.banking.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Column(unique = true)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    private BigDecimal balance;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
}