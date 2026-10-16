package com.app.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import com.app.Database;
import com.app.models.InventoryItem;
import com.app.models.Item;
import com.app.models.Recipe;

public class RecipeDAO {
    /**
     * Attracts the recipes from the SQL database and return it as a record with the item name and the item recipe
     * @param item
     * @param recipe
     * @return Recipe
     * @throws SQLException
     */
    public Recipe insert(Item item, Map<InventoryItem, Integer> recipe) throws SQLException {
        try (Connection conn = Database.getConnection()) {
            for (Map.Entry<InventoryItem, Integer> entry : recipe.entrySet()) {
                int id = Ids.nextId(conn, "recipes");
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO recipes (id, item_id, inventory_id, quantity) VALUES (?, ?, ?, ?)")) {
                    ps.setInt(1, id);
                    ps.setInt(2, item.id());
                    ps.setInt(3, entry.getKey().id());
                    ps.setInt(4, entry.getValue());
                    ps.executeUpdate();
                }
            }
        }
        return new Recipe(item, recipe);
    }
}
