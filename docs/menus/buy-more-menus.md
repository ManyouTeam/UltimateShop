# 🔽 Buy More Menus

Buy More selects the number of trade units for an already selected product. One trade unit can contain several items according to the product's configured reward amount. It is not a standalone shop and cannot be opened meaningfully with `/shop menu`.

Files live in `plugins/UltimateShop/menus/buy_more_menus/`. The bundled files are `buy-more.yml`, `buy-more-buy.yml` and `buy-more-sell.yml`.

<a id="enable-and-select-a-quantity-menu"></a>

## ⚙️ Enable and select the quantity menu

Global settings in `config.yml`:

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

| Key under `menu.buy-more-menu` | Meaning |
| --- | --- |
| `default.menu`, `default.max-amount` | Menu and maximum trade count when both price directions exist |
| `only-buy.menu`, `only-buy.max-amount` | Defaults for a product without sell prices |
| `only-sell.menu`, `only-sell.max-amount` | Defaults for a product without buy prices |
| `not-open-when-invalid` | Block opening a menu whose confirmation buttons are all invalid for that product |
| `display-item-max-stack` | Allow preview stacks to use the extended display maximum instead of the material stack limit on supported runtimes |

Shop `settings.buy-more` supplies the shop default. Product `buy-more` overrides whether that product allows quantity selection. Product `buy-more-menu` overrides the selected menu and maximum:

```yaml
items:
  A:
    buy-more: true
    buy-more-menu:
      menu: buy-more
      max-amount: 128
    # Product, prices and display follow here.
```

Bind the `select-amount` product operation under `menu.click-event` (default `SHIFT_RIGHT`) or use the [buy_more_menu action](../format/action-format.md) with the required shop/product context. `NEVER` disables the corresponding click binding. The `buy_more_menu` action supports `shop`, `item` and an optional `buy-more-menu` section with `menu` and `max-amount` for that opening. Custom click actions can select separate buy-only and sell-only menus; see [Custom Click Event](../features/custom-click-event-premium.md#example-only-buy-more-menu).

## ✅ Inventory amount-selection example

```yaml
title: 'Choose amount'
size: 27
layout:
  - '000000000'
  - '01a2b3c00'
  - '000D0E000'
amount-items:
  1:
    display-item:
      material: RED_WOOL
      name: '&c-1'
    add-amount: -1
  2:
    display-item:
      material: GREEN_WOOL
      name: '&a+1'
    add-amount: 1
  3:
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

`0` is empty unless defined under ordinary `buttons`. Confirmation and amount IDs must occur in `layout`. Do not reuse an ID for unrelated control types.

## 🧩 Amount/preview/confirmation keys

All [Common menu settings](general-menus.md) also apply, including title, layout, ordinary buttons, conditions/actions and UI selection.

| Additional key | Meaning |
| --- | --- |
| `amount-items.<id>.display-item` | Appearance of an amount control |
| `amount-items.<id>.add-amount` | Add a positive/negative amount to the current count |
| `amount-items.<id>.set-amount` | Set a fixed count when `add-amount` is absent or zero; set at least 1 |
| `display-items` | Ordered list of preview layout IDs; takes precedence over singular `display-item` |
| `display-item` | A single preview marker alternative to `display-items` |
| `display-items-padding-item` | Display Item Format for unused preview slots after the first |
| `display-items-ui.ui` | Optional graphical placement/style of a preview; this does not turn Buy More into an ArcMenu screen |
| `confirm-items.<id>.display-item` | Confirmation button appearance; `{amount}` is the selected quantity |
| `confirm-items.<id>.click-action` | Optional fixed operation such as `buy`, `sell`, `buy-or-sell`, `sell-all`; omitted means use the current click operation |
| `confirm-items.<id>.modify-lore` | Default true; generate the product price/stock/action lore using the selected amount |
| `amount-items.<id>.menu-visibility`, `confirm-items.<id>.menu-visibility` | Filter a control by presentation |
| `amount-items.<id>.ui`, `confirm-items.<id>.ui` | Graphical placement/style metadata; does not make ArcMenu render an Inventory Buy More menu |

Preview stacks split the selected count across preview slots in list order, using the effective stack maximum (99 when enabled, otherwise the material maximum). Extra preview space shows the padding item or air. Preview capacity is visual; it does not replace `max-amount`. Quantity controls clamp the count to the permitted range. Supply at least one valid confirmation button for the trade direction you intend to support.

## 🎨 Quantity controls by presentation

| Presentation | Quantity UI |
| --- | --- |
| Inventory | Uses `amount-items`, preview markers and `confirm-items` from this file |
| Bedrock Form | Text input, then product information; no Inventory amount buttons |
| Native Dialog / Ore | Native amount input, then product information; no Inventory slot layout |
| ArcMenu | `select-amount` opens product details directly; plus/minus and scrolling adjust the amount |

ArcMenu details require Buy More and a menu, and adjustment requires an available `select-amount` binding. The same product maximum limits it, but it does not read this file's amount/confirmation layout. Right-click defaults to `select-amount` and can be changed in `ui.renderers.arcmenu.right-click-action`.

Without a manual quantity selection, product operations keep `default-buy-amount` and `default-sell-amount` independently. After selection, both directions use the chosen count. `{buy-amount}` and `{sell-amount}` in generated lore reflect that distinction. Explicit display-item stack amounts are separate from the number of trade units. See [Default Trade Amounts](../shops/products.md#default-trade-amounts).

Form and Dialog labels are configured in their native `bedrock` and `dialog` sections; graphical product styling/actions are documented in [Graphical UI Layout](ui-layout.md). Editing an Inventory confirmation label will not rename a Form information action.

## Related documentation

[Menu Configuration](menu-configuration.md) · [UI Modes](ui-overview.md) · [Shop Menus](shop-menus.md)
