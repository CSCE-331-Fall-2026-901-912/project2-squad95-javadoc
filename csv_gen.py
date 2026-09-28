from datetime import date, datetime, timedelta
import csv
import random

# csv file headers
headers_drinks = ['id', 'name', 'base_price', 'stock', 'serving']
headers_toppings = ['id', 'name']
headers_modifiers = ['id', 'sugar_level', 'ice_level']
headers_days = ['id', 'date', 'sales']
headers_orders = ['id', 'menu_item', 'totals']
headers_order_items = ['item_id', 'order_id', 'price_at_order', '']
data_days = [[]]



menu_items = [
    'Classic Pearl Milk Tea', 
    'Classic Green Tea', 
    'Classic Black Tea',
    'Classic Oolong Tea',
    'Honey Green Tea',
    'Honey Black Tea',
    'Honey Oolong Tea',
    'Honey Pearl Milk Tea',
    'Mango Green Milk Tea',
    'Coffee Creama',
    'Matcha Pearl Milk Tea'
    'Coffee Milk Tea',
    'Taro Pearl Milk Tea',
    'Thai Pearl Milk Tea',
    'Coconut Pearl Milk Tea'
    'Mango Green Tea',
    'Passion Chess',
    'Berry Lychee Burst',
    'Peach Tea',
    'Honey Lemonade',
    'Strawberry Coconut',
    'Matcha Fresh Milk',
]
menu_item_prices = [4.99, 4.99, 4.99, 6]


# Target $1,000,000 total sales over 364 days -> ~$2,747 per day on average
# for a avg $10 order, we need to sell 274.7 avg orders per day


day_cur = date(2024, 12, 31)
for i in range(1,366):
    data_days.append([])
    day_cur = day_cur + timedelta(days=1)
    data_days[i].append([i, day_cur, i*10])

for row in data_days:
    for item in row:
        print(item)


# Verify required amounts of data
assert len(menu_items) >= 20
assert len(menu_item_prices) == len(menu_items)
