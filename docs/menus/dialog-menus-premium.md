# 💬 Native Dialog menus - Premium

Native Dialog uses the supported Paper Dialog API to display menus as actions or item lists. It is different from [🪨 Ore - Premium](ore-menus-premium.md), which is a graphical renderer built on Dialog.

## Enable native Dialog

Enable the capability in `config.yml`:

```yaml
menu:
  dialog:
    enabled: true
    not-auto-close: true
    auto-add-sprite:
      enabled: true
      auto-hide: true
      format: '<sprite:"{namespace}:{atlas}":{path}>'
```

In your **menu**:

```yaml
title: '&8Categories'
size: 9
layout:
  - '0000A0000'
dialog:
  enabled: true
  layout: multi-action
  content: '&7Choose a category'
  button-width: 150
  columns: 2
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

Both global `menu.dialog.enabled` and menu `dialog.enabled` must be enabled. The server needs a supported Paper Dialog API and the Premium functionality. Java ArcMenu takes precedence if enabled and available; otherwise Dialog is attempted before Inventory.

## Choose a Dialog layout

| Menu `dialog.layout` | Result |
| --- | --- |
| `multi-action` | Native Dialog action buttons |
| `item-action-list` | Native item-based action list |
| `ore` | Switch to the *graphical Ore renderer*, configured separately through `ui` |

The root `layout` identifies source entries; empty Inventory slots do not become empty Dialog rows. One entry can override its native action/list behavior with `buttons.<id>.dialog.layout`. Hide an item in one presentation with `menu-visibility`.

To suppress a material sprite on a particular button:

```yaml
buttons:
  A:
    dialog:
      show-sprite: false
    display-item:
      material: WHEAT
      name: '&aFarming'
    actions:
      1:
        type: shop_menu
        shop: farming
```

This is **button `dialog.show-sprite`**, not `ui.show-sprite` or `display-item.dialog.show-sprite`.

## Native Dialog configuration

Global defaults are under `menu.dialog`, with supported menu overrides under `dialog`:

| Key relative to `dialog` | Effect |
| --- | --- |
| `enabled` | Allow Dialog on this menu |
| `layout` | `multi-action`, `item-action-list` or `ore` |
| `content` | Dialog body, falling back to root `content` |
| `button-width` | Native action width |
| `columns` | Native multi-action column count |
| `not-auto-close` | Supported information actions can keep the Dialog open |
| `auto-add-sprite.enabled/auto-hide/format` | Global material sprite behavior |
| `default-button` | Fallback exit-action label |
| `details` | Ore product details label |
| `info.display-item`, `info.title` | Product information display |
| `info.buttons.buy/sell/buy-more/sell-all/back` | Product information action labels |
| `buy-more.display-item/title/input/buttons.confirm` | Native quantity input flow |
| `search.input`, `search.buttons.search` | Search text input and submit |
| `favourite-edit.title/content/buttons.*` | Favourite editing controls |

Some sprite/resource settings are read globally; changing them on one menu may not affect resource generation. Labels can use language placeholders.

## Product actions and quantity

A product's native Dialog information controls come from its available click bindings and trade eligibility. **`ui.products.actions` only configures graphical Ore/ArcMenu controls**; it does not determine native Dialog buttons.

Quantity selection uses a native numeric input when `select-amount` is available and the product supports Buy More. Inventory Buy More slot controls are not displayed inside Dialog. A product retains its default buy/sell amount until a different quantity is selected.

Native Search provides keyword input and results; Favourite supports normal and editing screens. Neither reproduces Inventory item-input slots as native widget slots.

## Switching to Ore

Set **menu `dialog.layout: ore`** and configure its canvas and entry placements under `ui`. The graphical Ore renderer also needs its resource pack. Do not place native Dialog `layout` or `button-width` under `ui.renderers.dialog`.

[🪨 Ore - Premium](ore-menus-premium.md) · [🎨 Presentation selection](ui-overview.md) · [🔍 Search - Premium](search-menus-premium.md) · [❤️ Favourite - Premium](favourite-menus-premium.md).
