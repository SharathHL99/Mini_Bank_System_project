# Banking Mini Core System — Architecture & Module Flow

## 1. High-Level Architecture (Layered)

```
┌─────────────────────────────────────────────────────────────────┐
│                         CLIENT LAYER                            │
│        (Postman / Swagger UI / Frontend / External Systems)      │
└───────────────────────────────┬───────────────────────────────────┘
                                 │ HTTPS (JSON over REST)
┌───────────────────────────────▼───────────────────────────────────┐
│                       CONTROLLER LAYER                           │
│  CustomerController | AccountController | TransactionController │
│  AuditController     | ReportController                          │
│  - Request validation (@Valid, Bean Validation)                  │
│  - DTO <-> internal mapping                                       │
│  - HTTP status code mapping                                        │
└───────────────────────────────┬───────────────────────────────────┘
                                 │
┌───────────────────────────────▼───────────────────────────────────┐
│                         SERVICE LAYER                             │
│  CustomerService | AccountService | TransactionService            │
│  AuditService     | ReportService                                 │
│  - Business rules (overdraft check, KYC validation, etc.)          │
│  - @Transactional boundaries                                      │
│  - Orchestrates repositories + emits audit events                 │
└───────────────────────────────┬───────────────────────────────────┘
                                 │
┌───────────────────────────────▼───────────────────────────────────┐
│                       REPOSITORY LAYER                            │
│  CustomerRepository | AccountRepository | TransactionRepository   │
│  AuditLogRepository  | DailyBalanceSnapshotRepository              │
│  - Spring Data JPA interfaces                                     │
│  - Custom @Query / @Lock for pessimistic row locking               │
└───────────────────────────────┬───────────────────────────────────┘
                                 │
┌───────────────────────────────▼───────────────────────────────────┐
│                          DATABASE LAYER                            │
│              MySQL 8.x (ACID-compliant transactions)              │
│  customer | account | transaction | audit_log | daily_balance_... │
└─────────────────────────────────────────────────────────────────────┘

Cross-cutting concerns (applied across all layers):
  - GlobalExceptionHandler (@ControllerAdvice)
  - Centralized logging (SLF4J + Logback, correlation/request IDs)
  - Bean Validation (jakarta.validation)
  - Security/auth filter (if enabled)
```

---

## 2. Module Breakdown

| Module | Responsibility | Key Entities |
|---|---|---|
| **Customer Management** | Create/update/deactivate customers, link accounts | `Customer` |
| **Account Management** | Open/close accounts, track balance, enforce overdraft rules | `Account` |
| **Transaction Management** | Deposit, withdrawal, fund transfer, transaction history | `Transaction` |
| **Audit & Logs** | Immutable record of every state-changing action, including rejections | `AuditLog` |
| **Reporting** | Daily statements, balance summaries, exports | `DailyBalanceSnapshot`, derived reports |

Each module follows the same internal shape: `Controller → Service → Repository → Entity`, keeping modules loosely coupled — e.g. `TransactionService` depends on `AccountService` (via interface) rather than reaching into `AccountRepository` directly, so account-level invariants stay enforced in one place.

---

## 3. Module Flow — Deposit

```
Client
  │  POST /accounts/{id}... deposit request
  ▼
TransactionController.deposit()
  │  validates request DTO (amount > 0)
  ▼
TransactionService.deposit(accountId, amount)
  │  @Transactional
  │  1. Load Account (SELECT ... FOR UPDATE / pessimistic lock)
  │  2. Verify account.status == ACTIVE
  │  3. account.balance += amount
  │  4. Save Account (optimistic version check as secondary guard)
  │  5. Persist Transaction row (type=DEPOSIT, status=SUCCESS)
  │  6. AuditService.log(ACCOUNT_ID, "DEPOSIT_SUCCESS", ...)
  ▼
Return 201 Created + transaction summary
```

---

## 4. Module Flow — Withdrawal (with Overdraft Prevention)

```
Client
  │  POST /transactions/withdraw
  ▼
TransactionController.withdraw()
  ▼
TransactionService.withdraw(accountId, amount)
  │  @Transactional
  │  1. Load Account (locked)
  │  2. Verify account.status == ACTIVE
  │  3. Check: (account.balance - amount) >= -account.overdraftLimit
  │        │
  │        ├─ FAIL → persist Transaction(status=FAILED),
  │        │         AuditService.log("WITHDRAWAL_REJECTED", ...)
  │        │         throw InsufficientFundsException
  │        │         → GlobalExceptionHandler → 422 UNPROCESSABLE_ENTITY
  │        │
  │        └─ PASS → account.balance -= amount
  │                  persist Transaction(status=SUCCESS)
  │                  AuditService.log("WITHDRAWAL_SUCCESS", ...)
  ▼
Return 201 Created (success) or 422 error body (failure)
```

---

## 5. Module Flow — Fund Transfer (Thread-Safe, ACID)

Fund transfer is the highest-risk operation: it must be atomic (both legs succeed or neither does), and safe under concurrent transfers touching the same accounts from different threads/requests.

```
Client
  │  POST /transactions/transfer  {fromAccountId, toAccountId, amount}
  ▼
TransactionController.transfer()
  ▼
TransactionService.transfer(fromId, toId, amount)
  │  @Transactional
  │
  │  STEP 1 — Deadlock prevention via consistent lock ordering:
  │    lockOrder = sort(fromId, toId)   // always lock lower ID first
  │
  │  STEP 2 — Acquire locks in that fixed order:
  │    accountA = accountRepository.findByIdForUpdate(lockOrder[0])
  │    accountB = accountRepository.findByIdForUpdate(lockOrder[1])
  │
  │  STEP 3 — Validate:
  │    - both accounts ACTIVE
  │    - fromAccount.balance - amount >= -fromAccount.overdraftLimit
  │        │
  │        ├─ FAIL → throw InsufficientFundsException / AccountNotActiveException
  │        │         (transaction rolls back — no partial state persisted)
  │        │
  │        └─ PASS ↓
  │
  │  STEP 4 — Apply both legs atomically:
  │    fromAccount.balance -= amount
  │    toAccount.balance   += amount
  │    save both accounts
  │
  │  STEP 5 — Persist two linked Transaction rows sharing one transaction_ref:
  │    Transaction(account=from, type=TRANSFER_DEBIT,  ref=X)
  │    Transaction(account=to,   type=TRANSFER_CREDIT, ref=X)
  │
  │  STEP 6 — Audit both sides:
  │    AuditService.log(fromAccount, "TRANSFER_SUCCESS", ...)
  │    AuditService.log(toAccount,   "TRANSFER_SUCCESS", ...)
  │
  │  Any exception at any step → @Transactional rollback:
  │    no balance change, no transaction rows, only a REJECTED audit entry
  │    is written in a *separate, non-rolled-back* logging call (e.g. via
  │    TransactionTemplate.REQUIRES_NEW) so failures are still auditable.
  ▼
Return 201 Created (success) or error body (4xx/5xx)
```

**Thread-safety notes:**
- Pessimistic row locks (`SELECT ... FOR UPDATE`) prevent two concurrent transfers from reading stale balances for the same account.
- Locking accounts in a fixed, sorted order (by ID) on every transfer prevents circular-wait deadlocks when two transfers happen to involve the same pair of accounts in opposite directions.
- The `version` column on `account` provides an optimistic-locking safety net (`@Version` in JPA) in case pessimistic locking is bypassed by a raw query path.

---

## 6. Exception Handling Flow

```
Any Controller/Service throws a domain exception
  │
  ▼
GlobalExceptionHandler (@ControllerAdvice)
  │
  ├─ InsufficientFundsException        → 422 UNPROCESSABLE_ENTITY
  ├─ AccountNotFoundException          → 404 NOT_FOUND
  ├─ CustomerNotFoundException         → 404 NOT_FOUND
  ├─ AccountNotActiveException         → 409 CONFLICT
  ├─ InvalidTransactionAmountException → 400 BAD_REQUEST
  ├─ MethodArgumentNotValidException   → 400 BAD_REQUEST (field-level errors)
  ├─ DataIntegrityViolationException   → 409 CONFLICT (e.g. duplicate email)
  └─ Exception (fallback)              → 500 INTERNAL_SERVER_ERROR
  │
  ▼
Standard error response body:
  {
    "timestamp": "...",
    "status": <code>,
    "error": "<ERROR_CODE>",
    "message": "<human-readable message>",
    "path": "<request path>"
  }
  │
  ▼
Logged via centralized logger (SLF4J) with correlation ID,
  stack trace at ERROR level for 5xx, WARN level for 4xx.
```

---

## 7. Reporting Flow (Daily Statement / Balance Summary)

```
Client → ReportController.getStatement(accountId, fromDate, toDate)
  ▼
ReportService.generateStatement()
  │  1. Check for existing DailyBalanceSnapshot rows in range
  │       - If present for a date → use snapshot (fast path)
  │       - If missing for a date → aggregate on-the-fly from
  │         Transaction table for that date (fallback path)
  │  2. Merge into a chronological statement: opening balance,
  │     list of transactions, closing balance per day
  │  3. (Optional) Render to PDF/CSV via export endpoint
  ▼
Return statement DTO / file stream to client
```

A nightly scheduled job (`@Scheduled`) rolls up the previous day's transactions per account into `daily_balance_snapshot`, keeping statement queries fast even as transaction volume grows.

---

## 8. Request Lifecycle — End-to-End Example (Transfer)

```
1. Client sends POST /api/v1/transactions/transfer
2. Controller validates payload shape (Bean Validation)
3. Service opens a DB transaction
4. Service locks both accounts in sorted ID order
5. Service validates business rules (active status, overdraft)
6. Service updates balances + persists 2 transaction rows
7. Service writes audit log entries
8. DB transaction commits (all-or-nothing)
9. Controller maps result to TransferResponse DTO
10. Client receives 201 Created with both accounts' new balances
```

---

## 9. Non-Functional Concerns Mapped to Design Choices

| Requirement | Design Choice |
|---|---|
| ACID-compliant transactions | `@Transactional` service methods; single DB transaction per operation |
| Thread-safe fund transfer | Pessimistic locking + fixed lock ordering + optimistic `version` fallback |
| Proper exception handling | `@ControllerAdvice` global handler, custom domain exceptions, consistent error DTO |
| Centralized logging | SLF4J + Logback, correlation ID per request (MDC), structured log format |
| Scalable layered architecture | Strict Controller → Service → Repository separation; modules interact only through service interfaces, never cross-module repositories |

---

## 10. Package Structure (Reference)

```
com.bank.core
 ├── customer
 │    ├── controller / service / repository / entity / dto
 ├── account
 │    ├── controller / service / repository / entity / dto
 ├── transaction
 │    ├── controller / service / repository / entity / dto
 ├── audit
 │    ├── service / repository / entity
 ├── report
 │    ├── controller / service / dto
 ├── common
 │    ├── exception (GlobalExceptionHandler, domain exceptions)
 │    ├── config (DataSource, Swagger/OpenAPI, logging filter)
 │    └── util
 └── BankingCoreApplication.java
```
