package com.mini_banking.response_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.mini_banking.entity.enums.TransactionType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatementEntryDTO {

    private LocalDateTime date;
    private TransactionType transactionType;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
}