package com.banking.util;

import org.springframework.stereotype.Component;

@Component
public class AccountNumberGenerator {

    public String generateAccountNumber() {

        return "ACC" + System.currentTimeMillis();
    }
}