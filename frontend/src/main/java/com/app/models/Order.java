package com.app.models;

import javafx.collections.ObservableList;

public class Order {

    private int id; 
    private ObservableList<OrderItem> orderItems; 
    public String notes;

    public Order(int id, ObservableList<OrderItem> orderItems) {
        this.id = id;
        this.orderItems = orderItems;
    }

    public int id() { return id; }
    public ObservableList<OrderItem> orderItems() { return orderItems; }
    public String notes() { return notes; }

    public String toString() {
        return String.valueOf(id);
    }
}
