# 🛒 Sell All Menu

Sell All is one global Inventory screen. Players deposit items and **close the window to sell them**. It has no explicit confirmation button. [Item Sell](item-sell-menus.md) provides price preview and confirmation when that flow is preferred.

<a id="open-the-menu"></a>

## Open the screen

Use `/shop sellall` with permission `ultimateshop.sellall`, or add this action to a menu button:

```yaml
actions:
  1:
    type: sell_all_menu
```

Opening from Form, Dialog, Ore or ArcMenu still creates a physical Inventory.

## Configure the screen

In `config.yml`:

```yaml
menu:
  sell-all:
    size: 54
    title: '{lang}'
    black-slots: []
```

| Key under `menu.sell-all` | Purpose |
| --- | --- |
| `size` | Slot count: `9`, `18`, `27`, `36`, `45` or `54` |
| `title` | Screen title; `{lang}` uses the matching language override |
| `black-slots` | Zero-based slots that reject item placement, for example `[0, 1, 2]` |

This screen has no separate file in `menus/`, no ordinary layout/button configuration and no independent menu IDs. Graphical styles and forced presentation selection do not change it.

## Configure sale exclusions

Exclude shop products in `config.yml` using `shop-id;;product-id`:

```yaml
sell:
  sell-all:
    ignore-items:
      - 'farming;;A'
```

This excludes that product reference from the Sell All flow. See [product configuration](../shops/products.md) for prices and sell requirements.

## Sale on close

Closing scans eligible shop products and runs normal selling against deposited items. Product conditions, limits, exclusions and applicable multipliers remain in effect. Global `sell.max-amount` limits the sell engine; it is separate from Inventory capacity and Item Sell's `max-sell-amount`.

Unsold items return to the player, or drop when they cannot fit. An empty menu produces no reward. Products requiring item-aware price modifiers use the configured Item Sell flow.

[Item Sell](item-sell-menus.md) · [Inventory UI](inventory-ui.md)
