package com.app.models;

public record EmployeeRow(int id, String name, String role, double sales) {
    public boolean isManager() { return "manager".equalsIgnoreCase(role); }
}