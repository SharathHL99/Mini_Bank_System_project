-- ============================================================
-- Sample test data for Banking Mini Core System
-- Place this file at: src/main/resources/data.sql
--
-- IMPORTANT: add these two properties to application.properties
-- (or .yml equivalent) so Spring Boot runs this AFTER Hibernate
-- creates the tables, instead of before:
--
--   spring.jpa.defer-datasource-initialization=true
--   spring.sql.init.mode=always
--
-- Without the first property, this script runs before the
-- "customers"/"accounts"/etc. tables exist and every insert fails.
-- ============================================================

-- ---------- CUSTOMERS ----------
INSERT INTO customers (customer_id, first_name, last_name, email, phone, created_at, updated_at) VALUES
(1, 'Asha',  'Rao',   'asha.rao@example.com',   '9000000001', '2026-06-01 09:00:00', '2026-06-01 09:00:00'),
(2, 'Ravi',  'Kumar', 'ravi.kumar@example.com', '9000000002', '2026-06-02 10:15:00', '2026-06-02 10:15:00'),
(3, 'Meena', 'Iyer',  'meena.iyer@example.com', '9000000003', '2026-06-03 11:30:00', '2026-06-03 11:30:00');

-- ---------- ACCOUNTS ----------
-- Asha has two accounts (savings + current), Ravi and Meena have one savings account each
INSERT INTO accounts (account_id, customer_id, account_number, account_type, balance, status, version, created_at, updated_at) VALUES
(1, 1, 'AC1001SAVE', 'SAVINGS', 4500.00,  'ACTIVE', 3, '2026-06-01 09:05:00', '2026-06-10 16:20:00'),
(2, 1, 'AC1002CURR', 'CURRENT', 10000.00, 'ACTIVE', 0, '2026-06-01 09:10:00', '2026-06-01 09:10:00'),
(3, 2, 'AC2001SAVE', 'SAVINGS', 5000.00,  'ACTIVE', 1, '2026-06-02 10:20:00', '2026-06-08 14:00:00'),
(4, 3, 'AC3001SAVE', 'SAVINGS', 5000.00,  'ACTIVE', 1, '2026-06-03 11:35:00', '2026-06-10 16:20:00');

-- ---------- TRANSACTIONS ----------
-- Account 1 (Asha, savings): opening deposit -> top-up deposit -> withdrawal -> transfer out to account 4
-- Account 2 (Asha, current): opening deposit only
-- Account 3 (Ravi, savings): opening deposit -> withdrawal
-- Account 4 (Meena, savings): opening deposit -> transfer in from account 1
INSERT INTO transactions (transaction_id, account_id, transaction_type, amount, balance_after, transfer_ref_id, status, created_at, updated_at) VALUES
(1, 1, 'DEPOSIT',      5000.00, 5000.00, NULL,                                   'SUCCESS', '2026-06-01 09:05:00', '2026-06-01 09:05:00'),
(2, 1, 'DEPOSIT',      2000.00, 7000.00, NULL,                                   'SUCCESS', '2026-06-05 12:00:00', '2026-06-05 12:00:00'),
(3, 1, 'WITHDRAWAL',   1500.00, 5500.00, NULL,                                   'SUCCESS', '2026-06-07 15:30:00', '2026-06-07 15:30:00'),
(4, 2, 'DEPOSIT',     10000.00, 10000.00, NULL,                                  'SUCCESS', '2026-06-01 09:10:00', '2026-06-01 09:10:00'),
(5, 3, 'DEPOSIT',      8000.00, 8000.00, NULL,                                   'SUCCESS', '2026-06-02 10:20:00', '2026-06-02 10:20:00'),
(6, 3, 'WITHDRAWAL',   3000.00, 5000.00, NULL,                                   'SUCCESS', '2026-06-08 14:00:00', '2026-06-08 14:00:00'),
(7, 4, 'DEPOSIT',      4000.00, 4000.00, NULL,                                   'SUCCESS', '2026-06-03 11:35:00', '2026-06-03 11:35:00'),
(8, 1, 'TRANSFER_OUT', 1000.00, 4500.00, 'TXN-REF-seed-0001-0001-0001-0001-001', 'SUCCESS', '2026-06-10 16:20:00', '2026-06-10 16:20:00'),
(9, 4, 'TRANSFER_IN',  1000.00, 5000.00, 'TXN-REF-seed-0001-0001-0001-0001-001', 'SUCCESS', '2026-06-10 16:20:00', '2026-06-10 16:20:00');

-- ---------- AUDIT LOGS ----------
-- One audit entry per transaction above, mirroring what TransactionServiceImpl.logAudit() writes
INSERT INTO audit_logs (audit_id, transaction_id, action, performed_by, details, created_at, updated_at) VALUES
(1, 1, 'DEPOSIT_PROCESSED',      'system', 'Transaction 1 of type DEPOSIT',      '2026-06-01 09:05:00', '2026-06-01 09:05:00'),
(2, 2, 'DEPOSIT_PROCESSED',      'system', 'Transaction 2 of type DEPOSIT',      '2026-06-05 12:00:00', '2026-06-05 12:00:00'),
(3, 3, 'WITHDRAWAL_PROCESSED',   'system', 'Transaction 3 of type WITHDRAWAL',   '2026-06-07 15:30:00', '2026-06-07 15:30:00'),
(4, 4, 'DEPOSIT_PROCESSED',      'system', 'Transaction 4 of type DEPOSIT',      '2026-06-01 09:10:00', '2026-06-01 09:10:00'),
(5, 5, 'DEPOSIT_PROCESSED',      'system', 'Transaction 5 of type DEPOSIT',      '2026-06-02 10:20:00', '2026-06-02 10:20:00'),
(6, 6, 'WITHDRAWAL_PROCESSED',   'system', 'Transaction 6 of type WITHDRAWAL',   '2026-06-08 14:00:00', '2026-06-08 14:00:00'),
(7, 7, 'DEPOSIT_PROCESSED',      'system', 'Transaction 7 of type DEPOSIT',      '2026-06-03 11:35:00', '2026-06-03 11:35:00'),
(8, 8, 'TRANSFER_OUT_PROCESSED', 'system', 'Transaction 8 of type TRANSFER_OUT', '2026-06-10 16:20:00', '2026-06-10 16:20:00'),
(9, 9, 'TRANSFER_IN_PROCESSED',  'system', 'Transaction 9 of type TRANSFER_IN',  '2026-06-10 16:20:00', '2026-06-10 16:20:00');

-- ---------- BUMP IDENTITY SEQUENCES ----------
-- So the next customer/account/transaction/audit_log created through the
-- running app doesn't collide with the seeded IDs above (H2-specific syntax).
ALTER TABLE customers    ALTER COLUMN customer_id    RESTART WITH 4;
ALTER TABLE accounts     ALTER COLUMN account_id     RESTART WITH 5;
ALTER TABLE transactions ALTER COLUMN transaction_id RESTART WITH 10;
ALTER TABLE audit_logs   ALTER COLUMN audit_id       RESTART WITH 10;