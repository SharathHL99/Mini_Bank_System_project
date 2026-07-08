package com.mini_banking.service_implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mini_banking.entity.Account;
import com.mini_banking.entity.AuditLog;
import com.mini_banking.entity.Transaction;
import com.mini_banking.entity.enums.TransactionStatus;
import com.mini_banking.entity.enums.TransactionType;
import com.mini_banking.exception.ExceptionMessages;
import com.mini_banking.exception.ResourceNotFoundException;
import com.mini_banking.repository.AccountRepository;
import com.mini_banking.repository.AuditLogRepository;
import com.mini_banking.repository.TransactionRepository;
import com.mini_banking.request_dto.DepositRequestDTO;
import com.mini_banking.request_dto.TransferRequestDTO;
import com.mini_banking.request_dto.WithdrawRequestDTO;
import com.mini_banking.response_dto.TransactionResponseDTO;
import com.mini_banking.service_interface.TransactionService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public TransactionResponseDTO deposit(DepositRequestDTO request) {
        Account account = accountRepository.findByIdForUpdate(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.ACCOUNT_NOT_FOUND + request.getAccountId()));

        account.setBalance(account.getBalance().add(request.getAmount()));
        accountRepository.save(account);

        Transaction txn = saveTransaction(account, TransactionType.DEPOSIT, request.getAmount(), null);
        logAudit(txn, "DEPOSIT_PROCESSED", "system");

        return toDto(txn);
    }

    @Override
    @Transactional
    public TransactionResponseDTO withdraw(WithdrawRequestDTO request) {
        Account account = accountRepository.findByIdForUpdate(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessages.ACCOUNT_NOT_FOUND + request.getAccountId()));

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new ResourceNotFoundException(
                    ExceptionMessages.INSUFFICIENT_BALANCE + account.getAccountId());
        }

        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountRepository.save(account);

        Transaction txn = saveTransaction(account, TransactionType.WITHDRAWAL, request.getAmount(), null);
        logAudit(txn, "WITHDRAWAL_PROCESSED", "system");

        return toDto(txn);
    }

    @Override
    @Transactional
    public List<TransactionResponseDTO> transfer(TransferRequestDTO request) {
        if (request.getFromAccountId().equals(request.getToAccountId())) {
            throw new ResourceNotFoundException(ExceptionMessages.SAME_ACCOUNT_TRANSFER);
        }

        Long firstLockId = Math.min(request.getFromAccountId(), request.getToAccountId());
        Long secondLockId = Math.max(request.getFromAccountId(), request.getToAccountId());

        Account firstLocked = accountRepository.findByIdForUpdate(firstLockId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessages.ACCOUNT_NOT_FOUND + firstLockId));
        Account secondLocked = accountRepository.findByIdForUpdate(secondLockId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessages.ACCOUNT_NOT_FOUND + secondLockId));

        Account fromAccount = firstLocked.getAccountId().equals(request.getFromAccountId()) ? firstLocked : secondLocked;
        Account toAccount = firstLocked.getAccountId().equals(request.getToAccountId()) ? firstLocked : secondLocked;

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new ResourceNotFoundException(
                    ExceptionMessages.INSUFFICIENT_BALANCE + fromAccount.getAccountId());
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

         String transferRefId = generateTransferRefId(fromAccount, toAccount);

        Transaction debitTxn = saveTransaction(fromAccount, TransactionType.TRANSFER_OUT, request.getAmount(), transferRefId);
        Transaction creditTxn = saveTransaction(toAccount, TransactionType.TRANSFER_IN, request.getAmount(), transferRefId);

        logAudit(debitTxn, "TRANSFER_OUT_PROCESSED", "system");
        logAudit(creditTxn, "TRANSFER_IN_PROCESSED", "system");

        return Arrays.asList(toDto(debitTxn), toDto(creditTxn));
    }
private String generateTransferRefId(Account fromAccount, Account toAccount) {
    return "TXN-REF-" + fromAccount.getAccountNumber() + "-"
            + toAccount.getAccountNumber() + "-" + System.currentTimeMillis();
}
    private Transaction saveTransaction(Account account, TransactionType type, BigDecimal amount, String transferRefId) {
        Transaction txn = new Transaction();
        txn.setAccount(account);
        txn.setTransactionType(type);
        txn.setAmount(amount);
        txn.setBalanceAfter(account.getBalance());
        txn.setTransferRefId(transferRefId);
        txn.setStatus(TransactionStatus.SUCCESS);
        return transactionRepository.save(txn);
    }

    private void logAudit(Transaction txn, String action, String performedBy) {
        AuditLog log = new AuditLog();
        log.setTransaction(txn);
        log.setAction(action);
        log.setPerformedBy(performedBy);
        log.setDetails("Transaction " + txn.getTransactionId() + " of type " + txn.getTransactionType());
        auditLogRepository.save(log);
    }

    private TransactionResponseDTO toDto(Transaction txn) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setTransactionId(txn.getTransactionId());
        dto.setAccountId(txn.getAccount().getAccountId());
        dto.setTransactionType(txn.getTransactionType());
        dto.setAmount(txn.getAmount());
        dto.setBalanceAfter(txn.getBalanceAfter());
        dto.setTransferRefId(txn.getTransferRefId());
        dto.setStatus(txn.getStatus());
        dto.setCreatedAt(txn.getCreatedAt());
        dto.setUpdatedAt(txn.getUpdatedAt());
        return dto;
    }
}
