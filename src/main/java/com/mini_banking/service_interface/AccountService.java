package com.mini_banking.service_interface;


import java.util.List;

import com.mini_banking.request_dto.AccountRequestDTO;
import com.mini_banking.response_dto.AccountResponseDTO;

public interface AccountService {

    AccountResponseDTO createAccount(AccountRequestDTO request);
    void deleteAccount(Long accountId);
    AccountResponseDTO getAccount(Long accountId);
    List<AccountResponseDTO> getAccountsByCustomer(Long customerId);
}