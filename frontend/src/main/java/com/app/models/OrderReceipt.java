package com.app.models;

public record OrderReceipt(int orderId, double subtotal, double tax, double grandTotal) {}
