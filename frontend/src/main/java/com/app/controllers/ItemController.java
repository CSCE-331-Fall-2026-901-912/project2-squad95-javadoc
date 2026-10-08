package com.app.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.app.models.Item;
import com.app.Database;

public class ItemController {
        public List<Item> getAll() throws SQLException {
        String sql = "SELECT id, name, base_price FROM items";
        List<Item> items = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                items.add(new Item(
                    rs.getInt("id"), rs.getString("name"),
                    rs.getDouble("base_price")));
            }
        }
        return items;
    }
}
