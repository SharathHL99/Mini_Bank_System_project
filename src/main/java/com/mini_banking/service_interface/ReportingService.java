package com.mini_banking.service_interface;


import java.time.LocalDate;
import java.util.List;

import com.mini_banking.response_dto.BalanceSummaryDTO;
import com.mini_banking.response_dto.StatementEntryDTO;

public interface ReportingService {

    List<StatementEntryDTO> getStatement(Long accountId, LocalDate from, LocalDate to);

    BalanceSummaryDTO getBalanceSummary(Long accountId, LocalDate from, LocalDate to);
}