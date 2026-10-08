package com.app.models;

import javafx.collections.ObservableList;

public record Order(int id, ObservableList<OrderItem> orderItems) {}
