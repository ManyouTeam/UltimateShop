# 🛏️ Bedrock Form menus - Premium

Bedrock Form displays ordinary menu buttons as a **list**, with dedicated product information and quantity input forms. The same products, prices and click rules are shared with Java interfaces; Form does **not** use Ore/ArcMenu graphical coordinates.

## Enable Form

In `config.yml`:

```yaml
menu:
  bedrock:
    enabled: true
    check-method: FLOODGATE
    not-auto-close: true
    auto-add-icon:
      enabled: true
      format: 'https://raw.githubusercontent.com/InventivetalentDev/minecraft-assets/refs/heads/{version}/assets/minecraft/textures/{path}.png'
    price-extra-line:
      default: '{lang}'
      only-buy: '{lang}'
      only-sell: '{lang}'
```

In a menu file:

```yaml
title: '&8Farming'
size: 9
layout:
  - '0000A0000'
bedrock:
  enabled: true
  content: '&7Choose a category'
buttons:
  A:
    display-item:
      material: WHEAT
      name: '&aFarming'
    actions:
      1:
        type: shop_menu
        shop: farming
```

Both the global and menu `bedrock.enabled` switches must allow Form. The server needs a supported Bedrock/Floodgate integration and the Premium functionality. If Form cannot be used, the usual Inventory interface is available.

## Set a custom button icon or second line

These keys belong **inside the active `display-item`**, not the root `ui` or the product's `bedrock.click-event`:

```yaml
buttons:
  A:
    display-item:
      material: WHEAT
      name: '&aFarming'
      bedrock:
        icon: 'path;;textures/items/wheat.png'
        extra-line: '&7Seeds, crops and food'
        hide: false
    actions:
      1:
        type: shop_menu
        shop: farming
```

| `display-item.bedrock` field | Meaning |
| --- | --- |
| `icon` | `path;;<client texture path>` or `url;;<public image URL>` |
| `extra-line` | Additional button line; price placeholders are supported for products |
| `hide` | `true` hides this particular Form button |

A `path;;` texture must exist **in the Bedrock client's resources**, not on the server disk. A `url;;` icon must be reachable by that client. Conditional display items should put icon settings on each intended variant.

Without an explicit icon, `menu.bedrock.auto-add-icon.enabled: true` attempts to resolve the vanilla material texture using its URL format. This does not resolve Java custom model data automatically. A button with an empty name, or `bedrock.hide: true`, can be omitted from the Form list.

## Product prices and click behavior

The price/extra line for products reads the active `display-item.bedrock.extra-line` first. Otherwise it uses the menu's `bedrock.price-extra-line` and then global `menu.bedrock.price-extra-line`:

```yaml
bedrock:
  price-extra-line:
    default: '&7Buy: {buy-price} | Sell: {sell-price}'
    only-buy: '&7Buy: {buy-price}'
    only-sell: '&7Sell: {sell-price}'
```

Set `display-item.bedrock.extra-line: ''` for an intentionally blank supplemental line. `{lang}` can use the matching language override.

A shop product normally opens an information Form. To execute an action directly instead, set the product-level shortcut in the **shop file**:

```yaml
items:
  A:
    bedrock:
      click-event: buy
```

This is separate from `display-item.bedrock.*`. The menu-level default is `bedrock.click-event`, and the global default is `menu.bedrock.click-event`. Trade availability, product conditions and click bindings still apply.

## Form configuration reference

Native Form defaults live under global `menu.bedrock`; override supported values with menu `bedrock`, except options read globally such as player detection.

| Key relative to `bedrock` | Effect |
| --- | --- |
| `enabled` | Allow Form for this menu |
| `content` | Menu description, root `content` as fallback |
| `check-method` | Global Bedrock-player detection method, e.g. `FLOODGATE` |
| `not-auto-close` | Reopen after supported information Form operations |
| `auto-add-icon.enabled` | Attempt vanilla material icons |
| `auto-add-icon.format` | URL template using `{version}` and `{path}` |
| `price-extra-line.default/only-buy/only-sell` | Product supplemental price text |
| `click-event` | Default direct shop-product action |
| `info.title` | Product information title |
| `info.buttons.buy/sell/buy-more/sell-all/back` | Information actions |
| `buy-or-sell.title` | Quantity input form title |
| `buy-or-sell.buttons.amount.name/tip` | Quantity input label and hint |
| `search.input`, `search.buttons.search` | Search form text |
| `favourite-edit.title/content`, `favourite-edit.buttons.*` | Favourite editing form text |

The generated product action buttons come from available click bindings, **not** from graphical `ui.products.actions`. Form quantity input does not reuse the Inventory Buy More button grid.

## Visibility and related menus

```yaml
menu-visibility:
  form: false
```

Put this at the button or product root to hide only that entry in Form. Unspecified presentations default to visible. Form uses entries from the menu's root `layout` but ignores empty chest-grid spacing; decorative fillers usually should be hidden.

Search and Favourite have dedicated Form flows and reuse their data from the corresponding menu files. Physical Item Sell and Sell All remain Inventory screens.

[🎨 Presentation selection](ui-overview.md) · [🔍 Search - Premium](search-menus-premium.md) · [❤️ Favourite - Premium](favourite-menus-premium.md) · [🩺 Troubleshooting](troubleshooting.md).
