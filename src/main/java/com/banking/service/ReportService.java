package com.banking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.banking.model.BankTransaction;
import com.banking.repository.AccountRepository;
import com.banking.repository.CustomerRepository;
import com.banking.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public Map<String, Object> dashboard() {

        Map<String, Object> dashboard =new HashMap<>();

        dashboard.put("totalCustomers",customerRepository.count());
        dashboard.put("totalAccounts",accountRepository.count());
        dashboard.put("totalBalance",accountRepository.getTotalBankBalance());

        return dashboard;
    }

    public BigDecimal getTotalBankBalance() {
        return accountRepository.getTotalBankBalance();
    }

    public List<BankTransaction>
    getTodayTransactions() {
        LocalDate today = LocalDate.now();
        LocalDateTime start =today.atStartOfDay();
        LocalDateTime end =today.atTime(23,59,59);

        return transactionRepository.findByTransactionDateBetween(start,end);
    }
}