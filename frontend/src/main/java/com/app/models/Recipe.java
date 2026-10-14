package com.app.models;

import java.util.Map;

import com.app.models.Item;

public record Recipe(Item item, Map<InventoryItem, Integer> recipe) {}
