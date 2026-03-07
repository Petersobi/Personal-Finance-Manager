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
                password TEXT NOT NULL,
                created_at TEXT NOT NULL);
              
                """;
        String createCategoriesTable = """
                CREATE TABLE IF NOT EXISTS categories (
                id INTEGER PRIMARY KEY AUTOINCREMENT ,
                user_Id INTEGER NOT NULL,
                name TEXT NOT NULL,
                type TEXT NOT NULL,
                is_deleted INTEGER NOT NULL,
                FOREIGN KEY (user_id) REFERENCES users(id));
                """;
        String createTransactionsTable = """
                CREATE TABLE IF NOT EXISTS transactions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                category_id INTEGER NOT NULL,
                amount TEXT NOT NULL,
                description TEXT,
                transaction_date TEXT NOT NULL,
                created_at TEXT NOT NULL,
                FOREIGN KEY (category_id) REFERENCES categories(id),
                FOREIGN KEY (user_id) REFERENCES users(id));
                """;
        String createBudgetsTable = """
                CREATE TABLE IF NOT EXISTS budgets(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                category_id INTEGER NOT NULL,
                month TEXT NOT NULL,
                amount TEXT NOT NULL,
                UNIQUE(user_id, category_id, month),
                FOREIGN KEY (user_id) REFERENCES users(id),
                FOREIGN KEY (category_id) REFERENCES categories(id)
                );
                """;

        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createCategoriesTable);
            stmt.execute(createTransactionsTable);
            stmt.execute(createBudgetsTable);
            stmt.execute("PRAGMA foreign_keys = ON;");


        } catch (SQLException e) {
            throw new RuntimeException( "Failed to initialize database", e);
        }
}


}
