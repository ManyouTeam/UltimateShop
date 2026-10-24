# 💰 Item Sell Menus

Item Sell lets players place real item stacks into input slots, preview their adjusted sell value, and explicitly confirm the sale. Closing without confirming returns the items. This differs from [Sell All](sell-all-menu.md), which sells on close.

Files live in `plugins/UltimateShop/menus/item_sell_menus/`; the bundled example is `item-sell.yml`. Open with `/shop menu item-sell` or an `open_menu` action. The dedicated folder determines the type; files in `common_menus` can use `menu-type: item-sell` (alias `item_sell`).

<a id="example"></a>

## Inventory deposit example

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
      - '&7Final multiplier: &fx{multiplier}'
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

Undefined `0` slots are empty but are not input slots; define filler button `0` if desired. The example intentionally includes only a confirm control and a Close button.

## Item Sell-specific keys

Common title, size, layout, buttons, open conditions and open/close actions are described in [Common menus](general-menus.md).

| Additional key | Meaning |
| --- | --- |
| `input-item` | Input layout marker, default `I` |
| `input-items` | Additional input markers, combined with `input-item` |
| `confirm-item` | Confirmation layout marker, default `C`; may occur in multiple slots |
| `confirm-button.display-item` | Display Item Format for the confirmation control |
| `max-sell-amount` | Maximum product trade units sold across all input slots per confirmation; `-1` means unlimited |

Only input slots accept the items to be sold. Use the layout token rules for longer IDs. The confirmation appearance is configured in `confirm-button`, not as a Buy More `confirm-items` entry. This menu's confirmation has a built-in sale handler; its name/lore does not define the sale formula.

The preview placeholders are:

| Placeholder | Value |
| --- | --- |
| `{price}` | Final reward after global sell multiplier and applicable item-aware modifiers |
| `{original-price}` | Reward before those multipliers |
| `{multiplier}` | Combined effective sell multiplier |
| `{item-amount}` | Amount currently eligible to sell |
| `{unsellable-amount}` | Amount currently not eligible to sell |

Preview is recalculated from input. Confirmation still runs normal product sell validation, including conditions, player/server limits, actions, events and logging. Unsupported or unsold items remain in the menu and return on close. Display/confirmation icons are not part of the player's sale stock.

## Item-aware price modifiers

Products with `price-modifier: true` must be sold through Item Sell so the calculation can inspect the supplied item. The menu has no independent modifier formula; configure modifiers on the product. Select the routing target globally:

```yaml
sell:
  price-modifier:
    item-sell-menu:
      enabled: true
      menu: item-sell
```

For those products, `sell` and `sell-all` route to the selected Item Sell menu. Buy, quantity selection and custom operations keep their normal behaviour. Form/Dialog shop listings first open product information so buying and selling remain separate choices; choosing the routed sell operation opens the physical Inventory menu.

## Presentation and related settings

This is an Inventory-only deposit interface, including for players whose ordinary shops use Form, Dialog, Ore or ArcMenu. It does not support `dialog`, `bedrock`, `arcmenu`, `ui` or button `ui.placement` settings, and does not inherit global `menu.ui` defaults. Configure Inventory `size`, `layout`, input and confirmation markers for this screen.

`max-sell-amount` belongs to this menu and caps the combined confirmation. The global sell engine still applies its own restrictions; raising this value does not bypass stock, limits or product conditions. See [Price Modifier](../shops/price-modifier.md) for calculation and [Sell Multiplier - Premium](../shops/sell-multiplier-premium.md) for reward multipliers.

## Related documentation

[Inventory UI](inventory-ui.md) · [Sell All](sell-all-menu.md) · [Troubleshooting](troubleshooting.md)
