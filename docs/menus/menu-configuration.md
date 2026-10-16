# ⚙️ Configuration scopes and inheritance

**Rule:** Ordinary behavior lives in native menu keys; the `ui` section contains **only Ore/ArcMenu graphics**. Do not move `dialog` or `bedrock` settings into `ui`.

## 🧭 Where to put a key

| Scope | File / path | Controls |
| --- | --- | --- |
| Global defaults | `config.yml` → `menu.dialog`, `menu.bedrock`, `menu.arcmenu` | Availability, native form/dialog behavior and text |
| Global graphics | `config.yml` → `menu.ui` | Ore/ArcMenu canvas, cards, colors and renderer graphics |
| One menu | `menus/<folder>/<id>.yml` | Title, inventory rows, buttons, native presentations |
| One menu's graphics | Menu root `ui` | Graphical layout/appearance overrides |
| One shop's screen overrides | `shops/<shop>.yml` → `settings.menu-settings` | Overrides for its menu template |
| One button or product | `buttons.<id>` / `items.<id>` | Actions, display-item, `menu-visibility`, per-entry graphical placement |

## ✅ Example: global defaults

In `config.yml`:

```yaml
menu:
  dialog:
    enabled: true
    not-auto-close: true
  bedrock:
    enabled: true
    check-method: FLOODGATE
  arcmenu:
    enabled: false
  ui:
    layout:
      canvas:
        width: 648
        height: 414
    products:
      placement:
        area: content
    theme:
      background: '#EF0C1223'
```

## ✅ Example: one shop's menu

In `menus/shop_menu_templates/farming.yml`:

```yaml
title: '{shop-name}'
size: 9
layout:
  - '0000A0000'
dialog:
  enabled: true
  layout: ore
  content: '&7Pick an item'
bedrock:
  enabled: true
  content: '&7Choose an item'
arcmenu:
  enabled: false
ui:
  products:
    card:
      icon-size: 32
```

This example demonstrates the **locations** of settings. A usable Ore layout also needs appropriate graphical areas and product placements; see [📐 Graphical layout](ui-layout.md).

## 🔀 Precedence: native settings vs graphical settings

**Native Dialog/Form text and behavior:** menu `dialog.*` / `bedrock.*` → global `menu.dialog.*` / `menu.bedrock.*` → method fallback. Different text screens use the relevant menu (for example, search text belongs to the search menu).

**Graphical menu defaults:** the shop template is loaded, `settings.menu-settings` overlays it, then missing `ui` values are filled recursively from global `menu.ui`.

**Graphical product settings:** product `ui.actions` / `ui.price` → owning shop menu `ui.products.actions` / `ui.products.price` → global `menu.ui.products`. These graphical action/price options do *not* configure native Form or Dialog.

| Never confuse | With |
| --- | --- |
| Root `layout` (Inventory slots) | `ui.layout` (Ore/ArcMenu canvas) |
| `dialog.layout: ore` (renderer selection) | `ui.layout.areas` (graphical arrangement) |
| `display-item.bedrock.icon` (Form button icon) | `ui.products.card.icon-size` (graphical item size) |
| `menu-visibility.*` (presentation filter) | `ui.placement.enabled` (graphical placement only) |

## 🛠️ In-game Editor

The Editor uses the same YAML keys. Menu presets expose `dialog.enabled/layout/content/button-width/columns`, `bedrock.enabled/content` and `arcmenu.enabled/content`. Button and product presets expose `dialog.show-sprite` and the `menu-visibility` section with one boolean value for each supported presentation.

The Editor's `ui` page edits **only graphical** coordinates, cards, themes and drawing settings. It does not offer `ui.mode` or native Form/Dialog renderers under `ui`. Edit advanced/unsupported fields through the Editor's raw section editor if necessary.

## 🔧 Other global menu settings

The rest of `config.yml` → `menu` controls features beyond renderer selection:

| Key | What it affects |
| --- | --- |
| `click-event`, `click-event-actions` | Product click-to-action mapping |
| `cooldown` | Click and reopening throttling |
| `menu-update`, `title-update` | Inventory refresh and optional title updates |
| `buy-more-menu` | Quantity selection defaults |
| `sell-all` | Sell All Inventory |
| `search-gui`, `favourite-gui` | Search and Favourite behavior |
| `auto-open` | Automatic menu opening |
| `anti-dupe-checker` | Inventory anti-dupe behavior |

Use the `config.yml` shipped with your plugin for every key and default; this article explains **where** keys belong, not a replacement for the complete configuration file.

## 🔗 Continue

[UI selection](ui-overview.md) · [Common](general-menus.md) · [Shop](shop-menus.md) · [Graphical layout](ui-layout.md) · [Troubleshooting](troubleshooting.md).
