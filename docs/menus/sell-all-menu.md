# 🛒Sell All Menu

Sell All is a single global Inventory interface: players deposit items, then close the window to sell them. It does not use a file in `menus/` and does not have a separate confirmation button. For explicit confirmation and price preview, use [Item Sell](item-sell-menus.md).

## Open the menu

`/shop sellall` requires `ultimateshop.sellall`. A menu button can use the `sell_all_menu` action:

```yaml
actions:
  1:
    type: sell_all_menu
```

This remains an Inventory interface even when the source shop/menu uses Form, Dialog, Ore or ArcMenu.

## Configuration reference

In `config.yml`:

```yaml
menu:
  sell-all:
    size: 54
    title: '{lang}'
    black-slots: []
```

| Key under `menu.sell-all` | Meaning |
| --- | --- |
| `size` | Inventory size: 9, 18, 27, 36, 45 or 54 |
| `title` | Title; `{lang}` uses the matching language override |
| `black-slots` | Zero-based slot numbers that reject item placement, for example `[0, 1, 2]` |

Configure excluded product references under `sell.sell-all.ignore-items` in `config.yml`, using `shop-id;;product-id`. The removed `menu.sell-all.ignore-items` alias is not read. This menu does not support common-menu layout/buttons, `ui.layout`, per-file modes or multiple independent menu IDs.

## Sale behaviour

Closing scans shop products visible to the player, skips excluded references and runs the normal sell flow against the deposited inventory. Product conditions, prices, player/server limits and applicable sell multipliers still apply. Global `sell.max-amount` limits the sell engine (fallback 128); the removed `menu.sell-all.max-amount` alias is not read; it is not this inventory's slot count or an Item Sell `max-sell-amount`.

The sale summary reports amount, reward and effective multiplier. Items that were not sold are returned to the player, or dropped if they cannot fit. Closing an empty menu does not create a reward. Products requiring item-aware price modifiers must use the configured Item Sell flow; Sell All does not replace that requirement.
