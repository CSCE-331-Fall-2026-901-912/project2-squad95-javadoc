package com.app.models;

/**
 * simple record to represent a topping entry in the database
 * 
 * @param id primary key id unique to each topping
 * @param name name of the topping
 * @param basePrice base price of the topping
 */
public record Topping(int id, String name, double basePrice) {}
