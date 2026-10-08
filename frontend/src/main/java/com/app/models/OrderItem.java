package com.app.models;

public record OrderItem(String name) { 
    public String toString() {
        return name;
    }
}