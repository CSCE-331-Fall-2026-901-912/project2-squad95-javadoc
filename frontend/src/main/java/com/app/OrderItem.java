package com.app;

public record OrderItem( String name /* TODO: data for individual items here */ ) { 
    public String toString() {
        return name;
    }
}