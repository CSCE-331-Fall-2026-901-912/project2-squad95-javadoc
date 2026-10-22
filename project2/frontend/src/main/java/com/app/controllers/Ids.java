package com.app.controllers;

import java.sql.*;

/**
 * Utility for generating sequential primary-key ids.
 */
public final class Ids {
    /** Utility class; not instantiable. */
    private Ids() {}

    /**
     * Computes the next available id for a table.
     *
     * @param conn an open database connection
     * @param table the table name; must be a hard-coded name, never user input
     * @return one greater than the current maximum id (1 if the table is empty)
     * @throws SQLException if a database access error occurs
     */
    public static int nextId(Connection conn, String table) throws SQLException {
        String sql = "SELECT COALESCE(MAX(id), 0) + 1 FROM " + table;
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        }
    }
}