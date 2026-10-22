-- Plain queries

--- 1. Most popular items
SELECT m.name AS menu_item, 
       COUNT(oi.id) AS times_ordered 
FROM items m 
JOIN order_items oi ON m.id = oi.item_id 
GROUP BY m.name 
ORDER BY times_ordered DESC;

--- 2. Least popular items
SELECT m.name AS menu_item, 
       COUNT(oi.id) AS times_ordered 
FROM items m 
JOIN order_items oi ON m.id = oi.item_id 
GROUP BY m.name 
ORDER BY times_ordered ASC;

--- 3. Orders placed after 15:00
SELECT SUM(subtotal) AS orders_after_3pm
FROM orders 
WHERE EXTRACT(HOUR FROM CAST(time AS time)) >= 15;

--- 4. Employee with most orders
SELECT e.name AS employee_name, 
       COUNT(o.id) AS orders_made
FROM orders o
JOIN employees e ON o.employee_id = e.id
GROUP BY e.id, e.name
ORDER BY orders_made DESC;

--- 5. Top 10 orders by total
SELECT id AS order_id, 
       time, 
       subtotal 
FROM orders 
ORDER BY subtotal DESC 
LIMIT 10;

--- 6. Bottom 10 orders by total
SELECT id AS order_id, 
       time, 
       subtotal 
FROM orders 
ORDER BY subtotal ASC 
LIMIT 10;

--- 7. Most Popular Toppings
SELECT t.name AS topping_name, 
       COUNT(oit.id) AS times_ordered
FROM toppings t
JOIN order_item_toppings oit ON t.id = oit.topping_id
GROUP BY t.name
ORDER BY times_ordered DESC;

--- 8. Highest Grossing Menu Items
SELECT i.name AS menu_item, 
       SUM(oi.price_at_order) AS total_revenue_generated
FROM items i
JOIN order_items oi ON i.id = oi.item_id
GROUP BY i.name
ORDER BY total_revenue_generated DESC
LIMIT 10; 

--- 9. Monthly Revenue
SELECT EXTRACT(MONTH FROM CAST(date AS date)) AS month,
       SUM(subtotal) AS monthly_revenue
FROM orders
GROUP BY EXTRACT(MONTH FROM CAST(date AS date))
ORDER BY month;

--- 10. Orders placed before 3pm, in case we want to run lunch specials like many Asian shops
SELECT COUNT(id) AS total_orders_before_3pm
FROM orders 
WHERE EXTRACT(HOUR FROM CAST(time AS time)) < 15;

--- 11. Total drinks with toppings
SELECT COUNT(DISTINCT order_items.id) as total_ordered_items_with_toppings
FROM order_items
INNER JOIN order_item_toppings
    ON order_item_toppings.order_item_id = order_items.item_id;

--- 12. Days where sales exceeded 3k, in descending order
SELECT date, SUM(subtotal) AS sales 
FROM orders
GROUP BY date
HAVING SUM(subtotal) > 3000
ORDER BY sales DESC;

--- 13. Count of orders where totals exceeded $10
SELECT COUNT(*) AS big_orders
FROM orders
WHERE subtotal > 10;

--- 14. Total Sales by day of week
SELECT CASE EXTRACT(DOW FROM CAST(date AS date))
           WHEN 0 THEN 'Sunday'
           WHEN 1 THEN 'Monday'
           WHEN 2 THEN 'Tuesday'
           WHEN 3 THEN 'Wednesday'
           WHEN 4 THEN 'Thursday'
           WHEN 5 THEN 'Friday'
           WHEN 6 THEN 'Saturday'
       END AS day_of_week, 
       COUNT(id) AS total_orders,
       SUM(subtotal) AS total_revenue
FROM orders
GROUP BY EXTRACT(DOW FROM CAST(date AS date))
ORDER BY total_revenue DESC;

--- 15. Total tax paid
SELECT SUM(tax) AS total_tax
FROM orders;


-- Special queries ---

-- 1. Weekly sales history
-- count of orders grouped by week
SELECT EXTRACT(week FROM CAST(date AS date)) AS week_num,
       COUNT(id) AS total_orders,
       SUM(subtotal) AS total_revenue
FROM orders
GROUP BY EXTRACT(week FROM CAST(date AS date))
ORDER BY week_num ASC;

-- 2. Peak sales day
-- sum of the top 10 order totals group by day
SELECT SUM(subtotal) AS order_totals, 
       DATE(date) as order_date
FROM orders
GROUP BY DATE(date)
ORDER BY order_totals DESC
LIMIT 10;

-- 3. Realistic Sales History
-- count of orders and sum of order totals grouped by hour
SELECT COUNT(id), 
       SUM(subtotal) AS order_totals,
       EXTRACT(HOUR FROM time) as order_hour
FROM orders
GROUP BY EXTRACT(HOUR FROM time)
ORDER BY order_hour;

-- 4. Menu Item Inventory
-- count of inventory items and menu grouped by menu item
SELECT items.id,
       items.name,
       COUNT(recipes.inventory_id) as total_inventory
FROM items
LEFT JOIN recipes
    ON items.id = recipes.item_id
GROUP BY items.id,
         items.name
ORDER BY total_inventory DESC;
