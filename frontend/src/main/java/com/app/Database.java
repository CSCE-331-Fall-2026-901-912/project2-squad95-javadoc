package com.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

public final class Database {

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        Dotenv dotenv = Dotenv.load();
        URL = dotenv.get("DB_HOST");
        USER = dotenv.get("DB_USER");
        PASSWORD = dotenv.get("DB_PASSWORD");
    }

    private Database() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
