# deep-dict-completion
Pycharm plugin for deep type inference based dict keys completion

Build: [may be outdated](https://www.dropbox.com/s/a5eki8wfne2zwdd/deep-dict-completion.jar?dl=0)

![screenshot](https://user-images.githubusercontent.com/5202330/33587199-dc534982-d964-11e7-946a-299b505b36a7.gif)

Idea is similar to https://github.com/klesun/deep-assoc-completion

What it already does:
- Makes it provide completion in vars assigned from function call like here:
```python
def get_book():
    return {'author': 'Vova', 'genre': 'Science Fiction', 'title': 'Sand-Hill'}

book = get_book()
book[''] # should suggest: 'author', 'genre', 'title'
```
What i plan to do:
- Add Go To definition.
- Make it possible to specify keys comments. I guess it could be done using something like mypy's `TypedDict('Movie', {'name': str, 'year': int})` syntax. Though it requires lotta of redundant text, so maybe the old good `@param movie = {'name': 'Blade Runner', 'year': 1982}` would be better. Maybe support both.
- Add some more support based on mypy typing if idea does not have them by default. It seems to understand `namedtuple` and inference arrays of objects very well, but what about generics?).
