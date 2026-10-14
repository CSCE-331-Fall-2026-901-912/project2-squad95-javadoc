package com.app.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.Map;

import com.app.Database;
import com.app.models.InventoryItem;
import com.app.models.Recipe;

public class RecipeDAO {
    public Recipe inserts(int itemId, Map<InventoryItem, Integer> recipe) throws SQLException {
        try (Connection conn = Database.getConnection()) {
            for (Map.Entry<InventoryItem, Integer> entry : recipe.entrySet()) {
                int id = Ids.nextId(conn, "recipes");
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO recipes (id, item_id, inventory_id, quantity) VALUES (?, ?, ?, ?)")) {
                    ps.setInt(1, id);
                    ps.setInt(2, itemId);
                    ps.setInt(3, entry.getKey().id());
                    ps.setInt(4, entry.getValue());
                    ps.executeUpdate();
                }
            }
        }
        return new Recipe(itemId, recipe);
    }
}
