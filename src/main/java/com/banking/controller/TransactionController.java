package com.banking.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.banking.dto.ApiResponse;
import com.banking.dto.DepositRequestDto;
import com.banking.dto.TransactionResponseDto;
import com.banking.dto.TransferRequestDto;
import com.banking.dto.WithdrawRequestDto;
import com.banking.service.TransactionService;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<TransactionResponseDto>>
    deposit(@Valid @RequestBody DepositRequestDto request) {
        return ResponseEntity.ok(
                ApiResponse.<TransactionResponseDto>builder()
                        .success(true)
                        .message("Deposit successful")
                        .data(transactionService.deposit(request.getAccountId(),request.getAmount()
                                )
                        )
                        .build()
        );
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<TransactionResponseDto>>
    withdraw(@Valid @RequestBody WithdrawRequestDto request) {
        return ResponseEntity.ok(
                ApiResponse.<TransactionResponseDto>builder()
                        .success(true)
                        .message("Withdrawal successful")
                        .data(transactionService.withdraw(request.getAccountId(),request.getAmount()
                                )
                        )
                        .build()
        );
    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransactionResponseDto>>
    transfer(@Valid @RequestBody TransferRequestDto request) {
        return ResponseEntity.ok(
                ApiResponse.<TransactionResponseDto>builder()
                        .success(true)
                        .message("Transfer successful")
                        .data(transactionService.transfer(request.getSourceAccountId(),
                                        request.getDestinationAccountId(),
                                        request.getAmount()
                                )
                        )
                        .build()
        );
    }

    @GetMapping("/statement/{accountNumber}")
    public ResponseEntity<ApiResponse<List<TransactionResponseDto>>>
    statement(@PathVariable String accountNumber) {
        return ResponseEntity.ok(
                ApiResponse.<List<TransactionResponseDto>>builder()
                        .success(true)
                        .message("Statement fetched successfully")
                        .data(transactionService.getStatement(accountNumber)
                        )
                        .build()
        );
    }
}