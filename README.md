# 🏦 Banking Management System

A simple console-based Banking Management System built using Java, JDBC and MySQL.

I built this project to understand how a banking application works with Java and a relational database.

## Features

- User Registration & Login
- Mobile Number
- Create Bank Account
- Generate Account Number
- Debit & Credit Money
- Transfer Money
- Check Balance
- Transaction History
- Account Details
- Change Security PIN
- Forgot Security PIN
- OTP Verification
- Input Validation
- Error Handling

## 🛠️ Technologies Used

- Java
- JDBC
- MySQL
- IntelliJ IDEA

##  What I Learned

- Connecting Java with MySQL using JDBC
- Working with `Connection`, `PreparedStatement` and `ResultSet`
- Database transactions using `commit()` and `rollback()`
- Java OOP concepts
- Exception handling
- Input validation
- Working with multiple classes

## 🗄️ Database

The project uses MySQL with three main tables:

- `users` — stores user information
- `accounts` — stores bank account information
- `transactions` — stores transaction history

## 🔄 Money Transfer

The system:

1. Checks the receiver account
2. Checks the security PIN
3. Checks available balance
4. Debits money from the sender
5. Credits money to the receiver
6. Saves transaction history
7. Uses `commit()` / `rollback()` for transaction safety

## 🔐 Security PIN

- Change Security PIN
- Forgot Security PIN
- 6-digit PIN validation
- OTP verification

## 🚀 Future Plans

I plan to rebuild this project using:

- Spring Boot
- REST API
- React
- Better authentication
- Real Email/SMS OTP
- Password hashing

This will make the project closer to a real-world banking application.

## 👨‍💻 Developer

**Umakant Sah**
