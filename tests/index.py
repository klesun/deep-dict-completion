from asyncio import Protocol
from typing import List, Callable
from mypy_extensions import TypedDict
from collections import namedtuple

class Tax(object):
    tax_code: str
    currency: str
    amount: str


IPricing = TypedDict('IPricing', {
    'net_price': str,
    'currency': str,
    'taxes': List[TypedDict('ITax', {
        'tax_code': str,
        'currency': str,
        'amount': str,
    })],
    'get_fuel_surcharge': Callable[[], str],
})


def get_segment():
    if 3 > 4:
        segment = {
            "airline": "DL",
            "flightNumber": "1234",
            "bookingClass": "F",
            "departure": "MNL",
            "destination": "NRT",
        }
    else:
        segment = {"a": 5, "b": 6}
    flight = segment
    flight['']
    return flight

Fun = namedtuple('Fun', ['closure', 'headers', 'is_secure'])
def huj():
    return Fun(
        closure=lambda a: a + 6,
        headers=['Cache-Control: max-age=86400'],
        is_secure=False,
    )

def get_some_values():
    values = [
        Fun(
            closure=lambda a: a + 6,
            headers=['Cache-Control: max-age=86400'],
            is_secure=False,
        ),
        Fun(
            closure=lambda a: a + 6,
            headers=['Cache-Control: max-age=86400'],
            is_secure=False,
        ),
    ]
    reassigned = values
    return reassigned


def main(gds: str) -> IPricing:
    segment = get_segment()
    segment[""]
    print(segment)

    obj = type('obj', (object,), {'propertyName' : 'propertyValue'})

    d = { 'a' : 'foo', 'b' : 'bar' }
    foobar = namedtuple('foobar', d.keys())(**d)

    method_dict = {
        'get_ichigos_midi_names': Fun(
            closure=lambda a: a + 6,
            headers=['Cache-Control: max-age=86400'],
            is_secure=False,
        ),
        'get_youtube_links': Fun(
            closure=lambda a: a + 6,
            headers=['Cache-Control: max-age=86400'],
            is_secure=False,
        ),
        'get_user_profiles': Fun(
            closure=lambda a: a + 6,
            headers=['Cache-Control: max-age=86400'],
            is_secure=False,
        ),
    }
    method_dict['asd']['get_ichigos_midi_names'] = 123
    method_dict['qwe'] = {'a':5, 'b': 7}
    method_dict = {
        'f': {
            'g': 5,
        },
    }
    method_dict['f']

    return {

    }

def get_book():
    return {'author': 'Vova', 'genre': 'Science Fiction', 'title': 'Sand-Hill'}

book = get_book()
book['Milky Holmes']

main('sabre')
