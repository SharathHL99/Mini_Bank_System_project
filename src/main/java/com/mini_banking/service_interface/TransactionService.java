package com.mini_banking.service_interface;


import java.util.List;

import com.mini_banking.request_dto.DepositRequestDTO;
import com.mini_banking.request_dto.TransferRequestDTO;
import com.mini_banking.request_dto.WithdrawRequestDTO;
import com.mini_banking.response_dto.TransactionResponseDTO;

public interface TransactionService {

    TransactionResponseDTO deposit(DepositRequestDTO request);

    TransactionResponseDTO withdraw(WithdrawRequestDTO request);

    List<TransactionResponseDTO> transfer(TransferRequestDTO request);
}