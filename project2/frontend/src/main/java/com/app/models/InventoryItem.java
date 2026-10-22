package com.app.models;

/**
 * simple record to represent inventory item entry in database
 * InventoryItem
 * @param id primary key id unique to each inventory item
 * @param name name of the inventory item
 * @param unit units that the stock is measured in (e.g g, ml, etc)
 * @param stock amount of stock in the store in the proper units
 */
public record InventoryItem(int id, String name, String unit, int stock) {}
