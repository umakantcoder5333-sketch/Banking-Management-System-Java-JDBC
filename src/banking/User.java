package banking;

import java.sql.*;
import java.util.Scanner;

public class User {

    private Connection connection;
    private Scanner scanner;

    public User(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }


    public void register() {

        // Consume leftover Enter from scanner.nextInt()
        scanner.nextLine();

        System.out.println("Full Name: ");
        String full_name = scanner.nextLine();

        System.out.println("Email: ");
        String email = scanner.nextLine();

        System.out.println("Mobile Number: ");
        String mobile_number = scanner.nextLine();

        System.out.println("Password: ");
        String password = scanner.nextLine();


        if (user_exist(email)) {

            System.out.println(
                    "User Already Exists for this Email Address!!"
            );

            return;
        }


        String register_query =
                "INSERT INTO users " +
                        "(full_name, email, mobile_number, password) " +
                        "VALUES (?, ?, ?, ?)";


        try {

            PreparedStatement preparedStatement =
                    connection.prepareStatement(register_query);

            preparedStatement.setString(1, full_name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, mobile_number);
            preparedStatement.setString(4, password);


            int affectedRows =
                    preparedStatement.executeUpdate();


            if (affectedRows > 0) {

                System.out.println(
                        "Successfully Registered!"
                );

            } else {

                System.out.println(
                        "Failed Register!"
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }


    public String login() {

        // Consume leftover Enter from scanner.nextInt()
        scanner.nextLine();

        System.out.println("Email:");
        String email = scanner.nextLine();

        System.out.println("Password:");
        String password = scanner.nextLine();


        String login_query =
                "SELECT * FROM users " +
                        "WHERE email = ? AND password = ?";


        try {

            PreparedStatement preparedStatement =
                    connection.prepareStatement(login_query);

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);


            ResultSet resultSet =
                    preparedStatement.executeQuery();


            if (resultSet.next()) {

                return email;

            } else {

                return null;
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return null;
    }


    public boolean user_exist(String email) {

        String query =
                "SELECT * FROM users WHERE email = ?";


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
}