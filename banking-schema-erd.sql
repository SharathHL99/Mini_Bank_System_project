-- =====================================================================
-- Banking Mini Core System - Database Schema & Entity Relationship Design
-- Target: MySQL 8.x (adjust AUTO_INCREMENT / types if using Postgres/H2)
-- =====================================================================
--
-- ---------------------------------------------------------------------
-- ENTITY RELATIONSHIP DIAGRAM (textual / Mermaid ER notation)
-- ---------------------------------------------------------------------
-- Paste the block below into https://mermaid.live to render visually.
--
-- erDiagram
--     CUSTOMER ||--o{ ACCOUNT : "owns"
--     ACCOUNT  ||--o{ TRANSACTION : "has"
--     ACCOUNT  ||--o{ AUDIT_LOG : "generates"
--     CUSTOMER ||--o{ AUDIT_LOG : "generates"
--     TRANSACTION }o--|| ACCOUNT : "from_account (nullable for deposit/withdraw)"
--     TRANSACTION }o--|| ACCOUNT : "to_account (nullable for deposit/withdraw)"
--     ACCOUNT   ||--o{ DAILY_BALANCE_SNAPSHOT : "rolled up into"
--
--     CUSTOMER {
--         BIGINT customer_id PK
--         VARCHAR first_name
--         VARCHAR last_name
--         VARCHAR email UK
--         VARCHAR phone
--         VARCHAR address
--         DATE date_of_birth
--         VARCHAR kyc_document_number
--         VARCHAR status
--         TIMESTAMP created_at
--         TIMESTAMP updated_at
--     }
--     ACCOUNT {
--         BIGINT account_id PK
--         BIGINT customer_id FK
--         VARCHAR account_number UK
--         VARCHAR account_type
--         DECIMAL balance
--         DECIMAL overdraft_limit
--         VARCHAR currency
--         VARCHAR status
--         BIGINT version
--         TIMESTAMP created_at
--         TIMESTAMP updated_at
--     }
--     TRANSACTION {
--         BIGINT transaction_id PK
--         VARCHAR transaction_ref UK
--         BIGINT account_id FK
--         BIGINT related_account_id FK
--         VARCHAR transaction_type
--         DECIMAL amount
--         DECIMAL balance_after
--         VARCHAR status
--         VARCHAR description
--         TIMESTAMP created_at
--     }
--     AUDIT_LOG {
--         BIGINT audit_id PK
--         BIGINT customer_id FK
--         BIGINT account_id FK
--         BIGINT transaction_id FK
--         VARCHAR action
--         VARCHAR performed_by
--         VARCHAR details
--         VARCHAR ip_address
--         TIMESTAMP created_at
--     }
--     DAILY_BALANCE_SNAPSHOT {
--         BIGINT snapshot_id PK
--         BIGINT account_id FK
--         DATE snapshot_date
--         DECIMAL opening_balance
--         DECIMAL closing_balance
--         DECIMAL total_credits
--         DECIMAL total_debits
--     }
--
-- Relationship summary:
--  - One CUSTOMER can own many ACCOUNTs (1:N)
--  - One ACCOUNT can have many TRANSACTIONs (1:N)
--  - A TRANSACTION references one primary account always; for TRANSFER type
--    it additionally references a related_account_id (the counterpart account)
--  - One ACCOUNT/CUSTOMER can generate many AUDIT_LOG entries (1:N each),
--    audit_log.transaction_id is nullable (some audit events are not
--    transaction-driven, e.g. profile updates, login attempts)
--  - One ACCOUNT rolls up into many DAILY_BALANCE_SNAPSHOT rows (1:N),
--    one row per calendar day, used to serve fast statement/report queries
-- ---------------------------------------------------------------------

DROP DATABASE IF EXISTS banking_mini_core;
CREATE DATABASE banking_mini_core;
USE banking_mini_core;

-- ---------------------------------------------------------------------
-- 1. CUSTOMER TABLE
-- --------------------------------------------------------------------
CREATE TABLE customer (
    customer_id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name            VARCHAR(100)    NOT NULL,
    last_name             VARCHAR(100)    NOT NULL,
    email                 VARCHAR(150)    NOT NULL UNIQUE,
    phone                 VARCHAR(20)     NOT NULL,
    address                VARCHAR(500),
    date_of_birth         DATE            NOT NULL,
    kyc_document_number   VARCHAR(50)     NOT NULL UNIQUE,
    status                VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE',   -- ACTIVE, INACTIVE, SUSPENDED
    created_at            TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------------
-- 2. ACCOUNT TABLE
-- ---------------------------------------------------------------------
CREATE TABLE account (
    account_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id      BIGINT           NOT NULL,
    account_number   VARCHAR(20)      NOT NULL UNIQUE,
    account_type     VARCHAR(20)      NOT NULL,                 -- SAVINGS, CURRENT
    balance          DECIMAL(18,2)    NOT NULL DEFAULT 0.00,
    overdraft_limit  DECIMAL(18,2)    NOT NULL DEFAULT 0.00,    -- 0 for SAVINGS; >0 allowed for CURRENT
    currency         VARCHAR(3)       NOT NULL DEFAULT 'INR',
    status           VARCHAR(20)      NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, DORMANT, CLOSED
    version          BIGINT           NOT NULL DEFAULT 0,        -- optimistic locking for concurrent balance updates
    created_at       TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_account_customer FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE RESTRICT,
    CONSTRAINT chk_balance_within_overdraft CHECK (balance >= -overdraft_limit)
);

-- ---------------------------------------------------------------------
-- 3. TRANSACTION TABLE
--    Single table for DEPOSIT, WITHDRAWAL, and TRANSFER (debit + credit
--    legs of a transfer are represented as two linked rows sharing the
--    same transaction_ref, distinguished by transaction_type).
-- ---------------------------------------------------------------------
CREATE TABLE transaction (
    transaction_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_ref      VARCHAR(40)      NOT NULL,           -- groups the two legs of a transfer; unique per deposit/withdrawal
    account_id           BIGINT           NOT NULL,           -- account this row's balance change applies to
    related_account_id   BIGINT           NULL,               -- counterpart account for TRANSFER_DEBIT / TRANSFER_CREDIT
    transaction_type     VARCHAR(20)      NOT NULL,           -- DEPOSIT, WITHDRAWAL, TRANSFER_DEBIT, TRANSFER_CREDIT
    amount                DECIMAL(18,2)    NOT NULL CHECK (amount > 0),
    balance_after         DECIMAL(18,2)    NOT NULL,
    status                VARCHAR(20)      NOT NULL DEFAULT 'SUCCESS', -- SUCCESS, FAILED, REJECTED
    description           VARCHAR(500),
    created_at            TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_txn_account         FOREIGN KEY (account_id)         REFERENCES account(account_id) ON DELETE RESTRICT,
    CONSTRAINT fk_txn_related_account FOREIGN KEY (related_account_id) REFERENCES account(account_id) ON DELETE RESTRICT
);

-- ---------------------------------------------------------------------
-- 4. AUDIT_LOG TABLE
--    Immutable trail of every state-changing action, including rejected
--    attempts (e.g. overdraft rejection), profile updates, and status changes.
-- ---------------------------------------------------------------------
CREATE TABLE audit_log (
    audit_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id     BIGINT           NULL,
    account_id      BIGINT           NULL,
    transaction_id  BIGINT           NULL,
    action           VARCHAR(60)      NOT NULL,   -- e.g. CUSTOMER_CREATED, ACCOUNT_OPENED, WITHDRAWAL_REJECTED, TRANSFER_SUCCESS
    performed_by     VARCHAR(60)      NOT NULL DEFAULT 'SYSTEM',
    details          VARCHAR(1000),
    ip_address       VARCHAR(45),
    created_at       TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_customer    FOREIGN KEY (customer_id)    REFERENCES customer(customer_id)       ON DELETE SET NULL,
    CONSTRAINT fk_audit_account     FOREIGN KEY (account_id)     REFERENCES account(account_id)         ON DELETE SET NULL,
    CONSTRAINT fk_audit_transaction FOREIGN KEY (transaction_id) REFERENCES transaction(transaction_id) ON DELETE SET NULL
);

-- ---------------------------------------------------------------------
-- 5. DAILY_BALANCE_SNAPSHOT TABLE
--    Pre-aggregated per-day rollup to serve fast statement/reporting
--    queries without scanning the full transaction table each time.
-- ---------------------------------------------------------------------
CREATE TABLE daily_balance_snapshot (
    snapshot_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id       BIGINT           NOT NULL,
    snapshot_date    DATE             NOT NULL,
    opening_balance  DECIMAL(18,2)    NOT NULL,
    closing_balance  DECIMAL(18,2)    NOT NULL,
    total_credits    DECIMAL(18,2)    NOT NULL DEFAULT 0.00,
    total_debits     DECIMAL(18,2)    NOT NULL DEFAULT 0.00,
    created_at       TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_snapshot_account FOREIGN KEY (account_id) REFERENCES account(account_id) ON DELETE CASCADE,
    UNIQUE KEY uq_account_date (account_id, snapshot_date)
);

-- ---------------------------------------------------------------------
-- Indexes for common query patterns
-- ---------------------------------------------------------------------
CREATE INDEX idx_account_customer_id         ON account(customer_id);
CREATE INDEX idx_account_status              ON account(status);
CREATE INDEX idx_transaction_account_id      ON transaction(account_id);
CREATE INDEX idx_transaction_related_account ON transaction(related_account_id);
CREATE INDEX idx_transaction_ref             ON transaction(transaction_ref);
CREATE INDEX idx_transaction_created_at      ON transaction(created_at);
CREATE INDEX idx_audit_log_customer_id       ON audit_log(customer_id);
CREATE INDEX idx_audit_log_account_id        ON audit_log(account_id);
CREATE INDEX idx_audit_log_created_at        ON audit_log(created_at);
CREATE INDEX idx_snapshot_account_date       ON daily_balance_snapshot(account_id, snapshot_date);

-- ---------------------------------------------------------------------
-- Sample seed data (optional - useful for Postman testing)
-- ---------------------------------------------------------------------
INSERT INTO customer (first_name, last_name, email, phone, address, date_of_birth, kyc_document_number) VALUES
  ('Alice', 'Sharma', 'alice.sharma@example.com', '9876543210', 'MG Road, Bengaluru', '1995-04-12', 'ABCDE1234F'),
  ('Bob', 'Verma', 'bob.verma@example.com', '9876500000', 'Indiranagar, Bengaluru', '1990-08-25', 'FGHIJ5678K');

INSERT INTO account (customer_id, account_number, account_type, balance, overdraft_limit, currency) VALUES
  (1, 'BNK0000001001', 'SAVINGS', 5000.00, 0.00, 'INR'),
  (1, 'BNK0000001002', 'CURRENT', 3000.00, 10000.00, 'INR'),
  (2, 'BNK0000001003', 'SAVINGS', 2000.00, 0.00, 'INR');
