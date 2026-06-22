package com.banking.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountResponseDto {

    private Long accountId;

    private String accountNumber;

    private String accountType;

    private String status;

    private BigDecimal balance;
    
    private Long customerId;
}