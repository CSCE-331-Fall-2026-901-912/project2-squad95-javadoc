package com.app.models;

import java.util.Map;

import com.app.models.Item;


/**
 * represents a recipe in the front end, contains all of the inventory items used by one menu item
 * 
 * @param item the menu item the recipe is for
 * @param recipe a map containing inventory items as keys and the quantity of each inventory item as values
 */
public record Recipe(Item item, Map<InventoryItem, Integer> recipe) {}
