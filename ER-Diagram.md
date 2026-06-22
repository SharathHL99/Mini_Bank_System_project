# Mini Bank System Project - ER Diagram

Customer (1) → Account (Many)  
Account (1) → BankTransaction (Many)

Customer:
- customer_id (PK)
- name
- email
- mobile_number
- status

Account:
- account_id (PK)
- account_number
- balance
- type
- customer_id (FK)

Transaction:
- transaction_id (PK)
- type
- amount
- date

AuditLog:
- audit_id (PK)
- action
- time

