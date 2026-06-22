Mini Bank System Project

Overview Banking Mini Core System is an enterprise-level Spring Boot backend application that simulates real-world banking operations including customer management, account management, fund transfers, transaction tracking, audit logging, and reporting.

The application is designed using scalable layered architecture and follows industry-standard development practices such as DTO pattern, validation, exception handling, centralized logging, Swagger documentation, and ACID-compliant transaction management.

Objectives This project demonstrates the implementation of a simplified Core Banking System with:

Customer Management Account Management Transaction Processing Audit Logging Reporting Dashboard Exception Handling API Documentation Unit Testing Features Customer Management Create Customer View Customer Details View All Customers Activate Customer Block Customer Account Management Create Savings Account Create Current Account View Account Details View All Accounts Activate Account Deactivate Account Close Account Balance Tracking Transaction Management Deposit Money Withdraw Money Transfer Funds Prevent Overdraft Prevent Invalid Transactions Account Statement Generation Reporting Dashboard Summary Total Bank Balance Daily Transaction Summary Audit Logging Customer Activity Logs Account Activity Logs Transaction Activity Logs Statement Access Logs Technology Stack | Technology | Version | |------------|----------| | Java | 21 | | Spring Boot | 3.x | | Spring Data JPA | Latest | | MySQL | 8.x | | Maven | Latest | | Lombok | Latest | | Swagger OpenAPI | Latest | | JUnit | 5 | | Mockito | Latest | | SLF4J Logging | Latest |

Project Architecture The project follows a layered architecture:

Controller Layer ↓ Service Layer ↓ Repository Layer ↓ Database Package Structure src/main/java/com/banking

├── config ├── controller ├── dto ├── enums ├── exception ├── model ├── repository ├── service ├── util └── BankingApplication Database Schema Customer | Column | |----------| | customer_id | | first_name | | last_name | | email | | mobile_number | | status |

Account | Column | |----------| | account_id | | account_number | | balance | | account_type | | status | | customer_id |

Bank Transaction | Column | |----------| | transaction_id | | transaction_reference | | transaction_type | | amount | | source_account | | destination_account | | transaction_date | | remarks |

Audit Log | Column | |----------| | audit_id | | action | | performed_by | | action_time |

Entity Relationship Diagram Customer (1) -------- (Many) Account

Account (1) -------- (Many) BankTransaction

AuditLog -------- Independent Entity

ER Diagram ER Diagram

API Endpoints Customer APIs | Method | Endpoint | |----------|----------| | POST | /api/customers | | GET | /api/customers/{id} | | GET | /api/customers | | PATCH | /api/customers/{id}/activate | | PATCH | /api/customers/{id}/block |

Account APIs | Method | Endpoint | |----------|----------| | POST | /api/accounts | | GET | /api/accounts/{id} | | GET | /api/accounts | | PATCH | /api/accounts/{id}/activate | | PATCH | /api/accounts/{id}/deactivate | | PATCH | /api/accounts/{id}/close |

Transaction APIs | Method | Endpoint | |----------|----------| | POST | /api/transactions/deposit | | POST | /api/transactions/withdraw | | POST | /api/transactions/transfer | | GET | /api/transactions/statement/{accountNumber} |

Report APIs | Method | Endpoint | |----------|----------| | GET | /api/reports/dashboard | | GET | /api/reports/total-balance | | GET | /api/reports/today-transactions |

Sample Request Create Customer Request { "firstName": "Rahul", "lastName": "Sharma", "email": "rahul@gmail.com", "mobileNumber": "9876543210" } Response { "success": true, "message": "Customer created successfully", "data": { "customerId": 1, "firstName": "Rahul", "lastName": "Sharma", "email": "rahul@gmail.com", "mobileNumber": "9876543210", "status": "ACTIVE" } } Validation Implemented using:

@NotNull @NotBlank @Email @DecimalMin @Min Exception Handling Custom Exceptions:

ResourceNotFoundException InsufficientBalanceException IllegalArgumentException Global Handling:

@RestControllerAdvice Standard Error Response DTO Example:

{ "message": "Insufficient balance", "status": 400, "timestamp": "2026-06-20T12:30:00" } Logging Implemented using SLF4J.

Logged Events:

Customer Creation Account Creation Deposits Withdrawals Transfers Statement Access Error Events Transaction Management Implemented using:

@Transactional Supports:

Atomicity Consistency Isolation Durability Thread-safe fund transfer is implemented using pessimistic locking.

Swagger Documentation Swagger UI:

http://localhost:8080/swagger-ui/index.html OpenAPI JSON:

http://localhost:8080/v3/api-docs Unit Testing Frameworks:

JUnit 5 Mockito Test Classes:

CustomerServiceTest AccountServiceTest TransactionServiceTest Database Scripts schema.sql data.sql Contains:

Sample Customers Sample Accounts Sample Transactions Build & Run Build mvn clean install Run Application mvn spring-boot:run OR

Run:

BankingApplication.java Configuration Example:

spring: application: name: BankingMiniCoreSystem

server: port: 8080 Do not commit real database passwords to GitHub repositories.

Enterprise Concepts Implemented Layered Architecture DTO Pattern Builder Pattern Repository Pattern Service Layer Pattern Exception Handling Validation Logging Swagger Documentation JPQL Queries Native Queries Audit Logging ACID Transactions Thread-Safe Fund Transfer Enum Usage Unit Testing Deliverables Covered ✅ Complete Spring Boot Source Code

✅ REST API Documentation (Swagger)

✅ Database Schema Design

✅ Entity Relationship Diagram

✅ Service Layer Unit Tests

✅ Sample Test Data Scripts

✅ README Documentation

✅ Architecture & Module Flow Documentation

Author Sharath H L
