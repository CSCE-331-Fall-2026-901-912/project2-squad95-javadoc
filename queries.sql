-- Plain queries

--- 1. Most popular items
SELECT m.name AS menu_item, 
       COUNT(oi.id) AS times_ordered 
FROM menu_items m 
JOIN order_items oi ON m.id = oi.item_id 
GROUP BY m.name 
ORDER BY times_ordered DESC;

--- 2. Least popular items
SELECT m.name AS menu_item, 
       COUNT(oi.id) AS times_ordered 
FROM menu_items m 
JOIN order_items oi ON m.id = oi.item_id 
GROUP BY m.name 
ORDER BY times_ordered ASC;

--- 3. Orders placed after 15:00
SELECT SUM(total) AS orders_after_3pm
FROM orders 
WHERE EXTRACT(HOUR FROM CAST(time AS time)) >= 15;

--- 4. Employee with most orders
SELECT created_by AS employee_id, 
       COUNT(id) AS orders_made
FROM orders 
GROUP BY created_by 
ORDER BY orders_made DESC;

--- 5. Top 10 orders by total
SELECT id AS order_id, 
       order_time, 
       total 
FROM orders 
ORDER BY total DESC 
LIMIT 10;

--- 6. Bottom 10 orders by total
SELECT id AS order_id, 
       order_time, 
       total 
FROM orders 
ORDER BY total ASC 
LIMIT 10;

--- 7. Most Common Drink size
SELECT size, 
       COUNT(id) AS times_ordered
FROM order_items
GROUP BY size
ORDER BY times_ordered DESC;

--- 8. Least Common Drink size
SELECT size,        
       COUNT(id) AS times_ordered
FROM order_items
GROUP BY size
ORDER BY times_ordered ASC;

--- 9. Monthly Revenue
SELECT EXTRACT(MONTH FROM CAST(date AS date)) AS month,
       SUM(totals) AS monthly_revenue
FROM orders
GROUP BY EXTRACT(MONTH FROM CAST(date AS date))
ORDER BY month;

--- 10. Orders placed before 3pm, in case we want to run lunch specials like many Asian shops
SELECT SUM(total) AS orders_before_3pm
FROM orders 
WHERE EXTRACT(HOUR FROM CAST(time AS time)) < 15;

-- 11. Ordered drinks with toppings
SELECT item_id FROM orders_items
INNER JOIN order_item_toppings
    ON order_item_toppings.order_item_id == order_items.item_id;

-- 12. Days where sales exceeded 5k, in descending order
SELECT date, sales FROM sale_history
WHERE sales > 5000
ORDER BY sales DESC;

-- 13. Count of orders where totals exceeded 10
SELECT COUNT(*) FROM orders
WHERE totals > 10
ORDER BY totals DESC;

-- 14. Order items with a 'Large' size
SELECT orders.id, order_items.size FROM orders
INNER JOIN order_items
    ON orders.id = order_items.order_id
WHERE order_items.size = 'Large';


-- Special queries ---

-- 1. Weekly sales history
-- can select by using COUNT() instead if 
-- an order had a date associated with it
SELECT COUNT(id), 
       date
FROM orders
GROUP BY EXTRACT(WEEK FROM time);

-- 2. Peak sales day
-- given a specific day, what was the sum 
-- of the top 10 order totals?
SELECT SUM(totals) AS order_totals, 
       DATE(date) as order_date
FROM orders
GROUP BY DATE(date)
ORDER BY order_totals DESC
LIMIT 10;

-- 3. Realistic Sales History
SELECT COUNT(id), 
       SUM(totals) AS order_totals,
       EXTRACT(HOUR FROM time) as order_hour
FROM orders
GROUP BY EXTRACT(HOUR FROM time);
ORDER BY order_hour

-- 4. Menu Item Inventory
-- TODO: what do I select for inventory items
-- select count of inventory items from inventory 
-- and menu grouped by menu item
SELECT SUM(<stock>), <menu-item> FROM <menu>
GROUP BY <menu-item>

-- 5. Best of the Worst
-- Optional, but counts towards required total queries
-- given a specific week, what day had the lowest sales, 
-- what were the sales numbers, 
-- and what was the top seller that day?

-- SELECT SUM(totals) as o_totals_sum, 
--        DATE(date) as order_date,
--        EXTRACT(WEEK FROM date) as week 
-- FROM orders
-- GROUP BY EXTRACT(WEEK FROM date)
-- ORDER BY o_totals_sum ASC
-- INNER JOIN items ON 
-- WHERE o_totals_sum = MIN(<order_total>) 

WITH weekly_orders AS (
       SELECT SUM(totals) AS o_totals_sum,
              DATE(date) as order_date
       FROM orders
       GROUP BY EXTRACT(WEEK FROM order_date)
),

lowest_sales_day AS (

),
