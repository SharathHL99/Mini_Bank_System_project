package com.banking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.banking.model.AuditLog;

public interface AuditRepository
        extends JpaRepository<AuditLog, Long> {

}