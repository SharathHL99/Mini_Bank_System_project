DROP TABLE IF EXISTS audit_logs;
DROP TABLE IF EXISTS bank_transactions;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS customers;

-- CUSTOMERS

CREATE TABLE customers (
    customer_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    mobile_number VARCHAR(15) UNIQUE NOT NULL,
    status VARCHAR(20) NOT NULL
);

-- ACCOUNTS

CREATE TABLE accounts (
    account_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_number VARCHAR(30) UNIQUE NOT NULL,
    balance DECIMAL(19,2) NOT NULL,
    account_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    customer_id BIGINT NOT NULL,
    CONSTRAINT fk_account_customer
    FOREIGN KEY (customer_id)
    REFERENCES customers(customer_id)
);


-- BANK TRANSACTIONS

CREATE TABLE bank_transactions (
    transaction_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    transaction_reference VARCHAR(50)
    UNIQUE NOT NULL,
    transaction_type VARCHAR(20)
    NOT NULL,
    amount DECIMAL(19,2)
    NOT NULL,
    source_account VARCHAR(30),
    destination_account VARCHAR(30),
    transaction_date TIMESTAMP
    NOT NULL
);


-- AUDIT LOGS

CREATE TABLE audit_logs (
    audit_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    action VARCHAR(255)
    NOT NULL,
    performed_by VARCHAR(100)
    NOT NULL,
    action_time TIMESTAMP
    NOT NULL
);