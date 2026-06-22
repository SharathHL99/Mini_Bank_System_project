//Customers
INSERT INTO customers
(first_name,last_name,email,mobile_number,status)
VALUES
('Sharath','Sharu','sharath@gmail.com','9676789898','ACTIVE');

INSERT INTO customers
(first_name,last_name,email,mobile_number,status)
VALUES
('abhi','Reddy','abhi@gmail.com','9876546678','ACTIVE');

INSERT INTO customers
(first_name,last_name,email,mobile_number,status)
VALUES
('Yashwanth','Kumar','yashwanth@gmail.com','9876543565','ACTIVE');

INSERT INTO customers
(first_name,last_name,email,mobile_number,status)
VALUES
('Prajwal','praju','praju@gmail.com','9876543543','ACTIVE');

INSERT INTO customers
(first_name,last_name,email,mobile_number,status)
VALUES
('Akshay','Raj','Akshay@gmail.com','9876543214','ACTIVE');


//Accounts
INSERT INTO accounts
(account_number,balance,account_type,status,customer_id)
VALUES
('ACC100001',15000,'SAVINGS','ACTIVE',1);

INSERT INTO accounts
(account_number,balance,account_type,status,customer_id)
VALUES
('ACC100002',25000,'CURRENT','ACTIVE',2);

INSERT INTO accounts
(account_number,balance,account_type,status,customer_id)
VALUES
('ACC100003',10000,'SAVINGS','ACTIVE',3);

INSERT INTO accounts
(account_number,balance,account_type,status,customer_id)
VALUES
('ACC100004',30000,'CURRENT','ACTIVE',4);

INSERT INTO accounts
(account_number,balance,account_type,status,customer_id)
VALUES
('ACC100005',5000,'SAVINGS','ACTIVE',5);


//Transactions
INSERT INTO bank_transactions
(transaction_reference,transaction_type,amount,
source_account,destination_account,transaction_date)
VALUES
('TXN100001','DEPOSIT',10000,
'ACC100001',NULL,NOW());

INSERT INTO bank_transactions
(transaction_reference,transaction_type,amount,
source_account,destination_account,transaction_date)
VALUES
('TXN100002','WITHDRAWAL',2000,
'ACC100001',NULL,NOW());

INSERT INTO bank_transactions
(transaction_reference,transaction_type,amount,
source_account,destination_account,transaction_date)
VALUES
('TXN100003','TRANSFER',3000,
'ACC100001','ACC100002',NOW());

INSERT INTO bank_transactions
(transaction_reference,transaction_type,amount,
source_account,destination_account,transaction_date)
VALUES
('TXN100004','DEPOSIT',5000,
'ACC100003',NULL,NOW());

INSERT INTO bank_transactions
(transaction_reference,transaction_type,amount,
source_account,destination_account,transaction_date)
VALUES
('TXN100005','TRANSFER',1000,
'ACC100004','ACC100005',NOW());


//AuditLogs
INSERT INTO audit_logs
(action,performed_by,action_time)
VALUES
('CUSTOMER CREATED','SYSTEM',NOW());

INSERT INTO audit_logs
(action,performed_by,action_time)
VALUES
('ACCOUNT CREATED','SYSTEM',NOW());