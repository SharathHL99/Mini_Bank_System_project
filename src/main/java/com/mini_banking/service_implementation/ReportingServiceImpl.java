package com.mini_banking.service_implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.mini_banking.entity.Account;
import com.mini_banking.entity.Transaction;
import com.mini_banking.entity.enums.TransactionType;
import com.mini_banking.exception.ExceptionMessages;
import com.mini_banking.exception.ResourceNotFoundException;
import com.mini_banking.repository.AccountRepository;
import com.mini_banking.repository.TransactionRepository;
import com.mini_banking.response_dto.BalanceSummaryDTO;
import com.mini_banking.response_dto.StatementEntryDTO;
import com.mini_banking.service_interface.ReportingService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportingServiceImpl implements ReportingService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public List<StatementEntryDTO> getStatement(Long accountId, LocalDate from, LocalDate to) {
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.atTime(23, 59, 59);

        List<Transaction> transactions = transactionRepository.findStatement(accountId, start, end);

        return transactions.stream()
                .map(t -> {
                    StatementEntryDTO dto = new StatementEntryDTO();
                    dto.setDate(t.getCreatedAt());
                    dto.setTransactionType(t.getTransactionType());
                    dto.setAmount(t.getAmount());
                    dto.setBalanceAfter(t.getBalanceAfter());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public BalanceSummaryDTO getBalanceSummary(Long accountId, LocalDate from, LocalDate to) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessages.ACCOUNT_NOT_FOUND + accountId));

        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.atTime(23, 59, 59);

        BigDecimal totalDeposits = transactionRepository.sumByType(accountId, TransactionType.DEPOSIT, start, end);
        BigDecimal totalWithdrawals = transactionRepository.sumByType(accountId, TransactionType.WITHDRAWAL, start, end);

        BalanceSummaryDTO dto = new BalanceSummaryDTO();
        dto.setAccountId(accountId);
        dto.setCurrentBalance(account.getBalance());
        dto.setTotalDeposits(totalDeposits);
        dto.setTotalWithdrawals(totalWithdrawals);
        dto.setPeriodStart(from);
        dto.setPeriodEnd(to);
        return dto;
    }
}