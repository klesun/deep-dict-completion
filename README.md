# deep-dict-completion
Pycharm plugin for deep type inference based dict keys completion

Idea will be similar to https://github.com/klesun/deep-assoc-completion

Like in phpstorm, idea has _very_ basic key completion - dict var must be defined in same scope as the reference on which you request the completion (and for some reason you must access key with same quotes `'`/`"` as this key was defined).

What i plan to do (by priority in descending order):
- Make it provide completion in vars assigned from function call like here:
```python
def get_book():
    return {'author': 'Vova', 'genre': 'Science Fiction', 'title': 'Sand-Hill'}

book = get_book()
book[''] # should suggest: 'author', 'genre', 'title'
```
- Add Go To definition.
- Make it possible to specify keys comments. I guess it could be done using something like mypy's `TypedDict('Movie', {'name': str, 'year': int})` syntax. Though it requires lotta of redundant text, so maybe the old good `@param movie = {'name': 'Blade Runner', 'year': 1982}` would be better. Maybe support both.
- Add some more support based on mypy typing if idea does not have them by default. It seems to understand `namedtuple` and inference arrays of objects very well, but what about generics?).
