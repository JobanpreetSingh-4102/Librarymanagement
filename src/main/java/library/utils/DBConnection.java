package main.java.library.utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/library_db";

    private static final String USER = "postgres";
    private static final String PASSWORD = "admin123";

    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");

            Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connected to PostgreSQL!");
            return con;
        } catch (Exception e) {
            System.out.println("❌ Connection Failed!");
            e.printStackTrace();
            return null;
        }
    }
}


