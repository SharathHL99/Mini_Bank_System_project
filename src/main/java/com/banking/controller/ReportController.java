package com.banking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.banking.dto.ApiResponse;
import com.banking.model.BankTransaction;
import com.banking.service.ReportService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>>dashboard() {
        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                        .success(true)
                        .message("Dashboard fetched successfully")
                        .data(reportService.dashboard())
                        .build()
        );
    }

    @GetMapping("/total-balance")
    public ResponseEntity<ApiResponse<BigDecimal>>totalBalance() {
        return ResponseEntity.ok(ApiResponse.<BigDecimal>builder()
                        .success(true)
                        .message("Total bank balance fetched")
                        .data(reportService.getTotalBankBalance())
                        .build()
        );
    }

    @GetMapping("/today-transactions")
    public ResponseEntity<ApiResponse<List<BankTransaction>>>todayTransactions() {
        return ResponseEntity.ok(ApiResponse.<List<BankTransaction>>builder()
                        .success(true)
                        .message("Today's transactions fetched")
                        .data(reportService.getTodayTransactions())
                        .build()
        );
    }
}