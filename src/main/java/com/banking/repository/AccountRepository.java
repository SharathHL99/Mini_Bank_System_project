package com.banking.repository;

import java.math.BigDecimal;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.banking.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    @Query("SELECT COALESCE(SUM(a.balance), 0) FROM Account a")
    BigDecimal getTotalBankBalance();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.id = :id")
    Account findByIdForUpdate(@Param("id") Long id);

    @Query("SELECT a FROM Account a WHERE a.customer.customerId = :customerId")
    java.util.List<Account> findAccountsByCustomer(@Param("customerId") Long customerId);

    @Query(value = "SELECT *FROM accounts WHERE balance > :amount",nativeQuery = true)
    java.util.List<Account> findAccountsWithBalanceGreaterThan(@Param("amount") BigDecimal amount);
}