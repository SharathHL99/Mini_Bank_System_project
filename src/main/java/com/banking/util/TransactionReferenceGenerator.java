package com.banking.util;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class TransactionReferenceGenerator {

    public String generateReference() {

        return "TXN" +
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 10);
    }
}