package com.banking.service;

import com.banking.dto.CustomerRequestDto;
import com.banking.model.Customer;
import com.banking.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void createCustomerTest() {

        CustomerRequestDto dto = new CustomerRequestDto();
        dto.setFirstName("Rahul");
        dto.setLastName("Sharma");
        dto.setEmail("rahul@gmail.com");
        dto.setMobileNumber("9876543210");

        Customer customer = Customer.builder()
                .customerId(1L)
                .firstName("Rahul")
                .lastName("Sharma")
                .email("rahul@gmail.com")
                .mobileNumber("9876543210")
                .build();

        when(customerRepository.save(any(Customer.class)))
                .thenReturn(customer);

        assertNotNull(customerService.createCustomer(dto));

        verify(customerRepository,times(1))
                .save(any(Customer.class));
    }

    @Test
    void getCustomerByIdTest() {

        Customer customer = Customer.builder()
                .customerId(1L)
                .firstName("Rahul")
                .build();

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        assertNotNull(
                customerService.getCustomerById(1L));
    }
}
