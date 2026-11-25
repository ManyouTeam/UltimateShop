# 🛒Sell All Menu

Sell All is a global Inventory screen where players deposit items and **sell them automatically on close**, with no confirm button. If you need a live price preview and explicit confirmation, use an [Item Sell Menu](item-sell-menus.md).

<a id="open-the-menu"></a>

## Open the Menu

Players can run `/shop sellall` (requires `ultimateshop.sellall`), or trigger it from any menu button action:

```yaml
actions:
  1:
    type: sell_all_menu
```

Even when triggered from Form, Dialog, Ore, or ArcMenu, it always opens a physical Inventory screen.

## Configure the Screen

Configure it directly in `config.yml` under `menu.sell-all` (no file in `menus/`):

```yaml
menu:
  sell-all:
    size: 54
    title: '{lang}'
    black-slots: []
```

| `menu.sell-all` Key | Purpose |
| --- | --- |
| `size` | Slot count: `9`, `18`, `27`, `36`, `45`, or `54` |
| `title` | Screen title (`{lang}` uses the active language file) |
| `black-slots` | Zero-based slots blocked from item input (e.g. `[0, 1, 2]`) |

This screen has no menu ID and does not use `layout`, `buttons`, graphical styles, or `force-ui`.

## Exclude Products

Exclude specific products in `config.yml` using `shop-id;;product-id`:

```yaml
sell:
  sell-all:
    ignore-items:
      - 'farming;;A'
```

See [Products](../shops/products.md) for base pricing and sell rules.

## Selling on Close

When the menu closes, the plugin scans eligible shop products and runs normal bulk selling on the deposited items (conditions, limits, ignore lists, and multipliers all apply). Global `sell.max-amount` caps the sell engine independently of Inventory capacity and Item Sell `max-sell-amount`.

Unsold or capped items return to the player's inventory (or drop if full); closing an empty menu gives no payout. Products requiring physical item inspection for price modifiers delegate to their configured Item Sell flow.

[Item Sell Menus](item-sell-menus.md) · [Inventory UI](inventory-ui.md)
