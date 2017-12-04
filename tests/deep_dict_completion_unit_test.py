

def provide_dict_completion():
    flight = {'from': 'KIV', 'to': 'RIX', 'airline': 'AA'}
    segment = flight
    return (
        (segment, ('from', 'to', 'airline')),
    )
