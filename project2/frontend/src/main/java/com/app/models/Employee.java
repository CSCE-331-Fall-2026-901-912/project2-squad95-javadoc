package com.app.models;
/**
 * 
 * simple record to represent an employee entry in the database
 * @param id primary key id unique to each employee
 * @param name name of employee
 * @param role either cashier or manager
 */
public record Employee(int id, String name, String role) {}