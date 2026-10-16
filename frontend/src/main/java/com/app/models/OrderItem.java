package com.app.models;

import java.util.*;
import java.util.stream.Collectors;
/**
 * represents an instance of a menu item being ordered
 * 
 * @param item the menu item ordered
 * @param iceLevel the ice level of the drink
 * @param sugarLevel the sugar level of the drink
 * @param toppings a list of toppings added to the drink
 */
public record OrderItem(Item item, String iceLevel, String sugarLevel, List<Topping> toppings) {
    /**
     * getter for the base price of the drink
     * @return the base price of the menu item
     */
    public double price() { return item.basePrice(); }

    /**
     * calculates the total price by adding the sum of the toppings to the base price of the drink
     * @return the total price
     */
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



