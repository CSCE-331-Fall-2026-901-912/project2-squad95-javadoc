package com.app.controllers;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import com.app.models.InventoryItem;
import com.app.Database;

public class InventoryDAO {
    /**
     * Returns all inventory items from database as a list.
     * @return List of InventoryItem objects representing rows in inventory table
     * @throws SQLException
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
     * Inserts an inventory item into the database
     * @param name Name of new inventory item
     * @param unit Units of the stock value 
     * @param stock Initial value of the inventory item 
     * @return New InventoryItem object representing added inventory item
     * @throws SQLException
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
     * Updates the stock of an inventory item in the database to a new value
     * @param id Id of target inventory item
     * @param stock New stock value
     * @throws SQLException
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
