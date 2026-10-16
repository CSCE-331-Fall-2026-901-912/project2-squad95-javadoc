package com.app.models;

/**
 * simple record to repsent item (menu item) entry in database
 * 
 * @param id primary key id unique to each item
 * @param name name of the item
 * @param basePrice base price of the item
 */
public record Item(int id, String name, double basePrice) {}
