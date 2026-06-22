package com.banking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.banking.dto.TransactionResponseDto;
import com.banking.enums.AccountStatus;
import com.banking.enums.TransactionType;
import com.banking.exception.InsufficientBalanceException;
import com.banking.exception.ResourceNotFoundException;
import com.banking.model.Account;
import com.banking.model.BankTransaction;
import com.banking.repository.AccountRepository;
import com.banking.repository.TransactionRepository;
import com.banking.util.TransactionReferenceGenerator;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionReferenceGenerator generator;
    private final AuditService auditService;

    @Transactional
    public TransactionResponseDto deposit(Long accountId,BigDecimal amount) {

        validateAmount(amount);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found"));

        validateAccountStatus(account);
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        BankTransaction transaction =saveTransaction(TransactionType.DEPOSIT,amount,account.getAccountNumber(),null);

        auditService.saveAudit("DEPOSIT",account.getAccountNumber());
        log.info("Deposit successful for account {}",account.getAccountNumber());
        return map(transaction);
    }

    @Transactional
    public TransactionResponseDto withdraw(Long accountId,BigDecimal amount) {
        validateAmount(amount);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found"));

        validateAccountStatus(account);
        if (account.getBalance().compareTo(amount) < 0) {
            log.error("Insufficient balance for account {}",account.getAccountNumber());
            throw new InsufficientBalanceException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
        BankTransaction transaction =saveTransaction(TransactionType.WITHDRAWAL,amount,account.getAccountNumber(),null);
        auditService.saveAudit("WITHDRAWAL",account.getAccountNumber());
        log.info("Withdrawal successful for account {}",account.getAccountNumber());
        return map(transaction);
    }

    @Transactional
    public TransactionResponseDto transfer(Long sourceId,Long destinationId,BigDecimal amount) {
        validateAmount(amount);
        if (sourceId.equals(destinationId)) {
            throw new IllegalArgumentException("Source and destination account cannot be same");
        }

        Account source =accountRepository.findByIdForUpdate(sourceId);
        if (source == null) {
            throw new ResourceNotFoundException("Source account not found");
        }

        Account destination =accountRepository.findByIdForUpdate(destinationId);
        if (destination == null) {
            throw new ResourceNotFoundException("Destination account not found");
        }

        validateAccountStatus(source);
        validateAccountStatus(destination);
        if (source.getBalance().compareTo(amount) < 0) {

            log.error("Transfer failed due to insufficient balance. Account: {}",source.getAccountNumber());
            throw new InsufficientBalanceException("Insufficient balance");
        }

        source.setBalance(source.getBalance().subtract(amount));
        destination.setBalance(destination.getBalance().add(amount));
        accountRepository.save(source);
        accountRepository.save(destination);

        BankTransaction transaction =saveTransaction(TransactionType.TRANSFER,amount,source.getAccountNumber(),
                        destination.getAccountNumber());

        auditService.saveAudit("TRANSFER",source.getAccountNumber());
        log.info("Transfer successful from {} to {}",source.getAccountNumber(),destination.getAccountNumber());
        return map(transaction);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null ||amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }

    private void validateAccountStatus(Account account) {
        if(account.getStatus()== AccountStatus.INACTIVE) {
            throw new IllegalArgumentException("Account is inactive");
        }

        if(account.getStatus()== AccountStatus.CLOSED) {
            throw new IllegalArgumentException("Account is closed");
        }
    }

    private BankTransaction saveTransaction(TransactionType type,BigDecimal amount,String source,String destination) {
        String remarks;

        switch (type) {

            case DEPOSIT:
                remarks = "Amount deposited successfully";
                break;

            case WITHDRAWAL:
                remarks = "Amount withdrawn successfully";
                break;

            case TRANSFER:
                remarks = "Fund transfer completed successfully";
                break;

            default:
                remarks = "Transaction completed";
        }

        BankTransaction transaction = BankTransaction.builder()
                .transactionReference(generator.generateReference())
                .transactionType(type)
                .amount(amount)
                .remarks(remarks)
                .sourceAccount(source)
                .destinationAccount(destination)
                .transactionDate(LocalDateTime.now())
                .build();

        return transactionRepository.save(transaction);
    }

    private TransactionResponseDto map(BankTransaction transaction) {
        return TransactionResponseDto.builder()
                .transactionReference(transaction.getTransactionReference())
                .transactionType(transaction.getTransactionType().name())
                .amount(transaction.getAmount())
                .sourceAccount(transaction.getSourceAccount())
                .destinationAccount(transaction.getDestinationAccount())
                .transactionDate(transaction.getTransactionDate())
                .build();
    }

    public List<TransactionResponseDto>getStatement(String accountNumber) {
        auditService.saveAudit("STATEMENT_VIEWED",accountNumber);
        log.info("Statement requested for account {}",accountNumber);
        return transactionRepository
                .getStatement(accountNumber)
                .stream()
                .map(this::map)
                .toList();
    }
}