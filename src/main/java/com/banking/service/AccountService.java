package com.banking.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.banking.dto.AccountRequestDto;
import com.banking.dto.AccountResponseDto;
import com.banking.enums.AccountStatus;
import com.banking.exception.ResourceNotFoundException;
import com.banking.model.Account;
import com.banking.model.Customer;
import com.banking.repository.AccountRepository;
import com.banking.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public AccountResponseDto createAccount(AccountRequestDto request) {

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found"));

        Account account = Account.builder()
                .accountNumber(generateAccountNumber())
                .accountType(request.getAccountType())
                .balance(request.getInitialBalance())
                .status(AccountStatus.ACTIVE)
                .customer(customer)
                .build();

        accountRepository.save(account);

        return mapToResponse(account);
    }

    public AccountResponseDto getAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found"));
        return mapToResponse(account);
    }

    public List<AccountResponseDto> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void deactivateAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found"));
        account.setStatus(AccountStatus.INACTIVE);
        accountRepository.save(account);
    }

    public void activateAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found"));
        account.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(account);
    }

    private AccountResponseDto mapToResponse(Account account) {
        return AccountResponseDto.builder()
                .accountId(account.getAccountId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType().name())
                .balance(account.getBalance())
                .status(account.getStatus().name())
                .customerId(account.getCustomer().getCustomerId())
                .build();
    }

    private String generateAccountNumber() {
        return "ACC" + System.currentTimeMillis();
    }
}