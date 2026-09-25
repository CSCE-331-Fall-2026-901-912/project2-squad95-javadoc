-- TODO: place names in <> with actual database names, verify correctness

-- 1. Weekly sales history
SELECT COUNT(<id>) FROM <order_history>
ORDER BY <week>

-- 2. Peak sales day
-- TODO: verify TOP <num> syntax support by our database
SELECT TOP 10 SUM(<order_total>), <days> 
FROM <order_history>
GROUP BY <days>