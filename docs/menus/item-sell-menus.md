# 💰 Item Sell Menus

Item Sell accepts real item stacks, previews their sell value and waits for an explicit confirmation. Closing without confirming returns the items. Use [Sell All](sell-all-menu.md) when deposits should instead be sold on close.

Save files in `menus/item_sell_menus/`. Open the bundled menu with `/shop menu item-sell` or an `open_menu` action. The folder assigns the menu type automatically; a file in `common_menus` can use root `menu-type: item-sell` (alias `item_sell`).

<a id="example"></a>

## Create a deposit menu

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

Save as `menus/item_sell_menus/my-sell.yml`, reload, then open `/shop menu my-sell`. `I` marks deposit slots; `C` marks confirmation. Undefined `0` slots are empty but do not accept sale deposits.

## Configuration reference

| Key | Purpose |
| --- | --- |
| `input-item` | Primary input marker; default `I` |
| `input-items` | Additional input markers, combined with `input-item` |
| `confirm-item` | Confirmation marker; default `C`, can appear in multiple slots |
| `confirm-button.display-item` | Confirmation icon, name and lore |
| `max-sell-amount` | Maximum trade units sold across all inputs per confirmation; `-1` is unlimited |

Common title, layout, ordinary buttons, opening conditions and lifecycle actions are supported. Longer markers use the [normal layout token rules](layout-and-buttons.md). The built-in confirmation control uses `confirm-button`, separate from Buy More's `confirm-items`.

## Price preview and confirmation

| Confirmation placeholder | Value |
| --- | --- |
| `{price}` | Final reward with applicable sell multipliers and item-aware modifiers |
| `{original-price}` | Reward before those multipliers |
| `{multiplier}` | Combined effective multiplier |
| `{item-amount}` | Amount eligible to sell |
| `{unsellable-amount}` | Amount currently ineligible |

The preview updates from deposited items. Confirming runs the normal product sell flow, including conditions, limits, events, actions and transaction logging. Unsold items remain available and are returned on close. Menu icons are not player sale stock.

`max-sell-amount` caps this menu's combined confirmation. It does not override product stock, conditions or other sell restrictions.

## Route item-aware products here

Products using `price-modifier: true` need Item Sell so pricing can inspect the actual supplied stacks. Configure the target in `config.yml`:

```yaml
sell:
  price-modifier:
    item-sell-menu:
      enabled: true
      menu: item-sell
```

For these products, the `sell` and `sell-all` operations route to that menu. The target ID must exist. Form/Dialog listings first expose product information so players can choose buying or selling; routed selling opens the physical Inventory. The product defines its modifiers, while the menu supplies input and confirmation.

## Presentation support

Item Sell is Inventory-only for all clients. Its settings do not include Form, Dialog, Ore or ArcMenu layout, and it does not inherit graphical `menu.ui`. Global forced UI preserves this physical deposit interface.

[Price Modifier](../shops/price-modifier.md) · [Sell Multiplier](../shops/sell-multiplier-premium.md) · [Inventory UI](inventory-ui.md)
