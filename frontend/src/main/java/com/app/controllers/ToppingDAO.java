package com.app.controllers;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import com.app.models.Topping;
import com.app.Database;

public class ToppingDAO {
    public List<Topping> getAll() throws SQLException {
        String sql = "SELECT id, name, base_price FROM toppings ORDER BY id";
        List<Topping> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Topping(rs.getInt("id"), rs.getString("name"), rs.getDouble("base_price")));
            }
        }
        return list;
    }
}