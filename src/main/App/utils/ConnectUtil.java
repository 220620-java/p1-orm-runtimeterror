package main.BankApp.utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectUtil {
    static final String endpoint="jdbc:postgresql://awsdb.ch3f9makdbyg.us-east-1.rds.amazonaws.com:5432/postgres", username="postgres", password="awsdatabase";
    private static ConnectUtil connectUtil;

    //private constructor
    private ConnectUtil() {
    }
    public static synchronized ConnectUtil getConnectUtil() {
        if (connectUtil == null) {
            connectUtil = new ConnectUtil();
        }
        return connectUtil;
    }
    //connect to the database
    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(endpoint, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
}
