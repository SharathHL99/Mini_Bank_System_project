package com.banking.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransferRequestDto {

    @NotNull
    private Long sourceAccountId;

    @NotNull
    private Long destinationAccountId;

    @DecimalMin(value = "1")
    private BigDecimal amount;
}