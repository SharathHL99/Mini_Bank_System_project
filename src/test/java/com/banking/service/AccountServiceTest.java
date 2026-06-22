package com.banking.service;

import com.banking.dto.AccountRequestDto;
import com.banking.enums.AccountType;
import com.banking.model.Account;
import com.banking.model.Customer;
import com.banking.repository.AccountRepository;
import com.banking.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void createAccountTest() {

        Customer customer = Customer.builder()
                .customerId(1L)
                .firstName("Rahul")
                .lastName("Sharma")
                .email("rahul@gmail.com")
                .build();

        AccountRequestDto dto = new AccountRequestDto();
        dto.setCustomerId(1L);

     
        dto.setAccountType(AccountType.SAVINGS);

        Account account = Account.builder()
                .accountId(1L)
                .accountNumber("ACC100001")
                .balance(BigDecimal.ZERO)
                .accountType(AccountType.SAVINGS)
                .customer(customer)
                .build();

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(accountRepository.save(any(Account.class)))
                .thenReturn(account);

        assertNotNull(accountService.createAccount(dto));

        verify(accountRepository, times(1))
                .save(any(Account.class));
    }

    @Test
    void getAccountTest() {
        Account account = Account.builder()
                .accountId(1L)
                .accountNumber("ACC100001")
                .balance(BigDecimal.ZERO)
                .accountType(AccountType.SAVINGS)
                .build();

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        assertNotNull(accountService.getAccount(1L));
    }
}