
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
