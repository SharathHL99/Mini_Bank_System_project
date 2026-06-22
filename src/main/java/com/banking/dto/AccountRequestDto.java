package com.banking.dto;

import java.math.BigDecimal;
import com.banking.enums.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AccountRequestDto {

    @NotNull(message = "Customer Id is required")
    private Long customerId;

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    @NotNull(message = "Initial balance is required")
    private BigDecimal initialBalance;
}