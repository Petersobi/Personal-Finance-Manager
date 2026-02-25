package com.peter.financeapp.util;

import java.sql.*;

public class DButil {
    private static final String URL = "jdbc:sqlite:finance.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

public static void initializeDataBase(){
        String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL);
              
                """;
        String createCategoriesTable = """
                CREATE TABLE IF NOT EXISTS categories (
                id INTEGER PRIMARY KEY AUTOINCREMENT ,
                name TEXT NOT NULL,
                user_Id INTEGER,
                FOREIGN KEY (user_id) REFERENCES users(id));
                """;
        String createTransactionsTable = """
                CREATE TABLE IF NOT EXISTS transactions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                amount REAL NOT NULL,
                category_id INTEGER,
                date TEXT,
                user_id INTEGER,
                FOREIGN KEY (category_id) REFERENCES categories(id),
                FOREIGN KEY (user_id) REFERENCES users(id));
                """;

        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createCategoriesTable);
            stmt.execute(createTransactionsTable);


        } catch (SQLException e) {
            throw new RuntimeException( "Could not create tables " +e);
        }
}


}
