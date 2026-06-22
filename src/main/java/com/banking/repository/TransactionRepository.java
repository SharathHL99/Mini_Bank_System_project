package com.banking.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.banking.model.BankTransaction;

public interface TransactionRepository extends JpaRepository<BankTransaction, Long> {

    @Query("SELECT t FROM BankTransaction t WHERE t.sourceAccount = :accountNumber OR t.destinationAccount = :accountNumber ORDER BY t.transactionDate DESC")
    List<BankTransaction> getStatement(@Param("accountNumber") String accountNumber);

    List<BankTransaction> findByTransactionDateBetween(LocalDateTime start,LocalDateTime end);

    long countByTransactionDateBetween(LocalDateTime start,LocalDateTime end);
}