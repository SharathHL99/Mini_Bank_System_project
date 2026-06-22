package com.banking.service;


import org.springframework.stereotype.Service;

import com.banking.model.AuditLog;
import com.banking.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditRepository auditRepository;

    public void saveAudit(String action,String performedBy) {

        AuditLog auditLog = AuditLog.builder()
                .action(action)
                .performedBy(performedBy)
                .actionTime(LocalDateTime.now())
                .build();

        auditRepository.save(auditLog);
    }
}