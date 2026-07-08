package com.mini_banking.service_implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mini_banking.entity.Account;
import com.mini_banking.entity.Customer;
import com.mini_banking.entity.enums.AccountStatus;
import com.mini_banking.exception.ExceptionMessages;
import com.mini_banking.exception.ResourceNotFoundException;
import com.mini_banking.repository.AccountRepository;
import com.mini_banking.repository.CustomerRepository;
import com.mini_banking.request_dto.CustomerRequestDTO;
import com.mini_banking.response_dto.CustomerResponseDTO;
import com.mini_banking.service_interface.CustomerService;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    @Override
    public CustomerResponseDTO createCustomer(CustomerRequestDTO request) {
        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());

        Customer saved = customerRepository.save(customer);
        return toDto(saved);
    }

    @Override
    public CustomerResponseDTO getCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessages.CUSTOMER_NOT_FOUND + customerId));
        return toDto(customer);
    }

    @Override
    public List<CustomerResponseDTO> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessages.CUSTOMER_NOT_FOUND + customerId));

        for (Account account : customer.getAccounts()) {
            if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                throw new IllegalStateException(
                        "Cannot delete customer. Account " + account.getAccountNumber() +
                        " still has balance: " + account.getBalance());
            }
        }

        for (Account account : customer.getAccounts()) {
            account.setStatus(AccountStatus.CLOSED);
            accountRepository.save(account);
        }

        customerRepository.delete(customer);
    }

    private CustomerResponseDTO toDto(Customer customer) {
        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setCustomerId(customer.getCustomerId());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setCreatedAt(customer.getCreatedAt());
        dto.setUpdateAt(customer.getUpdatedAt());
        return dto;
    }
}