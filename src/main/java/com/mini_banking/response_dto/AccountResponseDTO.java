package com.mini_banking.response_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.mini_banking.entity.enums.AccountStatus;
import com.mini_banking.entity.enums.AccountType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDTO {

    private Long accountId;
    private Long customerId;
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal balance;
    private AccountStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}