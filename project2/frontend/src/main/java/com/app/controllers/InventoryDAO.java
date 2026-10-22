package com.app.controllers;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import com.app.models.InventoryItem;
import com.app.Database;

/**
 * Data access object for inventory records.
 */
public class InventoryDAO {
    /** Creates an inventory DAO. */
    public InventoryDAO() {}

    /**
     * Retrieves all inventory items ordered by id.
     *
     * @return the list of inventory items
     * @throws SQLException if a database access error occurs
     */
    public List<InventoryItem> getAll() throws SQLException {
        String sql = "SELECT id, name, unit, stock FROM inventory ORDER BY id";
        List<InventoryItem> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new InventoryItem(rs.getInt("id"), rs.getString("name"),
                                           rs.getString("unit"), rs.getInt("stock")));
            }
        }
        return list;
    }

    /**
     * Inserts a new inventory item with the next available id.
     *
     * @param name the item name
     * @param unit the unit of measure
     * @param stock the initial stock quantity
     * @return the newly created inventory item
     * @throws SQLException if a database access error occurs
     */
    public InventoryItem insert(String name, String unit, int stock) throws SQLException {
        try (Connection conn = Database.getConnection()) {
            int id = Ids.nextId(conn, "inventory");
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO inventory (id, name, unit, stock) VALUES (?, ?, ?, ?)")) {
                ps.setInt(1, id);
                ps.setString(2, name);
                ps.setString(3, unit);
                ps.setInt(4, stock);
                ps.executeUpdate();
            }
            return new InventoryItem(id, name, unit, stock);
        }
    }

    /**
     * Updates the stock quantity of an inventory item.
     *
     * @param id the inventory item id
     * @param stock the new stock quantity
     * @throws SQLException if a database access error occurs
     */
    public void updateStock(int id, int stock) throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE inventory SET stock = ? WHERE id = ?")) {
            ps.setInt(1, stock);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }
}
