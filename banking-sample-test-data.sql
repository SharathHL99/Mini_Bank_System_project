-- =====================================================================
-- Banking Mini Core System - Sample Test Data Scripts
-- Target: MySQL 8.x
-- Run AFTER banking-schema-erd.sql has created the schema/tables.
-- Provides a richer dataset than the minimal seed in the schema file,
-- covering normal flows, edge cases, and rejected/failed scenarios
-- for use in manual testing, Postman collection runs, and integration tests.
-- =====================================================================

USE banking_mini_core;

-- Clean slate for repeatable test runs (respect FK order)
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE daily_balance_snapshot;
TRUNCATE TABLE audit_log;
TRUNCATE TABLE transaction;
TRUNCATE TABLE account;
TRUNCATE TABLE customer;
SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------------
-- 1. CUSTOMERS (5 test customers covering different statuses)
-- ---------------------------------------------------------------------
INSERT INTO customer (customer_id, first_name, last_name, email, phone, address, date_of_birth, kyc_document_number, status, created_at) VALUES
  (1, 'Alice',  'Sharma', 'alice.sharma@example.com', '9876543210', 'MG Road, Bengaluru',        '1995-04-12', 'ABCDE1234F', 'ACTIVE',   '2026-01-05 09:00:00'),
  (2, 'Bob',    'Verma',  'bob.verma@example.com',    '9876500000', 'Indiranagar, Bengaluru',    '1990-08-25', 'FGHIJ5678K', 'ACTIVE',   '2026-01-06 10:30:00'),
  (3, 'Carol',  'Nair',   'carol.nair@example.com',   '9812345678', 'Koramangala, Bengaluru',    '1988-11-02', 'KLMNO9012P', 'ACTIVE',   '2026-01-10 11:15:00'),
  (4, 'David',  'Iyer',   'david.iyer@example.com',   '9845612378', 'Whitefield, Bengaluru',     '2000-02-17', 'QRSTU3456V', 'ACTIVE',   '2026-02-01 08:45:00'),
  (5, 'Emma',   'Fernandes', 'emma.fernandes@example.com', '9900112233', 'HSR Layout, Bengaluru', '1998-07-30', 'WXYZA7890B', 'INACTIVE', '2026-02-15 14:00:00');

-- ---------------------------------------------------------------------
-- 2. ACCOUNTS (multiple accounts per customer; mix of SAVINGS/CURRENT;
--    includes a CURRENT account with an overdraft limit, and a CLOSED
--    account for edge-case testing)
-- ---------------------------------------------------------------------
INSERT INTO account (account_id, customer_id, account_number, account_type, balance, overdraft_limit, currency, status, version, created_at) VALUES
  (1001, 1, 'BNK0000001001', 'SAVINGS', 5000.00,     0.00, 'INR', 'ACTIVE', 0, '2026-01-05 09:05:00'),
  (1002, 1, 'BNK0000001002', 'CURRENT', 3000.00, 10000.00, 'INR', 'ACTIVE', 0, '2026-01-05 09:10:00'),
  (1003, 2, 'BNK0000001003', 'SAVINGS', 2000.00,     0.00, 'INR', 'ACTIVE', 0, '2026-01-06 10:35:00'),
  (1004, 3, 'BNK0000001004', 'SAVINGS',  750.50,     0.00, 'INR', 'ACTIVE', 0, '2026-01-10 11:20:00'),
  (1005, 3, 'BNK0000001005', 'CURRENT', 1200.00,  5000.00, 'INR', 'ACTIVE', 0, '2026-01-10 11:25:00'),
  (1006, 4, 'BNK0000001006', 'SAVINGS',    0.00,     0.00, 'INR', 'ACTIVE', 0, '2026-02-01 08:50:00'),  -- zero-balance account for overdraft testing
  (1007, 5, 'BNK0000001007', 'SAVINGS',  100.00,     0.00, 'INR', 'CLOSED', 0, '2026-02-15 14:05:00');  -- closed account for rejection testing

-- ---------------------------------------------------------------------
-- 3. TRANSACTIONS
--    Covers: DEPOSIT, WITHDRAWAL, matched TRANSFER_DEBIT/TRANSFER_CREDIT
--    legs (same transaction_ref), a FAILED withdrawal (insufficient funds),
--    and a REJECTED transaction on a CLOSED account.
-- ---------------------------------------------------------------------

-- Simple deposits
INSERT INTO transaction (transaction_id, transaction_ref, account_id, related_account_id, transaction_type, amount, balance_after, status, description, created_at) VALUES
  (5001, 'DEP-20260705-0001', 1001, NULL, 'DEPOSIT', 5000.00, 5000.00, 'SUCCESS', 'Initial cash deposit', '2026-01-05 09:05:00'),
  (5002, 'DEP-20260706-0001', 1003, NULL, 'DEPOSIT', 2000.00, 2000.00, 'SUCCESS', 'Initial cash deposit', '2026-01-06 10:35:00'),
  (5003, 'DEP-20260707-0001', 1001, NULL, 'DEPOSIT', 2000.00, 7000.00, 'SUCCESS', 'Salary credit', '2026-07-01 09:30:00');

-- Simple withdrawal (successful)
INSERT INTO transaction (transaction_id, transaction_ref, account_id, related_account_id, transaction_type, amount, balance_after, status, description, created_at) VALUES
  (5004, 'WDL-20260702-0001', 1001, NULL, 'WITHDRAWAL', 1500.00, 5500.00, 'SUCCESS', 'ATM withdrawal', '2026-07-02 12:00:00');

-- Matched fund transfer: account 1001 -> account 1003, amount 1000.00
INSERT INTO transaction (transaction_id, transaction_ref, account_id, related_account_id, transaction_type, amount, balance_after, status, description, created_at) VALUES
  (5005, 'TRF-20260703-0001', 1001, 1003, 'TRANSFER_DEBIT',  1000.00, 4500.00, 'SUCCESS', 'Fund transfer to Bob Verma', '2026-07-03 15:10:00'),
  (5006, 'TRF-20260703-0001', 1003, 1001, 'TRANSFER_CREDIT', 1000.00, 3000.00, 'SUCCESS', 'Fund transfer from Alice Sharma', '2026-07-03 15:10:00');

-- Matched fund transfer: account 1004 -> account 1005 (same customer, different accounts)
INSERT INTO transaction (transaction_id, transaction_ref, account_id, related_account_id, transaction_type, amount, balance_after, status, description, created_at) VALUES
  (5007, 'TRF-20260704-0001', 1004, 1005, 'TRANSFER_DEBIT',  250.50, 500.00,  'SUCCESS', 'Self transfer to CURRENT account', '2026-07-04 09:00:00'),
  (5008, 'TRF-20260704-0001', 1005, 1004, 'TRANSFER_CREDIT', 250.50, 1450.50, 'SUCCESS', 'Self transfer from SAVINGS account', '2026-07-04 09:00:00');

-- FAILED withdrawal - insufficient funds (account 1006 has 0 balance, no overdraft)
INSERT INTO transaction (transaction_id, transaction_ref, account_id, related_account_id, transaction_type, amount, balance_after, status, description, created_at) VALUES
  (5009, 'WDL-20260705-0002', 1006, NULL, 'WITHDRAWAL', 500.00, 0.00, 'FAILED', 'Rejected: insufficient funds, overdraft not permitted', '2026-07-05 16:20:00');

-- REJECTED transaction attempt on a CLOSED account
INSERT INTO transaction (transaction_id, transaction_ref, account_id, related_account_id, transaction_type, amount, balance_after, status, description, created_at) VALUES
  (5010, 'DEP-20260706-0002', 1007, NULL, 'DEPOSIT', 200.00, 100.00, 'REJECTED', 'Rejected: account status is CLOSED', '2026-07-06 10:00:00');

-- Overdraft usage within limit on CURRENT account 1002 (limit 10000.00)
INSERT INTO transaction (transaction_id, transaction_ref, account_id, related_account_id, transaction_type, amount, balance_after, status, description, created_at) VALUES
  (5011, 'WDL-20260706-0003', 1002, NULL, 'WITHDRAWAL', 8000.00, -5000.00, 'SUCCESS', 'Business expense payment (overdraft utilized)', '2026-07-06 11:00:00');

-- ---------------------------------------------------------------------
-- 4. AUDIT LOGS
--    Mirrors the transactions above plus non-transactional events
--    (account opened, customer status changed).
-- ---------------------------------------------------------------------
INSERT INTO audit_log (audit_id, customer_id, account_id, transaction_id, action, performed_by, details, ip_address, created_at) VALUES
  (9001, 1, 1001, NULL, 'CUSTOMER_CREATED',   'SYSTEM', 'Customer Alice Sharma onboarded', '10.0.0.11', '2026-01-05 09:00:00'),
  (9002, 1, 1001, NULL, 'ACCOUNT_OPENED',     'SYSTEM', 'SAVINGS account BNK0000001001 opened with initial deposit 5000.00', '10.0.0.11', '2026-01-05 09:05:00'),
  (9003, 1, 1002, NULL, 'ACCOUNT_OPENED',     'SYSTEM', 'CURRENT account BNK0000001002 opened with overdraft limit 10000.00', '10.0.0.11', '2026-01-05 09:10:00'),
  (9004, 2, 1003, NULL, 'ACCOUNT_OPENED',     'SYSTEM', 'SAVINGS account BNK0000001003 opened with initial deposit 2000.00', '10.0.0.12', '2026-01-06 10:35:00'),
  (9005, 1, 1001, 5004, 'WITHDRAWAL_SUCCESS', 'SYSTEM', 'Withdrawal of 1500.00 processed successfully', '10.0.0.11', '2026-07-02 12:00:00'),
  (9006, 1, 1001, 5005, 'TRANSFER_SUCCESS',   'SYSTEM', 'Transfer of 1000.00 to account 1003 completed', '10.0.0.11', '2026-07-03 15:10:00'),
  (9007, 2, 1003, 5006, 'TRANSFER_SUCCESS',   'SYSTEM', 'Received transfer of 1000.00 from account 1001', '10.0.0.12', '2026-07-03 15:10:00'),
  (9008, 4, 1006, 5009, 'WITHDRAWAL_REJECTED','SYSTEM', 'Attempted withdrawal of 500.00 exceeds available balance 0.00', '10.0.0.14', '2026-07-05 16:20:00'),
  (9009, 5, 1007, 5010, 'DEPOSIT_REJECTED',   'SYSTEM', 'Attempted deposit on CLOSED account BNK0000001007', '10.0.0.15', '2026-07-06 10:00:00'),
  (9010, 5, 1007, NULL, 'CUSTOMER_STATUS_CHANGED', 'ADMIN_USER_1', 'Customer status changed from ACTIVE to INACTIVE', '10.0.0.99', '2026-02-15 14:00:00'),
  (9011, 1, 1002, 5011, 'WITHDRAWAL_SUCCESS', 'SYSTEM', 'Withdrawal of 8000.00 processed using overdraft facility', '10.0.0.11', '2026-07-06 11:00:00');

-- ---------------------------------------------------------------------
-- 5. DAILY BALANCE SNAPSHOTS
--    Pre-aggregated rollups for a few accounts/dates, useful for testing
--    the statement/reporting endpoints without recomputing from transactions.
-- ---------------------------------------------------------------------
INSERT INTO daily_balance_snapshot (snapshot_id, account_id, snapshot_date, opening_balance, closing_balance, total_credits, total_debits) VALUES
  (7001, 1001, '2026-07-01', 5000.00, 7000.00, 2000.00,    0.00),
  (7002, 1001, '2026-07-02', 7000.00, 5500.00,    0.00, 1500.00),
  (7003, 1001, '2026-07-03', 5500.00, 4500.00,    0.00, 1000.00),
  (7004, 1003, '2026-07-03', 2000.00, 3000.00, 1000.00,    0.00),
  (7005, 1002, '2026-07-06', 3000.00, -5000.00,    0.00, 8000.00),
  (7006, 1006, '2026-07-05',    0.00,    0.00,    0.00,    0.00);

-- ---------------------------------------------------------------------
-- Quick sanity checks (run manually after seeding)
-- ---------------------------------------------------------------------
-- Verify balances reconcile with the latest transaction.balance_after per account:
--   SELECT a.account_id, a.balance AS current_balance, t.balance_after AS last_txn_balance
--   FROM account a
--   JOIN transaction t ON t.transaction_id = (
--       SELECT transaction_id FROM transaction
--       WHERE account_id = a.account_id AND status = 'SUCCESS'
--       ORDER BY created_at DESC LIMIT 1
--   );
--
-- Verify every TRANSFER_DEBIT has a matching TRANSFER_CREDIT with the same ref:
--   SELECT transaction_ref, COUNT(*) AS leg_count
--   FROM transaction
--   WHERE transaction_type IN ('TRANSFER_DEBIT','TRANSFER_CREDIT')
--   GROUP BY transaction_ref
--   HAVING leg_count <> 2;
