# 🔽Buy More Menus

Buy More menus let players choose a quantity for a selected product (quantities represent **trade units**—if one trade gives multiple items, the total scales accordingly). Players open them via a product's `select-amount` action or a `buy_more_menu` action with shop/product context.

Inventory quantity menus live in `menus/buy_more_menus/` (built-in: `buy-more.yml`, `buy-more-buy.yml`, `buy-more-sell.yml`) and cannot be opened standalone via `/shop menu`.

## Choose Menus and Caps

Global defaults in `config.yml`:

```yaml
menu:
  buy-more-menu:
    not-open-when-invalid: true
    display-item-max-stack: true
    default:
      menu: buy-more
      max-amount: 693
    only-buy:
      menu: buy-more-buy
      max-amount: 693
    only-sell:
      menu: buy-more-sell
      max-amount: 693
```

| `menu.buy-more-menu` Option | Purpose |
| --- | --- |
| `default` | Menu and max units when a product has both buy and sell prices |
| `only-buy` | Menu and max units when a product can only be bought |
| `only-sell` | Menu and max units when a product can only be sold |
| `not-open-when-invalid` | Blocks opening if the product has no valid confirm control |
| `display-item-max-stack` | Raises preview stack size up to 99 on Minecraft 1.20.5+ |

Shop `settings.buy-more` sets the default toggle, and individual products can override `buy-more` and `buy-more-menu` (selected quantity is clamped between `1` and `max-amount`, regardless of preview slot count):

```yaml
items:
  A:
    buy-more: true
    buy-more-menu:
      menu: buy-more
      max-amount: 128
```

The default click binding is `menu.click-event.select-amount: SHIFT_RIGHT` (set to `NEVER` to disable). The `buy_more_menu` action accepts `shop` and `item`, and Premium also allows per-open `buy-more-menu.menu` and `buy-more-menu.max-amount` overrides; see [Custom Click Event](../features/custom-click-event-premium.md#example-only-buy-more-menu).

## Inventory Quantity Menu Example

Create a file in `menus/buy_more_menus/` and reference its ID above:

```yaml
title: 'Choose amount'
size: 27
layout:
  - '000000000'
  - '01a2b3c00'
  - '000D0E000'
dialog:
  enabled: false
amount-items:
  '1':
    display-item:
      material: RED_WOOL
      name: '&c-1'
    add-amount: -1
  '2':
    display-item:
      material: GREEN_WOOL
      name: '&a+1'
    add-amount: 1
  '3':
    display-item:
      material: PAPER
      name: '&eSet to 64'
    set-amount: 64
display-items: [a, b, c]
display-items-padding-item:
  material: GRAY_STAINED_GLASS_PANE
  name: ' '
confirm-items:
  D:
    display-item:
      material: EMERALD
      name: '&aBuy x{amount}'
    click-action: buy
    modify-lore: true
  E:
    display-item:
      material: GOLD_INGOT
      name: '&eSell x{amount}'
    click-action: sell
    modify-lore: true
```

Amount, preview, and confirm IDs must appear in `layout`, and each control type should use distinct IDs. Common `buttons` can still be used for filler or back buttons. The menu starts at quantity `1`.

## Quantity Menu Configuration Reference

| Key | Behaviour |
| --- | --- |
| `amount-items.<id>.display-item` | Appearance of an amount adjustment button |
| `amount-items.<id>.add-amount` | Adds or subtracts from the current quantity |
| `amount-items.<id>.set-amount` | Sets a fixed quantity (used when `add-amount` is absent or `0`) |
| `display-items` | Ordered preview symbols (takes priority over singular `display-item`) |
| `display-item` | Single preview symbol shorthand |
| `display-items-padding-item` | Filler for unused preview slots (Display Item format) |
| `confirm-items.<id>.display-item` | Confirm button appearance; supports `{amount}` |
| `confirm-items.<id>.click-action` | Fixed action (`buy`, `sell`, `buy-or-sell`, `sell-all`); omit to use the opening click's action |
| `confirm-items.<id>.modify-lore` | Generates price/info lore for the chosen quantity (default `true`) |
| Control `menu-visibility` | Per-presentation filtering |
| `dialog.enabled` | Allows native quantity input when Dialog is available |

Preview items split across preview slots up to the active stack cap, with unused slots showing the padding item or air; confirming still runs normal trade validation (preview stacks are never given directly).

This file supports standard title, size, layout, `buttons`, and lifecycle actions, but **does not** inherit the source shop's `settings.menu-settings` or global graphical `menu.ui`. Its `bedrock`, `arcmenu`, `ui`, and `display-items-ui` keys do not define other quantity presentations.

## Quantity Selection Across Presentations

| Presentation | Quantity Controls | Text Source |
| --- | --- | --- |
| Inventory | Amount buttons, preview, and confirm controls in this file | Current Buy More file |
| Bedrock Form | Text input followed by product info confirmation | Source shop menu `bedrock.buy-or-sell` → global `menu.bedrock.buy-or-sell` |
| Native Dialog | Native input if this file enables `dialog.enabled`, else Inventory | Source shop menu `dialog.buy-more` → global `menu.dialog.buy-more` |
| ArcMenu | Quantity controls inside the graphical product details panel | Graphical settings and ArcMenu text |

In Bedrock Form, quantity input is part of the source shop flow and never renders Inventory slot controls. In Native Dialog, action-level menu and cap overrides persist across input and confirmation (global `menu.force-ui` can still override Dialog selection).

In ArcMenu, `select-amount` (default right-click) opens product details where +/- buttons, scroll wheel, or direct [text quantity input](arcmenu-menus-premium.md#quantity-input) adjust the count when Buy More and a quantity action are available. Inventory `confirm-items` labels do not rename graphical or native info actions.

## Default Trade Amounts

Before a player picks a quantity, `default-buy-amount` and `default-sell-amount` apply independently; once a quantity is chosen, both buy and sell use that count. `{buy-amount}` and `{sell-amount}` reflect those trade counts, while explicit display-item stack sizes stay separate. See [Default Trade Amounts](../shops/products.md#default-trade-amounts).

[Shop Menus](shop-menus.md) · [UI Modes](ui-overview.md) · [Display Item Add Lore](display-item-add-lore.md)
