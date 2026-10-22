package com.app.models;

/**
 * 
 * contains the information used for each row of employee table in manager view
 * @param id primary key id unique to each employee
 * @param name name of the employee
 * @param role either cashier or manager
 * @param sales total sales of orders made by the employee
 */
public record EmployeeRow(int id, String name, String role, double sales) {
    /**
     * Checks whether this employee is a manager.
     *
     * @return {@code true} if the role is "manager" (case-insensitive)
     */
    public boolean isManager() { return "manager".equalsIgnoreCase(role); }
}