package com.app.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.app.Database;
import com.app.models.EmployeeRow;

public class EmployeeDAO {
    /**
     * Takes in the employees from the database and organize them in a row
     * @return List<EmployeeRow>
     * @throws SQLException
     */
    public List<EmployeeRow> getAllWithSales() throws SQLException {
        String sql = """
            SELECT e.id, e.name, e.role, COALESCE(SUM(o.grandtotal), 0) AS sales
            FROM employees e LEFT JOIN orders o ON o.employee_id = e.id
            GROUP BY e.id, e.name, e.role
            ORDER BY e.id
            """;
        List<EmployeeRow> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new EmployeeRow(rs.getInt("id"), rs.getString("name"),
                                         rs.getString("role"), rs.getDouble("sales")));
            }
        }
        return list;
    }

    public void insert(String name, String role) throws SQLException {
        try (Connection conn = Database.getConnection()) {
            int id = Ids.nextId(conn, "employees");
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO employees (id, name, role) VALUES (?, ?, ?)")) {
                ps.setInt(1, id);
                ps.setString(2, name);
                ps.setString(3, role);
                ps.executeUpdate();
            }
        }
    }

    public void updateName(int id, String name) throws SQLException { exec("UPDATE employees SET name = ? WHERE id = ?", name, id); }
    public void updateRole(int id, String role) throws SQLException { exec("UPDATE employees SET role = ? WHERE id = ?", role, id); }
    public void delete(int id) throws SQLException { exec("DELETE FROM employees WHERE id = ?", id); }

    private void exec(String sql, Object... params) throws SQLException {
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            ps.executeUpdate();
        }
    }
}