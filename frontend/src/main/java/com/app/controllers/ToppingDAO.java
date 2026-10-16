package com.app.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.app.Database;
import com.app.models.Topping;

public class ToppingDAO {
    /**
     * Attracts the toppings from the SQL database and return it as a list
     * @return List<Topping>
     * @throws SQLException
     */
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