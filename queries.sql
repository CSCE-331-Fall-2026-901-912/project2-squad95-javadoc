-- TODO: place names in <> with actual database names, verify correctness

-- Plain queries

--- Most popular items
SELECT m.name AS menu_item, 
       COUNT(oi.id) AS times_ordered 
FROM menu_items m 
JOIN order_items oi ON m.id = oi.item_id 
GROUP BY m.name 
ORDER BY times_ordered DESC;

--- Orders placed after 15:00
SELECT SUM(total) AS orders_after_3pm
FROM orders 
WHERE EXTRACT(HOUR FROM CAST(time AS time)) >= 15;

--- Employee with most orders
SELECT created_by AS employee_id, 
       COUNT(id) AS orders_made
FROM orders 
GROUP BY created_by 
ORDER BY orders_made DESC;

--- Top 10 orders by total
SELECT id AS order_id, 
       order_time, 
       total 
FROM orders 
ORDER BY total DESC 
LIMIT 10;

--- Bottom 10 orders by total
SELECT id AS order_id, 
       order_time, 
       total 
FROM orders 
ORDER BY total ASC 
LIMIT 10;



-- TODO



-- Special queries ---

-- 1. Weekly sales history
SELECT COUNT(<id>) FROM <order_history>
ORDER BY <week>

-- 2. Peak sales day
-- TODO: verify TOP <num> syntax support by our database
SELECT TOP 10 SUM(<order_total>), <day> 
FROM <order_history>
GROUP BY <day>

-- 3. Realistic Sales History
SELECT COUNT(<id>), SUM(order_total), <hour> 
FROM <order_history>
GROUP BY <hour>

-- 4. Menu Item Inventory
SELECT SUM(<stock>), <menu-item> FROM <menu>
GROUP BY <menu-item>

-- 5. Best of the Worst
-- Optional, but counts towards required total queries
SELECT <order_total>, <day>, <menu-item>
FROM <order_history>, <menu>
WHERE <order_total> = MIN(<order_total>)