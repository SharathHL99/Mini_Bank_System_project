package com.banking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.banking.dto.ApiResponse;
import com.banking.dto.CustomerRequestDto;
import com.banking.dto.CustomerResponseDto;
import com.banking.service.CustomerService;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponseDto>>
    createCustomer(@Valid @RequestBody CustomerRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<CustomerResponseDto>builder()
                                .success(true)
                                .message("Customer created successfully")
                                .data(customerService.createCustomer(request))
                                .build()
                );
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerResponseDto>>>getAllCustomers() {
        return ResponseEntity.ok(ApiResponse.<List<CustomerResponseDto>>builder()
                        .success(true)
                        .message("Customers fetched successfully")
                        .data(customerService.getAllCustomers())
                        .build());
        
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<CustomerResponseDto>>
    getCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(ApiResponse.<CustomerResponseDto>builder()
                        .success(true)
                        .message("Customer fetched successfully")
                        .data(customerService.getCustomerById(customerId))
                        .build());
      
    }

    @PatchMapping("/{customerId}/block")
    public ResponseEntity<ApiResponse<String>>
    blockCustomer(@PathVariable Long customerId) {
        customerService.blockCustomer(customerId);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                        .success(true)
                        .message("Customer blocked successfully")
                        .data("BLOCKED")
                        .build());
       
    }

    @PatchMapping("/{customerId}/activate")
    public ResponseEntity<ApiResponse<String>>
    activateCustomer(@PathVariable Long customerId) {
        customerService.activateCustomer(customerId);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                        .success(true)
                        .message("Customer activated successfully")
                        .data("ACTIVE")
                        .build());
        
    }
}