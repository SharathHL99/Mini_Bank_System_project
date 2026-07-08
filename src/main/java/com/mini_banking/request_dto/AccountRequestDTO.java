package com.mini_banking.request_dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import com.mini_banking.entity.enums.AccountType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequestDTO {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    @PositiveOrZero(message = "Opening balance must be zero or positive")
    private BigDecimal openingBalance = BigDecimal.ZERO;
}