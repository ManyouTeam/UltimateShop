# 🎨 Menu presentations

A **presentation** is the client interface used to show an existing Common, Shop, Search or Favourite menu. It does not change product prices, click bindings, conditions or data storage.

## ✅ Choose with native switches, not `ui.mode`

In `config.yml`, enable the presentations that the server should support:

```yaml
menu:
  dialog:
    enabled: true
  bedrock:
    enabled: true
  arcmenu:
    enabled: false
```

In an individual menu, choose the layout and enable its intended presentations:

```yaml
title: '&8Farming'
size: 9
layout:
  - '0000A0000'
dialog:
  enabled: true
  layout: ore
  content: '&7Browse our products'
bedrock:
  enabled: true
  content: '&7Browse our products'
arcmenu:
  enabled: false
```

| Interface | Required selection | Additional requirement |
| --- | --- | --- |
| [📦 Inventory](inventory-ui.md) | Fallback when another presentation is disabled/unavailable | None beyond the menu itself |
| [🛏️ Bedrock Form](bedrock-menus-premium.md) | Global `menu.bedrock.enabled` **and** menu `bedrock.enabled` | Premium, supported Bedrock/Floodgate integration |
| [💬 Native Dialog](dialog-menus-premium.md) | Global `menu.dialog.enabled`, menu `dialog.enabled`, `dialog.layout: multi-action` or `item-action-list` | Premium, supported Paper Dialog API |
| [🪨 Ore](ore-menus-premium.md) | Global/menu Dialog enabled; `dialog.layout: ore` | Premium, supported Paper and generated client resource pack |
| [✨ ArcMenu](arcmenu-menus-premium.md) | Global `menu.arcmenu.enabled` **and** menu `arcmenu.enabled` | Premium and ArcMenu integration |

When a menu omits `dialog.enabled` or `bedrock.enabled`, it defaults to `true`. ArcMenu is **opt-in**: its menu switch defaults to `false`.

### Java priority

**ArcMenu → Dialog / Ore → Inventory.** If ArcMenu fails to open, the normal menu-opening flow can try Dialog/Ore when enabled and supported. If neither graphical/native route applies, Inventory is used. Bedrock Form is selected separately for Bedrock players.

`dialog.layout` is a *menu-level selector*: `multi-action`, `item-action-list` or `ore`. It does not configure pixel coordinates.

## 🧭 Where does each configuration belong?

| What you want to change | Correct key |
| --- | --- |
| Inventory rows and buttons | Root `size`, `layout`, `buttons` |
| Native Dialog mode, text and controls | Menu `dialog.*`, global `menu.dialog.*` |
| Bedrock Form enablement, button text and icons | Menu `bedrock.*`, global `menu.bedrock.*`, `display-item.bedrock.*` |
| ArcMenu enablement | `menu.arcmenu.enabled` and menu `arcmenu.enabled` |
| Ore/ArcMenu coordinates and card appearance | `menu.ui.*` and menu `ui.*` |
| Show or hide a product in one presentation | Entry `menu-visibility.<presentation>` |

`ui.mode`, `ui.visible-in`, `ui.show-sprite` and native Form/Dialog `ui.renderers` fields are **not supported**. The `ui` section is reserved for graphical drawing and placement.

## ⚠️ Menus that need a physical inventory

Item Sell and Sell All use a real Inventory container, even when opened from another type of screen. Inventory Buy More buttons are not automatically converted into Form or Dialog widgets; see [Buy More](buy-more-menus.md).

Related: [Configuration scopes](menu-configuration.md) · [Graphical layout](ui-layout.md) · [Troubleshooting](troubleshooting.md).
