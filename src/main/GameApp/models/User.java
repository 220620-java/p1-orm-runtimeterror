package main.BankApp.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class User {
    static Scanner scanner = new Scanner(System.in);
    private int id;
    private String name, username, password;
    private List<Account> accounts;
    private String password1;

    //------------------ classes -----------------------
    public User (){
        super();
        this.id = 0;
        this.name = "";
        this.username = "";
        this.password = "";
        this.accounts = new ArrayList<>();
    }

    public User (String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    public User (int id, String name, String username, String password){
        super();
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.accounts = new ArrayList<>();
    }

    //------------------------ methods -----------------------
    public void login(){
        System.out.println("What is your username?");
        String username = scanner.nextLine();
        System.out.println("What is your password?");
        String password = scanner.nextLine();
        setUsername(username);
        setPassword(password);
    }
    public void createUser() throws Exception {
        System.out.println("Enter your full name:");
        String name = scanner.nextLine();
        System.out.println("Enter a username:");
        String username = scanner.nextLine();
        System.out.println("Enter a password:");
        String password = scanner.nextLine();
        setName(name);
        setUsername(username);
        setPassword(password);
    }
    @Override
    public String toString() {
        return "User [id=" + id + ", name =" + name + ", username=" + username + ", password=" + password + "]";
    }

    //------------------------ getters & setters -----------------------
    //get & set id------------------------------------------------------------------
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    //get & set name------------------------------------------------------------------
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    //get & set username------------------------------------------------------------------
    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username = username;
    }

    //get & set password------------------------------------------------------------------
    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password = password;
    }
}
