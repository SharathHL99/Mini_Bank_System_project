package com.mini_banking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mini_banking.response_dto.BalanceSummaryDTO;
import com.mini_banking.response_dto.StatementEntryDTO;
import com.mini_banking.service_interface.ReportingService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/accounts/{accountId}")
@RequiredArgsConstructor
public class ReportingController {

    private final ReportingService reportingService;

    @GetMapping("/statement")
    public ResponseEntity<List<StatementEntryDTO>> getStatement(
            @PathVariable Long accountId,
            @RequestParam String from,
            @RequestParam String to) {
        
        LocalDate fromDate = LocalDate.parse(from);  
        LocalDate toDate = LocalDate.parse(to);      
        
        return ResponseEntity.ok(reportingService.getStatement(accountId, fromDate, toDate));
    }

    @GetMapping("/balance-summary")
    public ResponseEntity<BalanceSummaryDTO> getBalanceSummary(
            @PathVariable Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(reportingService.getBalanceSummary(accountId, from, to));
    }
}
