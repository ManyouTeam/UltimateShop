# 🛒Sell All Menu

## Sell All Menu Config

You can find sell all menu settings at `config.yml` file. You can only set 1 menu to sell all menu.

```yaml
menu:
  sell-all:
    size: 54
    title: '{lang}'
    black-slots: []
```

* size: Menu size, only support one of the number: **9,18,27,36,45,54**.
* title: Menu title.
* black-slots: Which slots will not allow to place item to sell. For example: `[0, 1, 2, 3]`.

## Sell All

Close this menu to sell all items inside this GUI. This menu also follows max amount setting in `sell.max-amount` option, by default, the value is `128`.

## Open Sell All Menu

You can use `/shop sellall` command to open sell all menu. This command require `ultimateshop.sellall` permission.
