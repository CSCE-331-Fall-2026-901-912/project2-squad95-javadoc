package com.app.models;

import javafx.collections.ObservableList;

/**
 * the order class represents an order in the frontend containing all of the associated items in the order
 * 
 */
public class Order {
    private int id; 
    private ObservableList<OrderItem> orderItems;
    public String notes;

    /**
     * generic constructor
     * @param id 
     * @param orderItems
     */
    public Order(int id, ObservableList<OrderItem> orderItems) {
        this.id = id;
        this.orderItems = orderItems;
    }

    /**
     * generic getter
     * @return returns id for the order
     */
    public int id() { return id; }
    /**
     * generic getter
     * @return returns list of order items for the order
     */
    public ObservableList<OrderItem> orderItems() { return orderItems; }
    /**
     * generic getter
     * @return returns the notes for the order
     */
    public String notes() { return notes; }

    public String toString() {
        return String.valueOf(id);
    }
}
