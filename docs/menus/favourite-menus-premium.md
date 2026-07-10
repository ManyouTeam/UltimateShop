# ❤️Favourite Menus - Premium

## Favourite Menu Config

Favourite menus have those special options compare than common menu:

```yaml
result-items:
  - '1'

edit-mode-item:
  D:
    normal-display-item:
      material: NAME_TAG
      name: '{lang:edit-mode-enable-name}'
      lore:
        - '{lang:edit-mode-enable-line-1}'
    editing-display-item:
      material: WRITABLE_BOOK
      name: '{lang:edit-mode-disable-name}'
      lore:
        - '{lang:edit-mode-disable-line-1}'

empty-item:
  display-item:
    material: GRAY_STAINED_GLASS_PANE
    name: '{lang:favourite-empty-name}'
    lore:
      - '{lang:favourite-empty-line-1}'
      - '{lang:favourite-empty-line-2}'

result-lore:
  - ' '
  - '{lang:favourite-result-shop}'
  - '{lang:favourite-result-product}'
  - '{lang:favourite-result-click}'
  - '{lang:favourite-result-enter-edit}'

editing-result-lore:
  - ' '
  - '{lang:favourite-result-shop}'
  - '{lang:favourite-result-product}'
  - '{lang:favourite-result-edit-forward}'
  - '{lang:favourite-result-edit-backward}'
  - '{lang:favourite-result-edit-remove}'
```

* result-items: Defines the marker used for result slots. Matched products are displayed into these slots in order. Must be a single char, use this char in `layout` option to set where it will display in menu.
* edit-mode-item: Displays the edit mode button. Player have to enter edit mode to modify the favourite products. Each edit mode button supports:
  * `normal-display-item`
  * `editing-display-item`
* empty-item: If the result item slot is empty, we will replace it to this item instead.
* result-lore: Extra lore added to each result item.
* editing-result-lore: Extra lore added to each result item when in edit mode.

## Set Favoutite Menu

You should put seach menu ID in `menu.favoutire-gui.menu` option in `config.yml` file.

```yaml
menu:
  favourite-gui:
    menu:
      - 'favourite'
      - 'favoutite2' # Put more search menu ID here.
```

The default favourite menu file is `menus/favourite.yml`, and you can duplicate it to create more favourite menus.

Command:

```yaml
/shop menu <menuName>
```

Examples:

```yaml
/shop menu favourite
```
