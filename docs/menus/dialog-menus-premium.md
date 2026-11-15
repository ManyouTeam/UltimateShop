# 💬 Native Dialog UI — Premium

Native Dialog uses supported Paper 1.21.9+ APIs for Java action buttons and item-action lists. These native layouts do not require Ore's generated resource pack. Ore is a separate graphical layout built on the Dialog route.

## Enable native Dialog

In `config.yml`:

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

Save this Common menu in `menus/common_menus/categories.yml`:

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
arcmenu:
  enabled: false
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

Create `farming`, reload, then open `/shop menu categories` on a compatible Java client. Normal selection requires global/menu Dialog enablement. ArcMenu takes priority when enabled and available; global forced presentation selection overrides the normal switches.

## Native layouts

| Menu `dialog.layout` | Result |
| --- | --- |
| `multi-action` | Native action buttons |
| `item-action-list` | Native item-action list |
| `ore` | Graphical Ore for supported menu types |

Root `layout` supplies source entries and order; empty Inventory slots do not produce blank Dialog rows. Individual entries can override native item-action behavior with their own `dialog.layout`. Visibility uses `menu-visibility.dialog` or `menu-visibility.item-action-list` for the active layout.

To suppress a material sprite on one button, add this to the button root:

```yaml
dialog:
  show-sprite: false
```

`dialog.show-sprite` overrides automatic label-sprite selection. The global `auto-add-sprite.auto-hide` rule avoids duplicating label sprites when an item icon is already shown. These options belong outside `display-item` and outside graphical `ui`.

## Dialog text and controls

| Menu-relative key | Purpose |
| --- | --- |
| `enabled` | Allow Dialog |
| `layout` | Native or Ore selector |
| `content` | Body text; local root `content` fallback |
| `button-width`, `columns` | Menu-local native action dimensions; defaults `150` and `2` |
| `not-auto-close` | Keep supported information actions open |
| `default-button` | Fallback exit label |
| `info.display-item`, `info.title` | Product information appearance |
| `info.buttons.buy/sell/buy-more/sell-all/back` | Information action labels |
| `buy-more.display-item/title/input/buttons.confirm` | Quantity input flow |
| `search.input`, `search.buttons.search` | Search controls |
| `favourite-edit.title/content/buttons.*` | Favourite editing controls |
| `details` | Ore product-details label |

Supported shared text/behavior options fall back to global `menu.dialog.*`; `content`, `button-width` and `columns` are read from the menu itself. Automatic sprite settings are global under `menu.dialog.auto-add-sprite`. Labels support normal language references and placeholders.

## Product and quantity flow

Selecting a product exposes available information actions. Operations are filtered by click bindings, prices and product eligibility. `ui.products.actions` configures graphical cards rather than native Dialog action buttons.

Quantity selection uses native input when the selected Buy More file enables Dialog and the product supports `select-amount`. Otherwise it uses the supported fallback. The source shop's `dialog.buy-more` text controls input labels; the Buy More file's Inventory amount buttons are not native widgets.

Until quantity selection, buy and sell use their independent default amounts. Selected quantities and per-opening menu/maximum overrides persist through native input and confirmation.

## Search and Favourite

Search provides keyword input and result information. Favourite provides normal and editing controls. Neither converts Inventory physical deposit slots into native controls. For these menu types, `dialog.layout: ore` resolves to native `multi-action`.

<a id="ore-layout"></a>

## Ore layout

Select menu `dialog.layout: ore`, then configure graphical areas and placements under `ui`. Common and Shop support Ore; it also requires the [generated resource pack](ore-menus-premium.md). Native `button-width` and `columns` do not position Ore cards.

[UI Modes](ui-overview.md) · [Buy More](buy-more-menus.md) · [Search](search-menus-premium.md) · [Favourite](favourite-menus-premium.md)
