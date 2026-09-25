-- TODO: place names in <> with actual database names, verify correctness

-- Plain queries

-- TODO

-- Special queries

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