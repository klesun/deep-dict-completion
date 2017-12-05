
def provide_dict_completion():
    flight = {'from': 'KIV', 'to': 'RIX', 'airline': 'AA'}
    segment = flight
    return (
        (flight, ('from', 'to', 'airline')),
        (segment, ('from', 'to', 'airline')),
    )

def __make_unit():
    return {'health': 100, 'mana': 200, 'damage': 20}

def provide_func_call():
    unit = __make_unit()
    return (
        (unit, ('health', 'mana', 'damage')),
    )

def provide_if_else_scope():
    cow = wolf = cat = None
    if 3 > 4:
        animal = {'name': 'cow', 'product': 'milk'}
        cow = animal
    elif 4 >5:
        animal = {'name': 'wolf', 'teeth': 'big'}
        wolf = animal
    else:
        animal = {'name': 'cat', 'lazy': True}
        cat = animal
    return (
        (cow, ('name', 'product')),
        (wolf, ('name', 'teeth')),
        (cat, ('name', 'lazy')),
        (animal, ('name', 'product', 'teeth', 'lazy')),
    )

def provide_nested_dict():
    witcher = {
        'name': 'Berengar',
        'fractions': ['Khaer Morhen', 'Salamandra'],
        'role': 'Secondary Character',
        'swords': {
            'silver': {
                'name': 'Zirael',
                'sharpness': 'very sharp',
            },
            'steel': {
                'name': 'Alastor',
                'sharpness': 'very very sharp',
            },
        },
    }
    witcher['swords']['silver']['']
    return (
        (witcher, ('name', 'fractions', 'role', 'swords')),
        (witcher['swords'], ('silver', 'steel')),
        (witcher['swords']['silver'], ('name', 'sharpness')),
    )

def provide_loop():
    last_hero = None
    heroes = [
        {'name': 'Riki', 'role': 'carry'},
        {'name': 'Shadow Fiend', 'role': 'nuker'}
    ]
    for hero in heroes:
        hero['']
        last_hero = hero
    return (
        (last_hero, ('name', 'role')),
    )

# not implemented yet follow

def provide_assigned_keys():
    witcher = {'name': 'Heralt', 'role': 'Protagonist'}
    witcher['found'] = 'Ciri'
    witcher['']
    return (
        # (witcher, ('name', 'role', 'found')),
    )

def provide_generator():
    heroes = [
        {'name': 'Riki', 'items': [
            {'name': 'Shadow Blade', 'price': 2600},
            {'name': 'Sage and Yasha', 'price': 4200},
        ]},
        {'name': 'Shadow Fiend', 'items': []},
    ]
    cheap_items = [item
        for hero in heroes if hero['name'] != 'Shadow Fiend'
        for item in hero['items'] if item['price'] < 3000
    ]
    return (
        # (cheap_items[0], ('name', 'price')),
    )