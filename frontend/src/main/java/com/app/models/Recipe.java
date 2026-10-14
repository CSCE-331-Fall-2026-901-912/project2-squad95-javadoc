package com.app.models;

import java.util.Map;

public record Recipe(int itemId, Map<InventoryItem, Integer> recipe) {}
