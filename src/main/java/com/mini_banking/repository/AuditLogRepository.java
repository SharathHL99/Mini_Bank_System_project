package com.mini_banking.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.mini_banking.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}