package com.app.controllers;

import java.sql.*;

public final class Ids {
    private Ids() {}

    /** table must be a hard-coded name, never user input. */
    public static int nextId(Connection conn, String table) throws SQLException {
        String sql = "SELECT COALESCE(MAX(id), 0) + 1 FROM " + table;
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        }
    }
}