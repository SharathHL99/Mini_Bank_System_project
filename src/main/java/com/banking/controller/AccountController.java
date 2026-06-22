package com.banking.controller;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.banking.dto.AccountRequestDto;
import com.banking.dto.AccountResponseDto;
import com.banking.dto.ApiResponse;
import com.banking.service.AccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponseDto>>
    createAccount(@Valid @RequestBody AccountRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<AccountResponseDto>builder()
                                .success(true)
                                .message("Account created successfully")
                                .data(accountService.createAccount(request))
                                .build()
                );
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountResponseDto>>>getAllAccounts() {
        return ResponseEntity.ok(ApiResponse.<List<AccountResponseDto>>builder()
                        .success(true)
                        .message("Accounts fetched successfully")
                        .data(accountService.getAllAccounts())
                        .build()
        );
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<ApiResponse<AccountResponseDto>>
    getAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(
                ApiResponse.<AccountResponseDto>builder()
                        .success(true)
                        .message("Account fetched successfully")
                        .data(accountService.getAccount(accountId))
                        .build()
        );
    }

    @PatchMapping("/{accountId}/deactivate")
    public ResponseEntity<ApiResponse<String>>
    deactivateAccount(@PathVariable Long accountId) {
        accountService.deactivateAccount(accountId);
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Account deactivated successfully")
                        .data("INACTIVE")
                        .build()
        );
    }

    @PatchMapping("/{accountId}/activate")
    public ResponseEntity<ApiResponse<String>>
    activateAccount(@PathVariable Long accountId) {
        accountService.activateAccount(accountId);
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Account activated successfully")
                        .data("ACTIVE")
                        .build()
        );
    }
}