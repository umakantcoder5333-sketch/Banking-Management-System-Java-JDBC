package banking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AccountManager {

    private Connection connection;
    private Scanner scanner;

    public AccountManager(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }


    // =========================
    // CREDIT MONEY
    // =========================
    public void credit_Money(long account_number) throws SQLException {

        scanner.nextLine();

        System.out.println("Enter Amount: ");
        double amount = scanner.nextDouble();

        if (amount <= 0) {
            System.out.println("Amount must be greater than 0!");
            return;
        }

        scanner.nextLine();

        System.out.println("Enter Security pin: ");
        String security_pin = scanner.nextLine();

        try {

            connection.setAutoCommit(false);

            String query =
                    "SELECT * FROM accounts " +
                            "WHERE account_number = ? " +
                            "AND security_pin = ?";

            PreparedStatement preparedStatement =
                    connection.prepareStatement(query);

            preparedStatement.setLong(1, account_number);
            preparedStatement.setString(2, security_pin);

            ResultSet resultSet =
                    preparedStatement.executeQuery();

            if (resultSet.next()) {

                String credit_query =
                        "UPDATE accounts " +
                                "SET balance = balance + ? " +
                                "WHERE account_number = ?";

                PreparedStatement preparedStatement1 =
                        connection.prepareStatement(credit_query);

                preparedStatement1.setDouble(1, amount);
                preparedStatement1.setLong(2, account_number);

                int rowsAffected =
                        preparedStatement1.executeUpdate();

                if (rowsAffected > 0) {

                    String transaction_query =
                            "INSERT INTO transactions " +
                                    "(account_number, transaction_type, amount, description) " +
                                    "VALUES (?, ?, ?, ?)";

                    PreparedStatement transactionStatement =
                            connection.prepareStatement(transaction_query);

                    transactionStatement.setLong(1, account_number);
                    transactionStatement.setString(2, "CREDIT");
                    transactionStatement.setDouble(3, amount);
                    transactionStatement.setString(
                            4,
                            "Money credited to account"
                    );

                    transactionStatement.executeUpdate();

                    connection.commit();

                    System.out.println(
                            "Rs. " + amount +
                                    " credit successfully"
                    );

                } else {

                    connection.rollback();

                    System.out.println(
                            "Credit transaction failed!"
                    );
                }

            } else {

                connection.rollback();

                System.out.println(
                        "Invalid Security PIN!"
                );
            }

        } catch (SQLException e) {

            connection.rollback();

            System.out.println(
                    "Credit transaction failed!"
            );

            e.printStackTrace();

        } finally {

            connection.setAutoCommit(true);
        }
    }


    // =========================
    // DEBIT MONEY
    // =========================
    public void debit_money(long account_number)
            throws SQLException {

        scanner.nextLine();

        System.out.println("Enter Amount: ");
        double amount = scanner.nextDouble();

        if (amount <= 0) {
            System.out.println(
                    "Amount must be greater than 0!"
            );
            return;
        }

        scanner.nextLine();

        System.out.println("Enter Security pin: ");
        String security_pin = scanner.nextLine();

        try {

            connection.setAutoCommit(false);

            String query =
                    "SELECT * FROM accounts " +
                            "WHERE account_number = ? " +
                            "AND security_pin = ?";

            PreparedStatement preparedStatement =
                    connection.prepareStatement(query);

            preparedStatement.setLong(1, account_number);
            preparedStatement.setString(2, security_pin);

            ResultSet resultSet =
                    preparedStatement.executeQuery();

            if (resultSet.next()) {

                double balance =
                        resultSet.getDouble("balance");

                if (amount > balance) {

                    connection.rollback();

                    System.out.println(
                            "Insufficient Balance!"
                    );

                    System.out.println(
                            "Available Balance: Rs. " +
                                    balance
                    );

                    return;
                }

                String debit_query =
                        "UPDATE accounts " +
                                "SET balance = balance - ? " +
                                "WHERE account_number = ?";

                PreparedStatement preparedStatement1 =
                        connection.prepareStatement(debit_query);

                preparedStatement1.setDouble(1, amount);
                preparedStatement1.setLong(2, account_number);

                int rowsAffected =
                        preparedStatement1.executeUpdate();

                if (rowsAffected > 0) {

                    String transaction_query =
                            "INSERT INTO transactions " +
                                    "(account_number, transaction_type, amount, description) " +
                                    "VALUES (?, ?, ?, ?)";

                    PreparedStatement transactionStatement =
                            connection.prepareStatement(
                                    transaction_query
                            );

                    transactionStatement.setLong(
                            1,
                            account_number
                    );

                    transactionStatement.setString(
                            2,
                            "DEBIT"
                    );

                    transactionStatement.setDouble(
                            3,
                            amount
                    );

                    transactionStatement.setString(
                            4,
                            "Money debited from account"
                    );

                    transactionStatement.executeUpdate();

                    connection.commit();

                    System.out.println(
                            "Rs. " + amount +
                                    " debited successfully"
                    );

                } else {

                    connection.rollback();

                    System.out.println(
                            "Debit transaction failed!"
                    );
                }

            } else {

                connection.rollback();

                System.out.println(
                        "Invalid Security PIN!"
                );
            }

        } catch (SQLException e) {

            connection.rollback();

            System.out.println(
                    "Debit transaction failed!"
            );

            e.printStackTrace();

        } finally {

            connection.setAutoCommit(true);
        }
    }


    // =========================
    // TRANSFER MONEY
    // =========================
    public void transfer_money(long sender_account_number)
            throws SQLException {

        scanner.nextLine();

        System.out.println(
                "Enter Receiver Account Number: "
        );

        long receiver_account_number =
                scanner.nextLong();

        // Own account check
        if (sender_account_number ==
                receiver_account_number) {

            System.out.println(
                    "You cannot transfer money to your own account!"
            );

            return;
        }

        // Receiver account check
        String receiverCheckQuery =
                "SELECT account_number FROM accounts " +
                        "WHERE account_number = ?";

        PreparedStatement receiverCheck =
                connection.prepareStatement(
                        receiverCheckQuery
                );

        receiverCheck.setLong(
                1,
                receiver_account_number
        );

        ResultSet receiverResult =
                receiverCheck.executeQuery();

        if (!receiverResult.next()) {

            System.out.println(
                    "Receiver account not found!"
            );

            System.out.println(
                    "Please enter a valid account number."
            );

            return;
        }

        System.out.println("Enter Amount: ");

        double amount =
                scanner.nextDouble();

        if (amount <= 0) {

            System.out.println(
                    "Amount must be greater than 0!"
            );

            return;
        }

        scanner.nextLine();

        System.out.println(
                "Enter Security pin: "
        );

        String security_pin =
                scanner.nextLine();

        try {

            connection.setAutoCommit(false);

            String senderQuery =
                    "SELECT balance FROM accounts " +
                            "WHERE account_number = ? " +
                            "AND security_pin = ?";

            PreparedStatement preparedStatement =
                    connection.prepareStatement(senderQuery);

            preparedStatement.setLong(
                    1,
                    sender_account_number
            );

            preparedStatement.setString(
                    2,
                    security_pin
            );

            ResultSet resultSet =
                    preparedStatement.executeQuery();

            // Wrong PIN
            if (!resultSet.next()) {

                connection.rollback();

                System.out.println(
                        "Invalid Security PIN!"
                );

                return;
            }

            double balance =
                    resultSet.getDouble("balance");

            // Insufficient balance
            if (amount > balance) {

                connection.rollback();

                System.out.println(
                        "Insufficient Balance!"
                );

                System.out.println(
                        "Available Balance: Rs. " +
                                balance
                );

                return;
            }

            // Debit sender
            String debitQuery =
                    "UPDATE accounts " +
                            "SET balance = balance - ? " +
                            "WHERE account_number = ?";

            PreparedStatement debitStatement =
                    connection.prepareStatement(debitQuery);

            debitStatement.setDouble(1, amount);
            debitStatement.setLong(
                    2,
                    sender_account_number
            );

            // Credit receiver
            String creditQuery =
                    "UPDATE accounts " +
                            "SET balance = balance + ? " +
                            "WHERE account_number = ?";

            PreparedStatement creditStatement =
                    connection.prepareStatement(creditQuery);

            creditStatement.setDouble(1, amount);
            creditStatement.setLong(
                    2,
                    receiver_account_number
            );

            int debitRows =
                    debitStatement.executeUpdate();

            int creditRows =
                    creditStatement.executeUpdate();

            if (debitRows > 0 &&
                    creditRows > 0) {

                // Sender transaction
                String senderTransactionQuery =
                        "INSERT INTO transactions " +
                                "(account_number, transaction_type, amount, description) " +
                                "VALUES (?, ?, ?, ?)";

                PreparedStatement senderTransaction =
                        connection.prepareStatement(
                                senderTransactionQuery
                        );

                senderTransaction.setLong(
                        1,
                        sender_account_number
                );

                senderTransaction.setString(
                        2,
                        "DEBIT"
                );

                senderTransaction.setDouble(
                        3,
                        amount
                );

                senderTransaction.setString(
                        4,
                        "Money transferred to account " +
                                receiver_account_number
                );

                senderTransaction.executeUpdate();

                // Receiver transaction
                String receiverTransactionQuery =
                        "INSERT INTO transactions " +
                                "(account_number, transaction_type, amount, description) " +
                                "VALUES (?, ?, ?, ?)";

                PreparedStatement receiverTransaction =
                        connection.prepareStatement(
                                receiverTransactionQuery
                        );

                receiverTransaction.setLong(
                        1,
                        receiver_account_number
                );

                receiverTransaction.setString(
                        2,
                        "CREDIT"
                );

                receiverTransaction.setDouble(
                        3,
                        amount
                );

                receiverTransaction.setString(
                        4,
                        "Money received from account " +
                                sender_account_number
                );

                receiverTransaction.executeUpdate();

                connection.commit();

                System.out.println(
                        "Transaction Successful!"
                );

                System.out.println(
                        "Rs. " + amount +
                                " Transfer successfully"
                );

            } else {

                connection.rollback();

                System.out.println(
                        "Transaction failed! Please try again."
                );
            }

        } catch (SQLException e) {

            connection.rollback();

            System.out.println(
                    "Transaction failed due to database error."
            );

            e.printStackTrace();

        } finally {

            connection.setAutoCommit(true);
        }
    }


    // =========================
    // GET BALANCE
    // =========================
    public void getBalance(long account_number) {

        scanner.nextLine();

        System.out.println(
                "Enter Security pin: "
        );

        String security_pin =
                scanner.nextLine();

        try {

            String query =
                    "SELECT balance FROM accounts " +
                            "WHERE account_number = ? " +
                            "AND security_pin = ?";

            PreparedStatement preparedStatement =
                    connection.prepareStatement(query);

            preparedStatement.setLong(
                    1,
                    account_number
            );

            preparedStatement.setString(
                    2,
                    security_pin
            );

            ResultSet resultSet =
                    preparedStatement.executeQuery();

            if (resultSet.next()) {

                double balance =
                        resultSet.getDouble("balance");

                System.out.println(
                        "Balance: " + balance
                );

            } else {

                System.out.println(
                        "Invalid Security PIN!"
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }


    // =========================
    // TRANSACTION HISTORY
    // =========================
    public void getTransactionHistory(
            long account_number) {

        String query =
                "SELECT * FROM transactions " +
                        "WHERE account_number = ? " +
                        "ORDER BY transaction_date DESC";

        try {

            PreparedStatement preparedStatement =
                    connection.prepareStatement(query);

            preparedStatement.setLong(
                    1,
                    account_number
            );

            ResultSet resultSet =
                    preparedStatement.executeQuery();

            System.out.println(
                    "\n===== TRANSACTION HISTORY ====="
            );

            boolean found = false;

            while (resultSet.next()) {

                found = true;

                System.out.println(
                        "Transaction ID: " +
                                resultSet.getLong(
                                        "transaction_id"
                                )
                );

                System.out.println(
                        "Type: " +
                                resultSet.getString(
                                        "transaction_type"
                                )
                );

                System.out.println(
                        "Amount: Rs. " +
                                resultSet.getDouble(
                                        "amount"
                                )
                );

                System.out.println(
                        "Date: " +
                                resultSet.getTimestamp(
                                        "transaction_date"
                                )
                );

                System.out.println(
                        "Description: " +
                                resultSet.getString(
                                        "description"
                                )
                );

                System.out.println(
                        "-----------------------------"
                );
            }

            if (!found) {

                System.out.println(
                        "No transactions found."
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }


    // =========================
    // CHANGE SECURITY PIN
    // =========================
    public void changeSecurityPin(
            long account_number) {

        scanner.nextLine();

        System.out.println(
                "Enter Current Security Pin: "
        );

        String current_pin =
                scanner.nextLine();

        System.out.println(
                "Enter New Security Pin: "
        );

        String new_pin =
                scanner.nextLine();

        // 6 digit validation
        if (!new_pin.matches("\\d{6}")) {

            System.out.println(
                    "Security PIN must be exactly 6 digits!"
            );

            return;
        }

        System.out.println(
                "Confirm New Security Pin: "
        );

        String confirm_pin =
                scanner.nextLine();

        // Confirm PIN
        if (!new_pin.equals(confirm_pin)) {

            System.out.println(
                    "New PIN and Confirm PIN do not match!"
            );

            return;
        }

        String query =
                "UPDATE accounts " +
                        "SET security_pin = ? " +
                        "WHERE account_number = ? " +
                        "AND security_pin = ?";

        try {

            PreparedStatement preparedStatement =
                    connection.prepareStatement(query);

            preparedStatement.setString(
                    1,
                    new_pin
            );

            preparedStatement.setLong(
                    2,
                    account_number
            );

            preparedStatement.setString(
                    3,
                    current_pin
            );

            int rowsAffected =
                    preparedStatement.executeUpdate();

            if (rowsAffected > 0) {

                System.out.println(
                        "Security PIN changed successfully!"
                );

            } else {

                System.out.println(
                        "Current Security PIN is incorrect!"
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }


    // =========================
// FORGOT SECURITY PIN
// =========================
    public void forgotSecurityPin() {

        scanner.nextLine();

        System.out.println("Enter Email: ");
        String email = scanner.nextLine();

        System.out.println("Enter Mobile Number: ");
        String mobile_number = scanner.nextLine();

        System.out.println("Enter Account Number: ");
        long account_number = scanner.nextLong();

        scanner.nextLine();

        // Verify user details
        String verifyQuery =
                "SELECT a.account_number " +
                        "FROM accounts a " +
                        "JOIN users u ON a.user_id = u.user_id " +
                        "WHERE u.email = ? " +
                        "AND u.mobile_number = ? " +
                        "AND a.account_number = ?";

        try {

            PreparedStatement preparedStatement =
                    connection.prepareStatement(verifyQuery);

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, mobile_number);
            preparedStatement.setLong(3, account_number);

            ResultSet resultSet =
                    preparedStatement.executeQuery();

            if (!resultSet.next()) {

                System.out.println(
                        "Invalid Email, Mobile Number or Account Number!"
                );

                return;
            }

            // =========================
            // GENERATE OTP
            // =========================

            int otp = 100000 + (int)(Math.random() * 900000);

            System.out.println();
            System.out.println("OTP sent successfully!");
            System.out.println(
                    "Your OTP is: " + otp
            );
            System.out.println(
                    "(Demo OTP - later we can send it by SMS/Email)"
            );

            System.out.println();
            System.out.println("Enter OTP: ");

            int enteredOtp = scanner.nextInt();

            scanner.nextLine();

            // Verify OTP
            if (enteredOtp != otp) {

                System.out.println(
                        "Invalid OTP!"
                );

                return;
            }

            System.out.println(
                    "OTP verified successfully!"
            );

            // =========================
            // NEW PIN
            // =========================

            System.out.println(
                    "Enter New Security Pin: "
            );

            String new_pin = scanner.nextLine();

            if (!new_pin.matches("\\d{6}")) {

                System.out.println(
                        "Security PIN must be exactly 6 digits!"
                );

                return;
            }

            System.out.println(
                    "Confirm New Security Pin: "
            );

            String confirm_pin = scanner.nextLine();

            if (!new_pin.equals(confirm_pin)) {

                System.out.println(
                        "New PIN and Confirm PIN do not match!"
                );

                return;
            }

            // =========================
            // UPDATE PIN
            // =========================

            String updateQuery =
                    "UPDATE accounts " +
                            "SET security_pin = ? " +
                            "WHERE account_number = ?";

            PreparedStatement updateStatement =
                    connection.prepareStatement(updateQuery);

            updateStatement.setString(1, new_pin);
            updateStatement.setLong(2, account_number);

            int rowsAffected =
                    updateStatement.executeUpdate();

            if (rowsAffected > 0) {

                System.out.println();
                System.out.println(
                        "Security PIN reset successfully!"
                );

            } else {

                System.out.println(
                        "Failed to reset Security PIN!"
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }
}