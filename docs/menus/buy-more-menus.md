# 🔽 Buy More Menus

Buy More selects a quantity for an already selected product. The quantity counts **trade units**; a unit can reward several physical items. Open it through a product's `select-amount` operation or a `buy_more_menu` action with shop/product context.

Inventory quantity files live in `menus/buy_more_menus/`. Bundled files are `buy-more.yml`, `buy-more-buy.yml` and `buy-more-sell.yml`. They are not standalone menus for `/shop menu`.

## Select a menu and maximum

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

| Setting under `menu.buy-more-menu` | Purpose |
| --- | --- |
| `default` | Menu/maximum for products with both price directions |
| `only-buy` | Menu/maximum for products without sell prices |
| `only-sell` | Menu/maximum for products without buy prices |
| `not-open-when-invalid` | Prevent opening when no confirmation control is valid for the product |
| `display-item-max-stack` | Use an extended preview stack maximum of 99 on supported Minecraft 1.20.5+ runtimes |

Shop `settings.buy-more` supplies the enablement default. Product `buy-more` and `buy-more-menu` can override it:

```yaml
items:
  A:
    buy-more: true
    buy-more-menu:
      menu: buy-more
      max-amount: 128
```

This is a fragment to add to an existing product. The selected quantity is clamped from 1 to its configured maximum. Preview capacity does not set that maximum.

The bundled product click binding is `menu.click-event.select-amount: SHIFT_RIGHT`. Set a click binding to `NEVER` to disable it. A `buy_more_menu` action accepts `shop` and `item`; Premium actions can additionally provide `buy-more-menu.menu` and `buy-more-menu.max-amount` for that opening. See [Custom Click Event](../features/custom-click-event-premium.md#example-only-buy-more-menu).

## Inventory quantity example

Save this as a new file in `menus/buy_more_menus/` and select its ID in the settings above:

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

The amount, preview and confirmation IDs must appear in `layout`. Ordinary `buttons` can supply fillers or navigation; use separate IDs for each control type. The example starts with quantity 1.

## Quantity file reference

| Key | Behavior |
| --- | --- |
| `amount-items.<id>.display-item` | Appearance of the quantity control |
| `amount-items.<id>.add-amount` | Positive/negative adjustment |
| `amount-items.<id>.set-amount` | Fixed value when `add-amount` is absent or zero |
| `display-items` | Ordered preview markers; takes priority over singular `display-item` |
| `display-item` | Alternative single preview marker |
| `display-items-padding-item` | Display Item Format used for unused preview space |
| `confirm-items.<id>.display-item` | Confirmation appearance; supports `{amount}` |
| `confirm-items.<id>.click-action` | Fixed operation such as `buy`, `sell`, `buy-or-sell` or `sell-all`; omitted uses the click's configured operation |
| `confirm-items.<id>.modify-lore` | Generate product lore for the selected quantity; default `true` |
| Control `menu-visibility` | Presentation filtering for a control |
| `dialog.enabled` | Allow native quantity input when Dialog is available |

Preview stacks split the quantity across preview slots in order, using the effective display stack maximum. Unused space shows padding or air. Confirmation uses normal trade validation and the selected quantity; displaying a large preview does not grant items.

This file supports ordinary title, size, layout, buttons and lifecycle settings. It does not inherit its parent shop's `settings.menu-settings` or global graphical `menu.ui`. Its `bedrock`, `arcmenu`, `ui` and `display-items-ui` fields do not define alternative quantity screens.

## Quantity selection in each presentation

| Presentation | Quantity controls | Text configuration |
| --- | --- | --- |
| Inventory | This file's amount buttons, previews and confirmations | This Buy More file |
| Bedrock Form | Text entry, followed by product information | Source shop menu `bedrock.buy-or-sell`, then global `menu.bedrock.buy-or-sell` |
| Native Dialog | Native amount input when this file enables Dialog; otherwise Inventory | Source shop menu `dialog.buy-more`, then global `menu.dialog.buy-more` |
| ArcMenu | Product details with quantity controls | Graphical settings and ArcMenu text |

Form quantity entry belongs to the source shop flow; it does not render this file's slot controls. Dialog keeps action-specific menu/maximum overrides through input and confirmation. Global forced UI can override the normal Dialog selector.

ArcMenu's `select-amount` opens product details. Quantity adjustment requires Buy More, a selected quantity menu and an available quantity operation. Right-click defaults to `select-amount`; plus/minus and scrolling adjust quantity. Compatible ArcMenu versions also provide direct [quantity text input](arcmenu-menus-premium.md#quantity-input). Inventory confirmation labels do not rename graphical or native information actions.

## Default trade amounts

Before manual quantity selection, `default-buy-amount` and `default-sell-amount` apply independently. After selection, both directions use the chosen count. Generated `{buy-amount}` and `{sell-amount}` reflect the corresponding operation; an explicit display-item stack amount remains separate. See [Default Trade Amounts](../shops/products.md#default-trade-amounts).

[Shop Menus](shop-menus.md) · [UI Modes](ui-overview.md) · [Display Item Add Lore](display-item-add-lore.md)
