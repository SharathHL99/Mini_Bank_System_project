package com.banking.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WithdrawRequestDto {

    @NotNull
    private Long accountId;

    @DecimalMin(value = "1")
    private BigDecimal amount;
}