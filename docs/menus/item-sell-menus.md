# 💰Item Sell Menus

Item Sell menus accept physical items, preview the total payout, and wait for explicit confirmation. Closing without confirming returns all items; if you want items sold automatically on close, use the [Sell All Menu](sell-all-menu.md).

Files live in `menus/item_sell_menus/` (auto-detected by folder) and the built-in menu opens via `/shop menu item-sell` or `open_menu`. Legacy files in `common_menus` can declare `menu-type: item-sell` (alias `item_sell`) at the root.

<a id="example"></a>

## Create an Item Deposit Menu

Save as `menus/item_sell_menus/my-sell.yml`, reload, and run `/shop menu my-sell`:

```yaml
title: 'Sell Items'
size: 54
max-sell-amount: -1
layout:
  - '000000000'
  - '0IIIIIII0'
  - '0IIIIIII0'
  - '0IIIIIII0'
  - '000000000'
  - '0000C0X00'
input-item: 'I'
confirm-item: 'C'
confirm-button:
  display-item:
    material: EMERALD
    name: '&aConfirm Sale'
    lore:
      - '&7Price: &f{price}'
      - '&7Original price: &f{original-price}'
      - '&7Multiplier: &fx{multiplier}'
      - '&7Sellable items: &f{item-amount}'
      - '&7Unsellable items: &f{unsellable-amount}'
buttons:
  X:
    display-item:
      material: BARRIER
      name: '&cClose'
    actions:
      1:
        type: close
```

`I` marks item deposit slots and `C` marks the confirm button; undefined `0` slots stay empty and do not accept items.

## Configuration Reference

| Key | Purpose |
| --- | --- |
| `input-item` | Primary input symbol (default `'I'`) |
| `input-items` | Additional input symbols (merged with `input-item`) |
| `confirm-item` | Confirm symbol (default `'C'`; may occupy multiple slots) |
| `confirm-button.display-item` | Confirm button icon, name, and lore |
| `max-sell-amount` | Max combined trade units sold per confirmation (`-1` for unlimited) |

Supports standard title, layout, `buttons`, open conditions, and lifecycle actions; multi-character IDs follow [Layout & Buttons](layout-and-buttons.md). Note the confirm key is `confirm-button`, distinct from `confirm-items` in Buy More menus.

## Price Preview and Confirmation

| Confirm Button Placeholder | Meaning |
| --- | --- |
| `{price}` | Final payout after sell multipliers and item-based price modifiers |
| `{original-price}` | Payout before those multipliers/modifiers |
| `{multiplier}` | Combined effective multiplier |
| `{item-amount}` | Sellable item count |
| `{unsellable-amount}` | Unsellable item count |

The preview updates live as items are added or removed. Confirming runs the full product sell flow (conditions, limits, events, actions, and transaction logs), and any unsold items remain in their slots and return on close (GUI icons are never treated as player items).

`max-sell-amount` caps only the combined units per confirmation in this menu—it never overrides product stock, conditions, or other sell limits.

## Route Products That Inspect Actual Items Here

Products using `price-modifier: true` must inspect the physical items being sold, so configure their target Item Sell menu in `config.yml`:

```yaml
sell:
  price-modifier:
    item-sell-menu:
      enabled: true
      menu: item-sell
```

`sell` and `sell-all` on those products route to the configured menu (the target ID must exist). In Form/Dialog, players still see product info first and choose Buy or Sell; choosing Sell opens the physical Inventory menu for deposit and confirmation.

## Presentation Support

To support physical item deposits, Item Sell menus always use **Inventory UI** for all clients—never Form, Dialog, Ore, or ArcMenu—and do not inherit graphical `menu.ui` (even when `menu.force-ui` is enabled).

[Price Modifier](../shops/price-modifier.md) · [Sell Multiplier](../shops/sell-multiplier-premium.md) · [Inventory UI](inventory-ui.md)
