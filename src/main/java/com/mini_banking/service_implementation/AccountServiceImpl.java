package com.mini_banking.service_implementation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.mini_banking.entity.Account;
import com.mini_banking.entity.Customer;
import com.mini_banking.entity.enums.AccountStatus;
import com.mini_banking.exception.ExceptionMessages;
import com.mini_banking.exception.ResourceNotFoundException;
import com.mini_banking.repository.AccountRepository;
import com.mini_banking.repository.CustomerRepository;
import com.mini_banking.request_dto.AccountRequestDTO;
import com.mini_banking.response_dto.AccountResponseDTO;
import com.mini_banking.service_interface.AccountService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    @Override
    public AccountResponseDTO createAccount(AccountRequestDTO request) {

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessages.CUSTOMER_NOT_FOUND + request.getCustomerId()));

        Account account = new Account();
        account.setCustomer(customer);
        account.setAccountNumber(generateAccountNumber());
        account.setAccountType(request.getAccountType());
        account.setBalance(request.getOpeningBalance());
        account.setStatus(AccountStatus.ACTIVE);

        Account savedAccount = accountRepository.save(account);

        return convertToDTO(savedAccount);
    }

    @Override
    public AccountResponseDTO getAccount(Long accountId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessages.ACCOUNT_NOT_FOUND + accountId));

        return convertToDTO(account);
    }

    @Override
    public List<AccountResponseDTO> getAccountsByCustomer(Long customerId) {

        List<Account> accounts = accountRepository.findByCustomer_CustomerId(customerId);

        List<AccountResponseDTO> accountDTOList = new ArrayList<>();

        for (Account account : accounts) {
            AccountResponseDTO dto = convertToDTO(account);
            accountDTOList.add(dto);
        }

        return accountDTOList;
    }

    @Override
    public void deleteAccount(Long accountId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessages.ACCOUNT_NOT_FOUND + accountId));

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new IllegalStateException("Account is already closed");
        }

        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException(
                    "Cannot delete account with remaining balance: " + account.getBalance());
        }

        account.setStatus(AccountStatus.CLOSED);

        accountRepository.save(account);
    }

    private String generateAccountNumber() {

        String random = UUID.randomUUID().toString().replace("-", "");

        return "AC" + random.substring(0, 10).toUpperCase();
    }

    private AccountResponseDTO convertToDTO(Account account) {

        AccountResponseDTO dto = new AccountResponseDTO();

        dto.setAccountId(account.getAccountId());
        dto.setCustomerId(account.getCustomer().getCustomerId());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setAccountType(account.getAccountType());
        dto.setBalance(account.getBalance());
        dto.setStatus(account.getStatus());
        dto.setCreatedAt(account.getCreatedAt());
        dto.setUpdatedAt(account.getUpdatedAt());

        return dto;
    }
}
