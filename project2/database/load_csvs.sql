DROP TABLE items;
DROP TABLE employees;
DROP TABLE toppings;
DROP TABLE order_items;
DROP TABLE order_item_toppings;
DROP TABLE inventory;
DROP TABLE utensils;
DROP TABLE recipes;
DROP TABLE orders;
CREATE TABLE items
(
    id INT PRIMARY KEY,
    name VARCHAR(255),
    base_price DECIMAL
);
CREATE TABLE employees
(
    id INT PRIMARY KEY,
    name VARCHAR(255),
    role VARCHAR(255)
);
CREATE TABLE inventory
(
    id INT PRIMARY KEY,
    name VARCHAR(255),
    unit VARCHAR(255),
    stock int
);
CREATE TABLE toppings
(
    id INT PRIMARY KEY,
    name VARCHAR(255),
    base_price DECIMAL,
    inventory_id INT,
    quantity_used INT
);
CREATE TABLE utensils
(
    id INT PRIMARY KEY,
    name VARCHAR(255)
);
CREATE TABLE orders
(
    id INT PRIMARY KEY,
    employee_id INT,
    subtotal DECIMAL,
    tax DECIMAL,
    grandtotal DECIMAL,
    date DATE,
    time TIME
);
CREATE TABLE order_items
(
    id INT PRIMARY KEY,
    order_id INT,
    item_id INT,
    price_at_order DECIMAL,
    ice_level VARCHAR(255),
    sugar_level VARCHAR(255)
);
CREATE TABLE order_item_toppings
(
    id INT PRIMARY KEY,
    order_item_id INT,
    topping_id INT,
    price_at_order DECIMAL
);
CREATE TABLE recipes
(
    id INT PRIMARY KEY,
    item_id INT,
    inventory_id INT,
    quantity INT
);
\copy items from 'items.csv' CSV HEADER
\copy employees from 'employees.csv' CSV HEADER
\copy toppings from 'toppings.csv' CSV HEADER
\copy utensils from 'utensils.csv' CSV HEADER
\copy inventory from 'inventory.csv' CSV HEADER
\copy recipes from 'recipes.csv' CSV HEADER
\copy order_items from 'order_items.csv' CSV HEADER
\copy order_item_toppings from 'order_item_toppings.csv' CSV HEADER
\copy orders from 'orders.csv' CSV HEADER
