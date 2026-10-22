package com.app.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.app.models.Item;
import com.app.Database;

/**
 * Data access object for menu items.
 */
public class ItemDAO {
    /** Creates an item DAO. */
    public ItemDAO() {}

    /**
     * Retrieves all menu items ordered by id.
     *
     * @return the list of menu items
     * @throws SQLException if a database access error occurs
     */
    public List<Item> getAll() throws SQLException {
        String sql = "SELECT id, name, base_price FROM items ORDER BY id";
        List<Item> items = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                items.add(new Item(rs.getInt("id"), rs.getString("name"), rs.getDouble("base_price")));
            }
        }
        return items;
    }

    /**
     * Inserts a new menu item with the next available id.
     *
     * @param name the item name
     * @param basePrice the base price of the item
     * @return the newly created item
     * @throws SQLException if a database access error occurs
     */
    public Item insert(String name, double basePrice) throws SQLException {
        try (Connection conn = Database.getConnection()) {
            int id = Ids.nextId(conn, "items");
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO items (id, name, base_price) VALUES (?, ?, ?)")) {
                ps.setInt(1, id);
                ps.setString(2, name);
                ps.setDouble(3, basePrice);
                ps.executeUpdate();
            }
            return new Item(id, name, basePrice);
        }
    }

    /**
     * Updates the name and base price of an existing item.
     *
     * @param item the item containing the id and new values
     * @throws SQLException if a database access error occurs
     */
    public void update(Item item) throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "UPDATE items SET name = ?, base_price = ? WHERE id = ?")) {
            ps.setString(1, item.name());
            ps.setDouble(2, item.basePrice());
            ps.setInt(3, item.id());
            ps.executeUpdate();
        }
    }
}
