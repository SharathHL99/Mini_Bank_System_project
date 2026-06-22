package com.banking.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.banking.dto.CustomerRequestDto;
import com.banking.dto.CustomerResponseDto;
import com.banking.enums.CustomerStatus;
import com.banking.exception.ResourceNotFoundException;
import com.banking.model.Customer;
import com.banking.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerResponseDto createCustomer(CustomerRequestDto request) {

        Customer customer = Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .mobileNumber(request.getMobileNumber())
                .status(CustomerStatus.ACTIVE)
                .build();

        customerRepository.save(customer);

        return mapToResponse(customer);
    }

    public CustomerResponseDto getCustomerById(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found"));
        return mapToResponse(customer);
    }

    public List<CustomerResponseDto> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void blockCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found"));
        customer.setStatus(CustomerStatus.BLOCKED);
        customerRepository.save(customer);
    }

    public void activateCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found"));
        customer.setStatus(CustomerStatus.ACTIVE);
        customerRepository.save(customer);
    }

    private CustomerResponseDto mapToResponse(Customer customer) {
        return CustomerResponseDto.builder()
                .customerId(customer.getCustomerId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .mobileNumber(customer.getMobileNumber())
                .status(customer.getStatus().name())
                .build();
    }
}