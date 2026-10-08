package com.app.controllers;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import com.app.models.InventoryItem;
import com.app.Database;

public class InventoryDAO {
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

    public void updateStock(int id, int stock) throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE inventory SET stock = ? WHERE id = ?")) {
            ps.setInt(1, stock);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }
}
