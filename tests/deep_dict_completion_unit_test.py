
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
