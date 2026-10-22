/**
 * Database.java
 * Manages the database connection for the application
 * using environment variables for configuration.
 * @author: Christopher Jermia
 * @author: Kenny Le
 * @author: Jeffrey Yu
 * @author: Ethan Faulkner
 * @author: Andrew Schrick
 */

package com.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

public final class Database {
    // Connection parameters for the database, for host, user, and password
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
    /**
     * Gets a connection to the database.
     *
     * @return A Connection object representing the database connection.
     * @throws SQLException when an error occurs while establishing the connection.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
