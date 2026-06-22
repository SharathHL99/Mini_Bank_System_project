# Mini Bank System Project - Architecture Document

# 1. Project Overview

Mini Bank System Project is a Spring Boot backend application that simulates core banking operations such as:

* Customer Management
* Account Management
* Transaction Management (Deposit, Withdrawal, Fund Transfer)
* Audit Logging
* Reporting and Statements

The system is designed using enterprise-level layered architecture principles to ensure scalability, maintainability, and reliability.



# 2. Architecture Style

The application follows a Layered Architecture pattern:

Client → Controller → Service → Repository → Database

This architecture separates responsibilities across layers and promotes clean code practices.



# 3. High-Level Architecture

```text
Client (Postman / Swagger UI)
        |
        v
REST Controller Layer
        |
        v
Service Layer (Business Logic)
        |
        v
Repository Layer (Spring Data JPA)
        |
        v
MySQL Database
```



# 4. Layer Responsibilities

## 4.1 Controller Layer

Responsibilities:

* Handles HTTP requests and responses
* Performs request validation
* Invokes service-layer operations
* Returns standardized API responses

Modules:

* CustomerController
* AccountController
* TransactionController
* ReportController



## 4.2 Service Layer

Responsibilities:

* Implements business logic
* Handles transaction management
* Performs validations
* Maintains audit records
* Coordinates repository operations

Modules:

* CustomerService
* AccountService
* TransactionService
* AuditService
* ReportService


## 4.3 Repository Layer

Responsibilities:

* Performs database operations
* Handles CRUD functionality
* Executes JPQL and Native SQL queries
* Interacts with MySQL through Spring Data JPA

Modules:

* CustomerRepository
* AccountRepository
* TransactionRepository
* AuditRepository



## 4.4 Database Layer

Responsibilities:

* Stores application data
* Maintains data consistency
* Supports transactional operations

Tables:

* customers
* accounts
* bank_transactions
* audit_logs



# 5. Module Flow

## Customer Flow

```text
Client → CustomerController → CustomerService → CustomerRepository → Database
```

## Account Flow

```text
Client → AccountController → AccountService → AccountRepository → Database
```

## Transaction Flow

```text
Client → TransactionController → TransactionService → Repository → Database
```



# 6. Transaction Flow (Detailed)

## Deposit / Withdrawal / Fund Transfer

1. Request is received by TransactionController.
2. Request is validated.
3. TransactionService processes the business logic.
4. Account validation is performed.
5. Balance validation is performed.
6. Account balances are updated.
7. Transaction history is recorded.
8. Audit log is generated.
9. Response is returned to the client.



# 7. Entity Relationship Overview

```text
Customer (1)
    |
    | One-to-Many
    v
Account (Many)

Account (1)
    |
    | One-to-Many
    v
BankTransaction (Many)

AuditLog (Independent Entity)
```



# 8. Design Principles Used

* Layered Architecture
* DTO Pattern
* Builder Pattern
* Dependency Injection
* Repository Pattern
* Global Exception Handling
* Jakarta Bean Validation
* SLF4J Logging
* Enum-Based Status Management
* ACID-Compliant Transaction Management



# 9. Database Design

## Customers

* customer_id (Primary Key)
* first_name
* last_name
* email
* mobile_number
* status

## Accounts

* account_id (Primary Key)
* account_number
* balance
* account_type
* status
* customer_id (Foreign Key)

## Bank Transactions

* transaction_id (Primary Key)
* transaction_reference
* transaction_type
* amount
* source_account
* destination_account
* transaction_date

## Audit Logs

* audit_id (Primary Key)
* action
* performed_by
* action_time



# 10. Transaction Management

All financial operations are managed using Spring's:

@Transactional

Benefits:

* Atomicity
* Consistency
* Isolation
* Durability (ACID)



# 11. Exception Handling

Centralized exception handling is implemented using:

@RestControllerAdvice

Handled Exceptions:

* ResourceNotFoundException
* InsufficientBalanceException
* IllegalArgumentException
* Validation Exceptions
* Generic Runtime Exceptions



# 12. Logging Strategy

Logging is implemented using SLF4J.

Log Levels:

* INFO – Application flow tracking
* DEBUG – Detailed debugging information
* ERROR – Exception and failure logging



# 13. Testing Strategy

Frameworks Used:

* JUnit 5
* Mockito

Coverage Includes:

* Service Layer Unit Tests
* Positive Scenarios
* Negative Scenarios
* Exception Validation
* Transaction Validation



# 14. API Documentation

Swagger/OpenAPI is integrated for API documentation.

Access URL:

http://localhost:8080/swagger-ui/index.html



# 15. Summary

The Mini Bank System Project demonstrates a real-world banking backend architecture using Spring Boot. The application follows industry-standard development practices including layered architecture, transaction safety, validation, centralized exception handling, logging, audit management, and automated testing.

This project satisfies the requirements for customer management, account management, transaction processing, audit logging, reporting, and enterprise-level software design.



Developed By:

Sharath H L
