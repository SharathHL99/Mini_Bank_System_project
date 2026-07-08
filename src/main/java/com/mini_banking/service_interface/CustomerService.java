package com.mini_banking.service_interface;



import java.util.List;

import com.mini_banking.request_dto.CustomerRequestDTO;
import com.mini_banking.response_dto.CustomerResponseDTO;

public interface CustomerService {

    CustomerResponseDTO createCustomer(CustomerRequestDTO request);
    void deleteCustomer(Long customerId);
    CustomerResponseDTO getCustomer(Long customerId);
    List<CustomerResponseDTO> getAllCustomers();
}