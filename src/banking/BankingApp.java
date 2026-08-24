package banking;

import java.sql.*;
import java.util.Scanner;

public class BankingApp {

    private static final String url =
            "jdbc:mysql://localhost:3306/banking_system";

    private static final String user = "root";

    private static final String password = "umakant@5333";

    public static void main(String[] args)
            throws ClassNotFoundException, SQLException {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {

            Connection connection =
                    DriverManager.getConnection(url, user, password);

            Scanner scanner = new Scanner(System.in);

            User user = new User(connection, scanner);
            Account account = new Account(connection, scanner);
            AccountManager accountManager =
                    new AccountManager(connection, scanner);

            String email;
            long account_number;

            while (true) {

                System.out.println("*** WELCOME TO BANKING SYSTEM ***");
                System.out.println();
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.println("Enter your choice: ");

                int choice = scanner.nextInt();

                switch (choice) {

                    case 1:
                        user.register();

                        System.out.println("\033[H\033[2J");
                        System.out.flush();

                        break;

                    case 2:

                        email = user.login();

                        if (email != null) {

                            System.out.println();
                            System.out.println("User Logged In!");

                            if (!account.account_exist(email)) {

                                System.out.println();
                                System.out.println(
                                        "1. Open a new Bank Account"
                                );
                                System.out.println("2. Exit");

                                if (scanner.nextInt() == 1) {

                                    account_number =
                                            account.open_account(email);

                                    System.out.println(
                                            "Account Created Successfully!"
                                    );

                                    System.out.println(
                                            "Your Account Number is: "
                                                    + account_number
                                    );

                                } else {
                                    break;
                                }
                            }

                            account_number =
                                    account.getAccount_number(email);

                            int choice2 = 0;

                            while (choice2 != 9) {

                                System.out.println();
                                System.out.println("1. Deposit Money");
                                System.out.println("2. Credit Money");
                                System.out.println("3. Transfer Money");
                                System.out.println("4. Check Balance");
                                System.out.println("5. Transaction History");
                                System.out.println("6. Account Details");
                                System.out.println("7. Change Security PIN");
                                System.out.println("8. Forgot Security PIN");
                                System.out.println("9. Log out");

                                choice2 = scanner.nextInt();

                                switch (choice2) {

                                    case 1:
                                        accountManager.debit_money(account_number
                                        );
                                        break;

                                    case 2:
                                        accountManager.credit_Money(account_number
                                        );
                                        break;

                                    case 3:
                                        accountManager.transfer_money(account_number
                                        );
                                        break;

                                    case 4:
                                        accountManager.getBalance(account_number
                                        );
                                        break;
                                    case 5:
                                        accountManager.getTransactionHistory(account_number);
                                        break;
                                    case 6:
                                        account.getAccountDetails(email);
                                        break;


                                    case 7:
                                        accountManager.changeSecurityPin(account_number);
                                        break;

                                    case 8:
                                        accountManager.forgotSecurityPin();
                                        break;

                                    case 9:
                                        break;

                                    default:
                                        System.out.println(
                                                "Enter valid Choice"
                                        );
                                        break;
                                }
                            }

                        } else {

                            System.out.println(
                                    "Incorrect Email or Password"
                            );
                        }

                        break;

                    case 3:

                        System.out.println(
                                "THANK YOU FOR USING BANKING SYSTEM!"
                        );

                        System.out.println(
                                "Exiting System!"
                        );

                        return;

                    default:

                        System.out.println(
                                "Enter valid Choice"
                        );

                        break;
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }
}