# 💬Native Dialog UI - Premium

Native Dialog uses the **Paper 1.21.9+** Dialog API to present native action buttons or item-action lists to Java players. Native dialog layouts **do not require an Ore resource pack** (Ore is a separate graphical layout built on top of the dialog transport).

## Enable Native Dialog

Configure global defaults in `config.yml`:

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

Save a Common menu as `menus/common_menus/categories.yml`:

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

Make sure the `farming` shop already exists. Run `/shop reload`, then run `/shop menu categories` on a supported Java 1.21.9+ client. Under normal selection, both global and menu `dialog.enabled` must be `true` (note: ArcMenu takes priority if also enabled and available; global `menu.force-ui` overrides switches).

## Native Layouts

| Menu `dialog.layout` | Result |
| --- | --- |
| `multi-action` | Native multi-action button grid |
| `item-action-list` | Native item-action list |
| `ore` | Ore graphical UI for supported menu types |

Root `layout` supplies entry order while ignoring empty spacer slots; individual entries can override native item-action behaviour via their own `dialog.layout`. Visibility uses `menu-visibility.dialog` or `menu-visibility.item-action-list` depending on the active layout.

To hide a button's sprite icon, add this at the button root (not inside `display-item` or `ui`):

```yaml
dialog:
  show-sprite: false
```

Global `auto-add-sprite.auto-hide: true` prevents duplicate sprites when an entry already displays an item icon.

## Dialog Text & Controls

| Menu `dialog` Key | Purpose |
| --- | --- |
| `enabled` | Allows Dialog |
| `layout` | Selects native layout or `ore` |
| `content` | Dialog body (falls back to root `content`) |
| `button-width`, `columns` | Native action button width and columns (defaults `150`, `2`) |
| `not-auto-close` | Keeps supported info dialogs open after an action |
| `default-button` | Fallback exit label |
| `info.display-item`, `info.title` | Product info appearance |
| `info.buttons.buy/sell/buy-more/sell-all/back` | Info action labels |
| `buy-more.display-item/title/input/buttons.confirm` | Native quantity input flow |
| `search.input`, `search.buttons.search` | Search controls |
| `favourite-edit.title/content/buttons.*` | Favourite edit controls |
| `details` | Ore product details label |

Shared text and behaviour options fall back to global `menu.dialog.*` (`content`, `button-width`, and `columns` are read from the menu itself; `auto-add-sprite` is global-only). All labels support language references and placeholders.

## Product & Quantity Flow

Selecting a product shows its available actions filtered by click bindings, prices, and eligibility (`ui.products.actions` configures graphical cards, not native dialog buttons).

When the selected Buy More menu enables `dialog.enabled` and the product supports `select-amount`, native quantity input opens (labels come from source shop `dialog.buy-more`); otherwise it falls back to Inventory Buy More. The chosen quantity and any per-open menu/cap overrides persist through input and confirmation.

## Search & Favourites

Search provides keyword input and result info, while Favourites provide browse and edit controls—neither converts physical input slots into native controls, and `dialog.layout: ore` resolves to native `multi-action`.

<a id="ore-layout"></a>

## Ore Layout

Set `dialog.layout: ore` on a Common or Shop menu and configure graphical areas/placement under `ui` (requires [generating the resource pack](ore-menus-premium.md); native `button-width` and `columns` do not position Ore cards).

[UI Modes](ui-overview.md) · [Buy More Menus](buy-more-menus.md) · [Search Menus](search-menus-premium.md) · [Favourite Menus](favourite-menus-premium.md)
