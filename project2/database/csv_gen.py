from datetime import date, datetime, timedelta, time
import csv
import random 

headers_items = ['id', 'name', 'base_price']
headers_toppings = ['id', 'name', 'base_price', 'inventory_item_id', 'quantity_used']
headers_orders = ['id', 'employee_id', 'subtotal', 'tax', 'grand_total', 'date', 'time']
headers_order_items = ['id', 'order_id', 'item_id', 'price_at_order', 'ice_level', 'sugar_level']
headers_order_item_toppings = ['id', 'order_item_id', 'topping_id', 'price_at_order']
headers_utensils = ['id', 'name']
headers_employees = ['id', 'name', 'role']
headers_inventory = ['id', 'name', 'unit', 'stock']
headers_recipes = ['id', 'item_id', 'inventory_item_id', 'quantity']
data_items = []
data_toppings = []
data_orders = []
data_order_items = []
data_order_item_toppings = []
data_utensils = []
data_inventory = []
data_recipes = []

data_employees = [[0, 'John Smith', 'Manager'],
                  [1, 'Jane Doe', 'Cashier'],
                  [2, 'Mary Richards', 'Cashier']]

toppings = [
    "Pearls (Boba)", 
    "Coffee Jelly", 
    "Pudding", 
    "Lychee Jelly", 
    "Honey Jelly", 
    "Crystal Boba", 
    "Mango Popping Boba", 
    "Strawberry Popping Boba",
    "Ice Cream", 
    "Creama"
]
topping_prices = [
    0.75,
    0.75,
    0.75,
    0.75,
    0.75,
    1.0,
    1.0,
    1.0,
    1.0,
    1.0
]
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
    'Matcha Pearl Milk Tea',
    'Coffee Milk Tea',
    'Taro Pearl Milk Tea',
    'Thai Pearl Milk Tea',
    'Coconut Pearl Milk Tea',
    'Mango Green Tea',
    'Passion Chess',
    'Berry Lychee Burst',
    'Peach Tea',
    'Honey Lemonade',
    'Strawberry Coconut',
    'Matcha Fresh Milk',
]
item_prices = [
    5.75,
    6.50,
    6.00,
    6.95,
    6.75,
    6.75,
    6.95,
    5.80,
    6.25,
    6.25,
    5.25,
    6.75,
    7.75,
    7.95,
    6.50,
    6.75,
    7.25,
    6.75,
    6.50,
    6.65,
    7.25,
    7.25
]
base_items = [
    ('Black Tea Leaves',    'g',  2000),
    ('Green Tea Leaves',    'g',  2000),
    ('Oolong Tea Leaves',   'g',  2000),
    ('Matcha Powder',       'g',  1500),
    ('Thai Tea Mix',        'g',  1500),
    ('Taro Powder',         'g',  2000),
    ('Coffee Concentrate',  'ml', 5000),
    ('Milk',                'ml', 25000),
    ('Coconut Milk',        'ml', 8000),
    ('Simple Syrup',        'ml', 15000),
    ('Honey',               'ml', 8000),
    ('Ice',                 'g',  100000),
    ('Mango Puree',         'ml', 6000),
    ('Passion Fruit Puree', 'ml', 5000),
    ('Berry Puree',         'ml', 5000),
    ('Strawberry Puree',    'ml', 5000),
    ('Lychee Syrup',        'ml', 5000),
    ('Peach Syrup',         'ml', 5000),
    ('Lemon Juice',         'ml', 5000),
    ('Tapioca Pearls',           'g', 8000),
    ('Coffee Jelly',             'g', 4000),
    ('Pudding',                  'g', 4000),
    ('Lychee Jelly',             'g', 4000),
    ('Honey Jelly',              'g', 4000),
    ('Crystal Boba',             'g', 4000),
    ('Mango Popping Boba',       'g', 4000),
    ('Strawberry Popping Boba',  'g', 4000),
    ('Ice Cream',                'g', 5000),
    ('Creama Foam',              'g', 5000),
    ('Cup',                      'unit', 500),
    ('Straw',                    'unit', 500),
    ('Lid',                      'unit', 500),
    ('Napkin',                   'unit', 500),
    ('Tray',                     'unit', 500),
]
utensil_names = [
    'Cup',
    'Straw',
    'Lid',
    'Napkin',
    'Tray'
]
index = 0
for utensil_name in utensil_names:
    data_utensils.append([])
    data_utensils[index] = [index, utensil_name]
    index += 1
topping_inventory_names = [
    'Tapioca Pearls',
    'Coffee Jelly',
    'Pudding',
    'Lychee Jelly',
    'Honey Jelly',
    'Crystal Boba',
    'Mango Popping Boba',
    'Strawberry Popping Boba',
    'Ice Cream',
    'Creama Foam',
]
topping_quantities = [50, 50, 50, 50, 50, 50, 40, 40, 60, 40]

recipes = {
    'Classic Pearl Milk Tea': {'Black Tea Leaves': 4, 'Milk': 60, 'Simple Syrup': 30, 'Ice': 150, 'Tapioca Pearls': 50, 'Cup': 1, 'Straw': 1, 'Lid': 1, 'Napkin': 1},
    'Classic Green Tea':      {'Green Tea Leaves': 4, 'Simple Syrup': 30, 'Ice': 150, 'Cup': 1, 'Straw': 1, 'Lid': 1, 'Napkin': 1},
    'Classic Black Tea':      {'Black Tea Leaves': 4, 'Simple Syrup': 30, 'Ice': 150, 'Cup': 1, 'Straw': 1, 'Lid': 1, 'Napkin': 1},
    'Classic Oolong Tea':     {'Oolong Tea Leaves': 4, 'Simple Syrup': 30, 'Ice': 150, 'Cup': 1, 'Straw': 1, 'Lid': 1, 'Napkin': 1},
    'Honey Green Tea':        {'Green Tea Leaves': 4, 'Honey': 35, 'Ice': 150, 'Cup': 1, 'Straw': 1, 'Lid': 1, 'Napkin': 1},
    'Honey Black Tea':        {'Black Tea Leaves': 4, 'Honey': 35, 'Ice': 150, 'Cup': 1, 'Straw': 1, 'Lid': 1, 'Napkin': 1},
    'Honey Oolong Tea':       {'Oolong Tea Leaves': 4, 'Honey': 35, 'Ice': 150, 'Cup': 1, 'Straw': 1, 'Lid': 1, 'Napkin': 1},
    'Honey Pearl Milk Tea':   {'Black Tea Leaves': 4, 'Milk': 60, 'Honey': 35, 'Ice': 150, 'Tapioca Pearls': 50, 'Cup': 1, 'Straw': 1, 'Lid': 1, 'Napkin': 1},
    'Mango Green Milk Tea':   {'Green Tea Leaves': 4, 'Milk': 60, 'Mango Puree': 50, 'Simple Syrup': 20, 'Ice': 150, 'Cup': 1, 'Straw': 1, 'Lid': 1, 'Napkin': 1},
    'Coffee Creama':          {'Coffee Concentrate': 60, 'Simple Syrup': 25, 'Ice': 150, 'Creama Foam': 40, 'Cup': 1, 'Straw': 1, 'Lid': 1, 'Napkin': 1},
    'Matcha Pearl Milk Tea':  {'Matcha Powder': 6, 'Milk': 120, 'Simple Syrup': 25, 'Ice': 150, 'Tapioca Pearls': 50, 'Cup': 1, 'Straw': 1, 'Lid': 1, 'Napkin': 1},
    'Coffee Milk Tea':        {'Black Tea Leaves': 4, 'Coffee Concentrate': 40, 'Milk': 60, 'Simple Syrup': 30, 'Ice': 150, 'Cup': 1, 'Straw': 1, 'Lid': 1, 'Napkin': 1},
    'Taro Pearl Milk Tea':    {'Taro Powder': 30, 'Milk': 100, 'Simple Syrup': 20, 'Ice': 150, 'Tapioca Pearls': 50, 'Cup': 1, 'Straw': 1, 'Lid': 1, 'Napkin': 1},
    'Thai Pearl Milk Tea':    {'Thai Tea Mix': 6, 'Milk': 80, 'Simple Syrup': 35, 'Ice': 150, 'Tapioca Pearls': 50, 'Cup': 1, 'Straw': 1, 'Lid': 1, 'Napkin': 1},
    'Coconut Pearl Milk Tea': {'Black Tea Leaves': 4, 'Coconut Milk': 60, 'Milk': 40, 'Simple Syrup': 30, 'Ice': 150, 'Tapioca Pearls': 50, 'Cup': 1, 'Straw': 1, 'Lid': 1, 'Napkin': 1},
    'Mango Green Tea':        {'Green Tea Leaves': 4, 'Mango Puree': 60, 'Simple Syrup': 15, 'Ice': 150, 'Cup': 1, 'Straw': 1, 'Lid': 1, 'Napkin': 1},
    'Passion Chess':          {'Green Tea Leaves': 4, 'Passion Fruit Puree': 50, 'Simple Syrup': 20, 'Ice': 150, 'Creama Foam': 40, 'Cup': 1, 'Straw': 1, 'Lid': 1, 'Napkin': 1},
    'Berry Lychee Burst':     {'Green Tea Leaves': 4, 'Lychee Syrup': 40, 'Berry Puree': 40, 'Ice': 150, 'Cup': 1, 'Straw': 1, 'Lid': 1, 'Napkin': 1},
    'Peach Tea':              {'Black Tea Leaves': 4, 'Peach Syrup': 45, 'Ice': 150, 'Cup': 1, 'Straw': 1, 'Lid': 1, 'Napkin': 1},
    'Honey Lemonade':         {'Honey': 35, 'Lemon Juice': 40, 'Ice': 150, 'Cup': 1, 'Straw': 1, 'Lid': 1, 'Napkin': 1},
    'Strawberry Coconut':     {'Strawberry Puree': 50, 'Coconut Milk': 80, 'Simple Syrup': 20, 'Ice': 150, 'Cup': 1, 'Straw': 1, 'Lid': 1, 'Napkin': 1},
    'Matcha Fresh Milk':      {'Matcha Powder': 6, 'Milk': 200, 'Simple Syrup': 20, 'Ice': 150, 'Cup': 1, 'Straw': 1, 'Lid': 1, 'Napkin': 1},
}

index = 0
for name in menu_items:
    data_items.append([])
    data_items[index] = [index,name,item_prices[index]]
    index += 1

inventory_ids = {}
for index, (name, unit, starting_stock) in enumerate(base_items):
    inventory_ids[name] = index
    data_inventory.append([index, name, unit, starting_stock])

index = 0
for topping in toppings:
    data_toppings.append([])
    data_toppings[index] = [index, topping, topping_prices[index], 
                            inventory_ids[topping_inventory_names[index]], 
                            topping_quantities[index]]

    index += 1


recipe_lookup = {}
for item_id, item_name in enumerate(menu_items):
    recipe_lookup[item_id] = []
    for ingredient, quantity in recipes[item_name].items():
        inventory_id = inventory_ids[ingredient]
        data_recipes.append([len(data_recipes), item_id, inventory_id, quantity])
        recipe_lookup[item_id].append((inventory_id, quantity))

def generate_peak_days():
    days = [date(2025, 8, 20), date(2025, 12, 25), date(2025, 10, 31), date(2025, 4, 30)]
    DESIRED_PEAK_SALES = 3000
    total_day_sales = 0
    for day in days:
        total_day_sales = 0
        while total_day_sales < DESIRED_PEAK_SALES:
            total_day_sales += generate_random_order(day)
        

def consume_inventory(inventory_id, quantity):
    threshold = 500
    restock_amount = 25000
    if data_inventory[inventory_id][3] - quantity < threshold:
        data_inventory[inventory_id][3] += restock_amount
    data_inventory[inventory_id][3] -= quantity
    return True

def consume_recipe(item_id):
    for inventory_id, quantity in recipe_lookup[item_id]:
        consume_inventory(inventory_id, quantity)

def generate_orders():
    DESIRED_TOTAL_SALES = 1000000.0
    total_sales = 0
    while (total_sales < DESIRED_TOTAL_SALES):
        total_sales += generate_random_order()
    generate_peak_days()

def generate_random_date():
    start_date = datetime(2025, 1, 1)
    end_date = datetime(2025, 12, 31)
    time_between_dates = end_date - start_date
    days_between_dates = time_between_dates.days
    
    random_number_of_days = random.randrange(days_between_dates + 1)
    random_date = start_date + timedelta(days=random_number_of_days)
    
    return random_date

def generate_random_time():
    start_time = time(11, 0, 0)
    end_time = time(23, 0, 0)
    start_secs = start_time.hour * 3600
    end_secs = end_time.hour * 3600
    secs_between_times = end_secs - start_secs

    random_number_of_secs = random.randrange(secs_between_times)
    random_time = (
        datetime.combine(datetime.today(), start_time)
        + timedelta(seconds=random_number_of_secs)
    ).time()
    return random_time

def generate_random_order(day = None):
    data_orders.append([])
    i = len(data_orders) - 1
    
    weights_employees = [20, 40, 40]
    employee_id = random.choices(range(3), weights=weights_employees)[0]
    
    weights_num_items = [50, 25, 12, 7, 3, 2, 1, 1, 1, 1]
    num_items = random.choices(range(1, 11), weights=weights_num_items)[0]
    items_total = 0
    for _ in range(num_items):
        items_total += generate_random_order_item(i)
        
    order_date = generate_random_date() if day == None else day
    order_time = generate_random_time()

    subtotal = round(items_total, 2) # revenue
    tax_amount = round(subtotal * 0.0625, 2) # texas sales tax
    grand_total = round(subtotal + tax_amount, 2) # total customer pays

    if num_items >= 3:  # order has 3+ items it will use a tray
        consume_inventory(inventory_ids['Tray'], 1)

    data_orders[i] = [i, employee_id, subtotal, tax_amount, grand_total, order_date, order_time]

    return subtotal

def generate_random_order_item(order_id):
    data_order_items.append([])
    i = len(data_order_items) - 1

    weights_num_toppings = [36, 54, 16, 13, 8, 2]
    num_toppings = random.choices(range(6), weights=weights_num_toppings)[0]
    toppings_total = 0
    for _ in range(num_toppings):
        toppings_total += generate_random_order_item_topping(i)

    weights_item = [300, 190, 195, 200, 30, 20, 15, 20, 40, 35, 20, 50, 40, 25, 30, 10, 60, 40, 50, 55, 30, 30]
    item_id = random.choices(range(22), weights=weights_item)[0]


    weights_sugar = [25, 15, 10, 20, 30]
    sugar_levels = ['0%', '30%', '50%', '80%', '100%']
    sugar_level = random.choices(sugar_levels, weights=weights_sugar)[0]

    weights_ice = [75, 15, 10]
    ice_levels = ['Regular', 'Less', 'No Ice']
    ice_level = random.choices(ice_levels, weights=weights_ice)[0]

    item_price = data_items[item_id][2]

    consume_recipe(item_id)

    rounded_item_price = round(item_price + toppings_total, 2) 
    data_order_items[i] = [i, order_id, item_id, rounded_item_price, 
                           ice_level, sugar_level]

    return data_order_items[i][3]

def generate_random_order_item_topping(order_item_id):
    weights = [40, 9, 8, 5, 4, 12, 8, 6, 9, 8]
    topping_id = random.choices(range(10), weights=weights)[0]

    data_order_item_toppings.append([])
    i = len(data_order_item_toppings) - 1
    data_order_item_toppings[i] = [i, order_item_id, topping_id, topping_prices[topping_id]]

    consume_inventory(data_toppings[topping_id][3], data_toppings[topping_id][4])

    return topping_prices[topping_id]

generate_orders()


with open('items.csv', 'w', newline='', encoding='utf-8') as file:
    writer = csv.writer(file)
    writer.writerow(headers_items)
    writer.writerows(data_items)
with open('employees.csv', 'w', newline='', encoding='utf-8') as file:
    writer = csv.writer(file)
    writer.writerow(headers_employees)
    writer.writerows(data_employees)
with open('order_items.csv', 'w', newline='', encoding='utf-8') as file:
    writer = csv.writer(file)
    writer.writerow(headers_order_items)
    writer.writerows(data_order_items)
with open('toppings.csv', 'w', newline='', encoding='utf-8') as file:
    writer = csv.writer(file)
    writer.writerow(headers_toppings)
    writer.writerows(data_toppings)
with open('order_item_toppings.csv', 'w', newline='', encoding='utf-8') as file:
    writer = csv.writer(file)
    writer.writerow(headers_order_item_toppings)
    writer.writerows(data_order_item_toppings)
with open('orders.csv', 'w', newline='', encoding='utf-8') as file:
    writer = csv.writer(file)
    writer.writerow(headers_orders)
    writer.writerows(data_orders)
with open('inventory.csv', 'w', newline='', encoding='utf-8') as file:
    writer = csv.writer(file)
    writer.writerow(headers_inventory)
    writer.writerows(data_inventory)
with open('recipes.csv', 'w', newline='', encoding='utf-8') as file:
    writer = csv.writer(file)
    writer.writerow(headers_recipes)
    writer.writerows(data_recipes)
with open('utensils.csv', 'w', newline='', encoding='utf-8') as file:
    writer = csv.writer(file)
    writer.writerow(headers_utensils)
    writer.writerows(data_utensils)
print("done.")
