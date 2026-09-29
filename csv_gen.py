from datetime import date, datetime, timedelta
import csv
import random

# csv file headers
headers_items = ['id', 'name', 'base_price', 'stock', 'serving']
headers_toppings = ['id', 'name']
headers_modifiers = ['id', 'sugar_level', 'ice_level']
headers_days = ['id', 'date', 'sales']
headers_orders = ['id', 'menu_item', 'totals']
headers_order_items = ['id', 'order_id', 'item_id', 'price_at_order', 'size', '']
headers_order_item_toppings = ['id', 'order_item_id', 'topping_id']
headers_utensils = ['id', 'name', 'stock']
headers_order_utensils = ['id', 'order_id', '']
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
menu_item_prices = []
# Placeholder prices, don't need to worry about more realistic pricing for now
for _ in range(0, len(menu_items)):
    drink_price = random.randint(4, 6) + 0.99
    menu_item_prices.append(drink_price)



# Target $1,000,000 total sales over 364 days -> ~$2,747 per day on average
# for a avg $10 order, we need to sell 274.7 avg orders per day


day_cur = date(2024, 12, 31)
for i in range(0,365):
    data_days.append([])
    day_cur = day_cur + timedelta(days=1)
    day_cur_str = day_cur.strftime("%d/%m/%Y")
    data_days[i].append([i+1, day_cur_str, (i+1)*20])

peak_days = [20, 141, 144, 331]
for i in peak_days:
    # 1_500_000 is an "okay" base number for making up a peak sales amount
    peak_sales_amount = 1_500_000 * random.uniform(0.0075, 0.0150)
    peak_sales_amount = round(peak_sales_amount, 2)
    data_days[i][0][2] = peak_sales_amount

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

assert len(peak_days) >= 3
for i in peak_days:
    peak_day_sales = data_days[i][0][2]
    is_higher_than_req = peak_day_sales > (0.0025 * 1_250_000)
    print("{:.2f} higher than 0.0025 * 1_250_000: {}".format(peak_day_sales, is_higher_than_req))
    assert is_higher_than_req

for row in data_days:
    for item in row:
        print(item)


# Verify required amounts of data
assert len(menu_items) >= 20
assert len(menu_item_prices) == len(menu_items)
