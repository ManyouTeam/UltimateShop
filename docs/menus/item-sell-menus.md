# 💰Item Sell Menus

Item sell menus let players place item stacks into configured slots, preview their adjusted sell price, and confirm the sale with one button. Files are stored in `/item_sell_menus/` and can be opened with `/shop menu <menuName>`.

The default example is `item_sell_menus/item-sell.yml`.

## Menu structure

```yaml
menu-type: item-sell
title: 'Sell Items'
size: 54
max-sell-amount: -1

layout:
  - '000000000'
  - '0IIIIIII0'
  - '0IIIIIII0'
  - '0IIIIIII0'
  - '000000000'
  - '00B0C0X00'

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
      - '&aClick to sell.'
```

`input-item` is the layout marker for slots that accept player items. `confirm-item` is the marker for the confirmation button. `max-sell-amount` limits the total number of product trade units sold by one confirmation across all input slots; `-1` means unlimited. Unsold or unsupported items remain in the menu and are returned when it closes.

The confirmation display supports these placeholders:

* `{price}`: final price after the global sell multiplier and item-aware modifiers
* `{original-price}`: product sell price before multipliers
* `{multiplier}`: combined final sell multiplier
* `{item-amount}`: amount that can currently be sold
* `{unsellable-amount}`: amount that cannot currently be sold

The transaction still uses the normal product sell flow, including sell conditions, player/server limits, actions, events, logging, and the global sell multiplier. An item sell menu does not have its own modifier configuration.

Products with `price-modifier: true` can only be sold through an item sell menu. Enable `sell.price-modifier.item-sell-menu.enabled` and set `sell.price-modifier.item-sell-menu.menu` to choose that menu.

Choosing `sell` or `sell-all` for one of these products opens the configured item sell menu. Buy, amount-selection, and custom click actions keep their normal behavior. Bedrock forms and dialog shop lists open the product information view first so that buy and sell remain separate choices.

See [Price Modifier](../shops/price-modifier.md) for modifier types, formulas, validation, calculation precision, and the developer API.
