package com.banking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponseDto {

    private String transactionReference;

    private String transactionType;

    private BigDecimal amount;

    private String sourceAccount;

    private String destinationAccount;

    private LocalDateTime transactionDate;
}