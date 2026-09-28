from datetime import date, datetime, timedelta
import csv
import random

headers_drinks = ['id', 'name', 'base_price', 'stock', 'serving']
headers_toppings = ['id', 'name']
headers_modifiers = ['id', 'sugar_level', 'ice_level']
headers_days = ['id', 'date', 'sales']

data_days = [] # stores sale histories


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
    '',
    '',
]

menu_item_prices = []
# Placeholder prices, don't need to worry about more realistic pricing for now
for _ in range(0, len(menu_items)):
    drink_price = random.randint(4, 6) + 0.99
    menu_item_prices.append(drink_price)



day_cur = date(2024, 12, 31)
for i in range(0,365):
    data_days.append([])
    day_cur = day_cur + timedelta(days=1)
    day_cur_str = day_cur.strftime("%d/%m/%Y")
    data_days[i].append([i+1, day_cur_str, (i+1)*20])


# Need to make sure that the seeded data meets project requirements 
assert len(menu_items) >= 20
# print(len(menu_item_prices), len(menu_items))
assert len(menu_item_prices) == len(menu_items)
assert len(data_days) >= 52

total_sales_amount = 0
for i in range(0, 365):
    total_sales_amount += data_days[i][0][2]

print("Total sales is {:.2f}".format(total_sales_amount))
assert total_sales_amount >= 1_250_000


for row in data_days:
    for item in row:
        print(item)