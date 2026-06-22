# Banking Mini Core System - API Documentation

## Base URL

http://localhost:8080



# Customer APIs

## Create Customer

**POST** `/api/customers`

### Request

```json
{
  "firstName": "John",
  "lastName": "David",
  "email": "john@gmail.com",
  "mobileNumber": "9876543210"
}
```

### Response

```json
{
  "success": true,
  "message": "Customer created successfully",
  "data": {
    "customerId": 1,
    "firstName": "John",
    "lastName": "David",
    "email": "john@gmail.com",
    "mobileNumber": "9876543210",
    "status": "ACTIVE"
  }
}
```



## Get Customer By Id

**GET** `/api/customers/{customerId}`

Example:

```http
GET /api/customers/1
```



## Get All Customers

**GET** `/api/customers`



## Block Customer

**PATCH** `/api/customers/{customerId}/block`


## Activate Customer

**PATCH** `/api/customers/{customerId}/activate`


# Account APIs

## Create Account

**POST** `/api/accounts`

### Request

```json
{
  "customerId": 1,
  "accountType": "SAVINGS",
  "initialBalance": 10000
}
```



## Get Account By Id

**GET** `/api/accounts/{accountId}`



## Get All Accounts

**GET** `/api/accounts`



## Deactivate Account

**PATCH** `/api/accounts/{accountId}/deactivate`

---

## Activate Account

**PATCH** `/api/accounts/{accountId}/activate`



# Transaction APIs

## Deposit

**POST** `/api/transactions/deposit`

### Request

```json
{
  "accountId": 1,
  "amount": 5000
}
```



## Withdraw

**POST** `/api/transactions/withdraw`

### Request

```json
{
  "accountId": 1,
  "amount": 1000
}
```



## Transfer Funds

**POST** `/api/transactions/transfer`

### Request

```json
{
  "sourceAccountId": 1,
  "destinationAccountId": 2,
  "amount": 2000
}
```


## Account Statement

**GET** `/api/transactions/statement/{accountNumber}`

Example:

```http
GET /api/transactions/statement/ACC100001
```



# Reporting APIs

## Dashboard Summary

**GET** `/api/reports/dashboard`



## Total Bank Balance

**GET** `/api/reports/total-balance`



# Error Responses

## Resource Not Found

```json
{
  "message": "Account not found",
  "status": 404
}
```

## Insufficient Balance

```json
{
  "message": "Insufficient balance",
  "status": 400
}
```

## Invalid Amount

```json
{
  "message": "Amount must be greater than zero",
  "status": 400
}
```

## Account Inactive

```json
{
  "message": "Account is inactive",
  "status": 400
}
```

---

# Swagger Documentation

Access Swagger UI:

http://localhost:8080/swagger-ui/index.html



# Technology Stack

* Java 21
* Spring Boot 3
* Spring Data JPA
* MySQL
* Lombok
* Swagger OpenAPI
* JUnit 5
* Mockito
* Maven



# Security & Validation Features

* ACID-compliant transactions
* Thread-safe fund transfer using pessimistic locking
* Overdraft prevention
* Account status validation
* Audit logging
* Centralized exception handling
* Transaction history tracking
