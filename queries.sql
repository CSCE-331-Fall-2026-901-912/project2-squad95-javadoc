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
-- count of orders grouped by week
SELECT COUNT(id), 
       date
FROM orders
GROUP BY EXTRACT(WEEK FROM time);

-- 2. Peak sales day
-- sum of the top 10 order totals group by day
SELECT SUM(totals) AS order_totals, 
       DATE(date) as order_date
FROM orders
GROUP BY DATE(date)
ORDER BY order_totals DESC
LIMIT 10;

-- 3. Realistic Sales History
-- count of orders and sum of order totals grouped by hour
SELECT COUNT(id), 
       SUM(totals) AS order_totals,
       EXTRACT(HOUR FROM time) as order_hour
FROM orders
GROUP BY EXTRACT(HOUR FROM time);
ORDER BY order_hour

-- 4. Menu Item Inventory
-- count of inventory items and menu grouped by menu item
SELECT items.id,
       items.name,
       COUNT(recipes.inventory_item_id) as total_inventory
FROM items
LEFT JOIN recipe 
    ON items.id = recipe.item_id
GROUP BY items.id,
         items.name

-- 5. Best of the Worst
-- Optional, but counts towards required total queries
"get the lowest sales day from orders grouped by weeks, 
then get the top selling item from the lowest sales day,
and finally display the sales of lowest sales day and
the top selling item"

SELECT lowest_sales_day.order_date,
       lowest_sales_day.total_sales,
       items.name AS item_name,
       COUNT(order_items.id) AS quantity_sold
FROM (
    SELECT DATE(date) AS order_date, SUM(totals) AS total_sales
    FROM orders
    GROUP BY order_date
    ORDER BY total_sales ASC
    LIMIT 1
) lowest_sales_day
JOIN order_items ON order_items.order_id IN (
    SELECT id FROM orders WHERE DATE(date) = lowest_sales_day.order_date
)
JOIN items ON items.id = order_items.item_id
GROUP BY lowest_sales_day.order_date, 
         lowest_sales_day.total_sales, 
         items.id, 
         items.name
ORDER BY quantity_sold DESC
LIMIT 1;
