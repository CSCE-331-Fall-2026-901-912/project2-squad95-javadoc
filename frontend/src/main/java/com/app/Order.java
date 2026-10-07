package com.app;

import javafx.collections.ObservableList;

public record Order(int id, ObservableList<OrderItem> orderItems) {}
