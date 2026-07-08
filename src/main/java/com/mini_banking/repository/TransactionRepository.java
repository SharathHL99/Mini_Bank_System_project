package com.mini_banking.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mini_banking.entity.Transaction;
import com.mini_banking.entity.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.account.accountId = :accountId " +
           "AND t.createdAt BETWEEN :from AND :to ORDER BY t.createdAt ASC")
    List<Transaction> findStatement(@Param("accountId") Long accountId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.account.accountId = :accountId " +
           "AND t.transactionType = :type AND t.createdAt BETWEEN :from AND :to")
    BigDecimal sumByType(@Param("accountId") Long accountId, @Param("type") TransactionType type,
                         @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    
    
    List<Transaction> findByTransferRefId(String transferRefId);
}