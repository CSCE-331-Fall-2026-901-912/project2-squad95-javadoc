package com.app.models;

import java.util.*;
import java.util.stream.Collectors;

public record OrderItem(Item item, String iceLevel, String sugarLevel, List<Topping> toppings) {
    public double price() { return item.basePrice(); }

    public double totalPrice() {
        return price() + toppings.stream().mapToDouble(Topping::basePrice).sum();
    }

    @Override public String toString() {
        String tops = toppings.isEmpty() ? "" :
            " + " + toppings.stream().map(Topping::name).collect(Collectors.joining(", "));
        return String.format("%s (%s sugar, %s)%s  $%.2f",
            item.name(), sugarLevel, iceLevel, tops, totalPrice());
    }
}



