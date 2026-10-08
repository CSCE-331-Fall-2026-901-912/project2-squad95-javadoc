package com.app.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.app.models.Item;
import com.app.Database;

public class ItemDAO {
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
