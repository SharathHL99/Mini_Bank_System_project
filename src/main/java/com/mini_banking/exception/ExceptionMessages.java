package com.mini_banking.exception;



public final class ExceptionMessages {

    private ExceptionMessages() {
    }

    public static final String CUSTOMER_NOT_FOUND = "Customer not found with id: ";
    public static final String ACCOUNT_NOT_FOUND = "Account not found with id: ";
    public static final String INSUFFICIENT_BALANCE = "Insufficient balance in account  for this ";
    public static final String SAME_ACCOUNT_TRANSFER = "Cannot transfer to the same account";
}