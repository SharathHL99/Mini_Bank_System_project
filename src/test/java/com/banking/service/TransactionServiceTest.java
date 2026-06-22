package com.banking.service;

import com.banking.dto.TransactionResponseDto;
import com.banking.exception.InsufficientBalanceException;
import com.banking.model.Account;
import com.banking.model.BankTransaction;
import com.banking.repository.AccountRepository;
import com.banking.repository.TransactionRepository;
import com.banking.util.TransactionReferenceGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionReferenceGenerator generator;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void depositTest() {

        Account account = Account.builder()
                .accountId(1L)
                .accountNumber("ACC100001")
                .balance(BigDecimal.valueOf(1000))
                .build();

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        when(generator.generateReference())
                .thenReturn("TXN100001");

        when(transactionRepository.save(any(BankTransaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponseDto response =
                transactionService.deposit(
                        1L,
                        BigDecimal.valueOf(500));

        assertNotNull(response);

        verify(accountRepository)
                .save(any(Account.class));
    }

    @Test
    void withdrawTest() {

        Account account = Account.builder()
                .accountId(1L)
                .accountNumber("ACC100001")
                .balance(BigDecimal.valueOf(5000))
                .build();

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        when(generator.generateReference())
                .thenReturn("TXN100002");

        when(transactionRepository.save(any(BankTransaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponseDto response =
                transactionService.withdraw(
                        1L,
                        BigDecimal.valueOf(1000));

        assertNotNull(response);
    }

    @Test
    void transferTest() {

        Account source = Account.builder()
                .accountId(1L)
                .accountNumber("ACC100001")
                .balance(BigDecimal.valueOf(10000))
                .build();

        Account destination = Account.builder()
                .accountId(2L)
                .accountNumber("ACC100002")
                .balance(BigDecimal.valueOf(2000))
                .build();

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(source));

        when(accountRepository.findById(2L))
                .thenReturn(Optional.of(destination));

        when(generator.generateReference())
                .thenReturn("TXN100003");

        when(transactionRepository.save(any(BankTransaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponseDto response =
                transactionService.transfer(
                        1L,
                        2L,
                        BigDecimal.valueOf(3000));

        assertNotNull(response);
    }

    @Test
    void insufficientBalanceTest() {

        Account account = Account.builder()
                .accountId(1L)
                .accountNumber("ACC100001")
                .balance(BigDecimal.valueOf(1000))
                .build();

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        assertThrows(
                InsufficientBalanceException.class,
                () -> transactionService.withdraw(
                        1L,
                        BigDecimal.valueOf(5000))
        );
    }
}