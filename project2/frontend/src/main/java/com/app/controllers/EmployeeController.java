package com.app.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.app.Database;
import com.app.models.Employee;

/**
 * Controller providing access to employee records.
 */
public class EmployeeController {
    /** Creates an employee controller. */
    public EmployeeController() {}

    /**
     * Retrieves all employees from the database.
     *
     * @return the list of all employees
     * @throws SQLException if a database access error occurs
     */
    public List<Employee> getAll() throws SQLException {
        String sql = "SELECT id, name, role FROM toppings";
        List<Employee> employees = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                employees.add(new Employee(
                    rs.getInt("id"), rs.getString("name"),
                    rs.getString("role")));
            }
        }
        return employees;
    }
}
