package com.mini_banking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mini_banking.request_dto.DepositRequestDTO;
import com.mini_banking.request_dto.TransferRequestDTO;
import com.mini_banking.request_dto.WithdrawRequestDTO;
import com.mini_banking.response_dto.TransactionResponseDTO;
import com.mini_banking.service_interface.TransactionService;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponseDTO> deposit(@Valid @RequestBody DepositRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.deposit(request));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponseDTO> withdraw(@Valid @RequestBody WithdrawRequestDTO request) {
        return ResponseEntity.ok(transactionService.withdraw(request));
    }

    @PostMapping("/transfer")
    public ResponseEntity<List<TransactionResponseDTO>> transfer(@Valid @RequestBody TransferRequestDTO request) {
        return ResponseEntity.ok(transactionService.transfer(request));
    }
}