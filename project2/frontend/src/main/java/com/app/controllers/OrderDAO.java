package com.app.controllers;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDateTime;

import com.app.Database;
import com.app.models.Order;
import com.app.models.OrderItem;
import com.app.models.OrderReceipt;
import com.app.models.Topping;

/**
 * Data access object for order entries.
 */
public class OrderDAO {
    /**
     * Takes in information on the orders, calculate the total and return as a receipt
     * @param order the drink order
     * @param employeeId the id of the employee who placed the order
     * @param taxRate rate of tax
     * @return OrderReceipt
     * @throws SQLException
     */
    public OrderReceipt checkout(Order order, int employeeId, double taxRate) throws SQLException {
        double subtotal = order.orderItems().stream().mapToDouble(OrderItem::totalPrice).sum();
        subtotal = round2(subtotal);
        double tax = round2(subtotal * taxRate);
        double grandTotal = round2(subtotal + tax);

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Serialize concurrent checkouts so MAX(id)+1 can't collide (PostgreSQL syntax)
                try (Statement st = conn.createStatement()) {
                    st.execute("LOCK TABLE orders, order_items, order_item_toppings IN EXCLUSIVE MODE");
                }

                int orderId = Ids.nextId(conn, "orders");
                int orderItemId = Ids.nextId(conn, "order_items");
                int oitId = Ids.nextId(conn, "order_item_toppings");

                LocalDateTime now = LocalDateTime.now();
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO orders (id, employee_id, subtotal, tax, grandtotal, date, time) "
                      + "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                    ps.setInt(1, orderId);
                    ps.setInt(2, employeeId);
                    ps.setDouble(3, subtotal);
                    ps.setDouble(4, tax);
                    ps.setDouble(5, grandTotal);
                    ps.setDate(6, Date.valueOf(now.toLocalDate()));
                    ps.setTime(7, Time.valueOf(now.toLocalTime().withNano(0)));
                    ps.executeUpdate();
                }

                try (PreparedStatement psItem = conn.prepareStatement(
                         "INSERT INTO order_items (id, order_id, item_id, price_at_order, ice_level, sugar_level) "
                       + "VALUES (?, ?, ?, ?, ?, ?)");
                     PreparedStatement psTop = conn.prepareStatement(
                         "INSERT INTO order_item_toppings (id, order_item_id, topping_id, price_at_order) "
                       + "VALUES (?, ?, ?, ?)");
                     PreparedStatement psRecipe = conn.prepareStatement(
                         "UPDATE inventory SET stock = stock - "
                       + "(SELECT SUM(quantity) FROM recipes WHERE item_id = ? AND inventory_id = inventory.id) "
                       + "WHERE id IN (SELECT inventory_id FROM recipes WHERE item_id = ?)");
                     PreparedStatement psTopStock = conn.prepareStatement(
                         "UPDATE inventory SET stock = stock - (SELECT quantity_used FROM toppings WHERE id = ?) "
                       + "WHERE id = (SELECT inventory_id FROM toppings WHERE id = ?)")) {

                    for (OrderItem oi : order.orderItems()) {
                        int thisItemId = orderItemId++;
                        psItem.setInt(1, thisItemId);
                        psItem.setInt(2, orderId);
                        psItem.setInt(3, oi.item().id());
                        psItem.setDouble(4, oi.price());
                        psItem.setString(5, oi.iceLevel());
                        psItem.setString(6, oi.sugarLevel());
                        psItem.executeUpdate();

                        psRecipe.setInt(1, oi.item().id());
                        psRecipe.setInt(2, oi.item().id());
                        psRecipe.executeUpdate();

                        for (Topping t : oi.toppings()) {
                            psTop.setInt(1, oitId++);
                            psTop.setInt(2, thisItemId);
                            psTop.setInt(3, t.id());
                            psTop.setDouble(4, t.basePrice());
                            psTop.executeUpdate();

                            psTopStock.setInt(1, t.id());
                            psTopStock.setInt(2, t.id());
                            psTopStock.executeUpdate();
                        }
                    }
                }

                conn.commit();
                return new OrderReceipt(orderId, subtotal, tax, grandTotal);
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    /**
     * rounds number to two decimal places
     * @param v the number to be rounded
     * @return a two decimal place double
     */
    private static double round2(double v) { return Math.round(v * 100.0) / 100.0; }
}