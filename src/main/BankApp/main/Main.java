package main.BankApp.main;

import main.BankApp.models.Account;
import main.BankApp.models.Transaction;
import main.BankApp.models.User;
import main.BankApp.service.AccountService;
import main.BankApp.service.TransService;
import main.BankApp.service.UserService;

import java.util.Scanner;

public class Main {
    public static Account account = new Account();
    public static Transaction transaction = new Transaction();
    public static UserService userServ = new UserService();
    public static AccountService accountServ = new AccountService();
    public static TransService transServ = new TransService();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        mainMenu();


    }

    public static void mainMenu() throws Exception {
        boolean banking = true;
        while (banking) {
            System.out.println(
                    "----------------------------------------\n"
                            + "Welcome to The Bank\n"
                            + "----------------------------------------\n\n"
                            + "1. Login to my account\n"
                            + "2. Create an account\n"
                            + "3. Exit The Bank\n\n"
                            + "----------------------------------------\n\n"
                            + "What would you like to do?"
            );
            int selection = scanner.nextInt();
            switch (selection) {
                case 1:
                    User oldUser = new User();
                    oldUser.login();
                    oldUser = userServ.login(oldUser.getUsername(), oldUser.getPassword());
                    if (oldUser != null) {
                        userMenu(oldUser);
                    }
                    break;
                case 2:
                    //create user in db
                    User newUser = new User();
                    newUser.createUser();
                    userServ.createUser(newUser);
                    //create account in db
                    Account newAccount = new Account();
                    newAccount.createAccount(newUser);
                    accountServ.createAccount(newAccount, newAccount.getAccountType(), newAccount.getBalance(), newUser);
                    break;
                case 3:
                    banking = false;
                    System.out.println("Thank you for using The Bank");
            }
        }
    }
    public static void userMenu(User user) {
        boolean banking = true;
        while (banking) {
            account = accountServ.getAccount(user);
            System.out.println(
                    "----------------------------------------------------\n"
                            + " Welcome back to The Bank, " + user.getName() + "\n"
                            + "----------------------------------------------------\n"
                            + " Account Type: " + account.getAccountType() + "\n"
                            + "----------------------------------------------------\n"
                            + "	Account Balance: $" + account.getBalance() + "\n\n"
                            + "	1. Adjust your account\n"
                            + "    2. View your transactions\n"
                            + "	3. Logout\n\n"
                            + "----------------------------------------------------\n"
                            + "What would you like to do?"
            );
            int selection = scanner.nextInt();
            switch (selection) {
                case 1:
                    Transaction newTrans = new Transaction();
                    newTrans.createTransaction(account);
                    if (newTrans.getTransType() == "Withdrawal" && newTrans.getAmount() > account.getBalance()) {
                        System.out.println(
                                "-------------------------------------------\n"
                                + "You are trying to overdraw your account!\n"
                                + "-------------------------------------------\n"
                        );
                    } else if (newTrans.getAmount() < 0) {
                        System.out.println(
                                "-------------------------------------------\n"
                                + "You can't enter a negative amount!\n"
                                + "-------------------------------------------\n"
                        );
                    } else {
                        transaction = transServ.createTrans(newTrans, account.getId(), newTrans.getTransType(), newTrans.getAmount(), account);
                    }
                    accountServ.updateBalance(account, account.getBalance(), transaction.getTransType(), transaction.getAmount());
                    break;
                case 2:
                    System.out.println(transServ.findTransactions(account));
                    break;
                case 3:
                    System.out.println("Thank you for using our mobile banking!");
                    banking = false;
            }
        }
    }
}
