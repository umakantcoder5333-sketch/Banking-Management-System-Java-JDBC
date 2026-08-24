package banking;

import java.sql.*;
import java.util.Scanner;

public class Account {

    private Connection connection;
    private Scanner scanner;

    public Account(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public long open_account(String email) {

        if (!account_exist(email)) {

            String open_account_query =
                    "INSERT INTO accounts(account_number, user_id, balance, security_pin) " +
                            "VALUES (?, ?, ?, ?)";

            scanner.nextLine();

            System.out.println("Enter Initial Amount: ");
            double balance = scanner.nextDouble();

            scanner.nextLine();

            System.out.println("Enter Security Pin:");
            String security_pin = scanner.nextLine();

            try {

                long account_number = generateAccountNumber();

                // Get user_id using email
                String user_id_query =
                        "SELECT user_id FROM users WHERE email = ?";

                PreparedStatement userStatement =
                        connection.prepareStatement(user_id_query);

                userStatement.setString(1, email);

                ResultSet userResult =
                        userStatement.executeQuery();

                if (userResult.next()) {

                    int user_id =
                            userResult.getInt("user_id");

                    PreparedStatement preparedStatement =
                            connection.prepareStatement(open_account_query);

                    preparedStatement.setLong(1, account_number);
                    preparedStatement.setInt(2, user_id);
                    preparedStatement.setDouble(3, balance);
                    preparedStatement.setString(4, security_pin);

                    int rowsAffected =
                            preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        return account_number;
                    } else {
                        throw new RuntimeException(
                                "Account Creation Failed"
                        );
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        throw new RuntimeException("Account Already Exists");
    }


    public long getAccount_number(String email) {

        String query =
                "SELECT a.account_number " +
                        "FROM accounts a " +
                        "JOIN users u ON a.user_id = u.user_id " +
                        "WHERE u.email = ?";

        try {

            PreparedStatement preparedStatement =
                    connection.prepareStatement(query);

            preparedStatement.setString(1, email);

            ResultSet resultSet =
                    preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getLong("account_number");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("Account Not Found");
    }


    private long generateAccountNumber() {

        try {

            Statement statement =
                    connection.createStatement();

            ResultSet resultSet =
                    statement.executeQuery(
                            "SELECT account_number " +
                                    "FROM accounts " +
                                    "ORDER BY account_number DESC " +
                                    "LIMIT 1"
                    );

            if (resultSet.next()) {

                long last_account_number =
                        resultSet.getLong("account_number");

                return last_account_number + 1;

            } else {
                return 10000100;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 10000100;
    }


    public boolean account_exist(String email) {

        String query =
                "SELECT a.account_number " +
                        "FROM accounts a " +
                        "JOIN users u ON a.user_id = u.user_id " +
                        "WHERE u.email = ?";

        try {

            PreparedStatement preparedStatement =
                    connection.prepareStatement(query);

            preparedStatement.setString(1, email);

            ResultSet resultSet =
                    preparedStatement.executeQuery();

            return resultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    public void getAccountDetails(String email) {

        String query =
                "SELECT u.full_name, u.email, " +
                        "a.account_number, a.balance " +
                        "FROM users u " +
                        "JOIN accounts a ON u.user_id = a.user_id " +
                        "WHERE u.email = ?";

        try {

            PreparedStatement preparedStatement =
                    connection.prepareStatement(query);

            preparedStatement.setString(1, email);

            ResultSet resultSet =
                    preparedStatement.executeQuery();

            if (resultSet.next()) {

                System.out.println("\n===== ACCOUNT DETAILS =====");

                System.out.println(
                        "Account Number: " +
                                resultSet.getLong("account_number")
                );

                System.out.println(
                        "Full Name: " +
                                resultSet.getString("full_name")
                );

                System.out.println(
                        "Email: " +
                                resultSet.getString("email")
                );

                System.out.println(
                        "Balance: Rs. " +
                                resultSet.getDouble("balance")
                );

            } else {

                System.out.println("Account Not Found!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}