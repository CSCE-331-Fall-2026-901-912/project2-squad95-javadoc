from datetime import date, datetime, timedelta
import csv
import random

headers_drinks = ['id', 'name', 'base_price', 'stock', 'serving']
headers_toppings = ['id', 'name']
headers_modifiers = ['id', 'sugar_level', 'ice_level']
headers_days = ['id', 'date', 'sales']
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
    '',
    '',
]
menu_item_prices = [4.99, 4.99, 4.99, 6]


day_cur = date(2024, 12, 31)
for i in range(1,366):
    data_days.append([])
    day_cur = day_cur + timedelta(days=1)
    data_days[i].append([i, day_cur, i*10])

for row in data_days:
    for item in row:
        print(item)