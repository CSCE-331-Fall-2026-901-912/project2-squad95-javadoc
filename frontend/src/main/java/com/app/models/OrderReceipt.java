package com.app.models;

/**
 * simple record to represent a receipt, used for displaying information after checkout
 * 
 * @param orderId id of the order
 * @param subtotal subtotal of the order
 * @param tax tax applied to the order
 * @param grandTotal grand total of the order
 */
public record OrderReceipt(int orderId, double subtotal, double tax, double grandTotal) {}
