# 🎨 UI Modes Overview

A **presentation** is the client interface used to show an existing Common, Shop, Search or Favourite menu. It does not change product prices, click bindings, conditions or data storage.

## Choose with native switches, not `ui.mode`

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

### Inventory

[Inventory](inventory-ui.md) is the fallback when another presentation is disabled or unavailable. It has no additional edition requirement beyond the menu itself.

### Bedrock Form <mark style="color:red;">- Premium</mark>

[Bedrock Form - Premium](bedrock-menus-premium.md) requires global `menu.bedrock.enabled` and menu `bedrock.enabled`, with a supported Bedrock/Floodgate integration.

### Native Dialog <mark style="color:red;">- Premium</mark>

[Native Dialog - Premium](dialog-menus-premium.md) requires global `menu.dialog.enabled`, menu `dialog.enabled` and a supported Paper Dialog API. Set `dialog.layout` to `multi-action` or `item-action-list`.

### Ore <mark style="color:red;">- Premium</mark>

[Ore - Premium](ore-menus-premium.md) requires global/menu Dialog enabled, `dialog.layout: ore`, supported Paper and a generated client resource pack.

### ArcMenu <mark style="color:red;">- Premium</mark>

[ArcMenu - Premium](arcmenu-menus-premium.md) requires global `menu.arcmenu.enabled`, menu `arcmenu.enabled` and a compatible ArcMenu integration.

When a menu omits `dialog.enabled` or `bedrock.enabled`, it defaults to `true`. ArcMenu is **opt-in**: its menu switch defaults to `false`.

### Java priority

**ArcMenu → Dialog / Ore → Inventory.** If ArcMenu fails to open, the normal menu-opening flow can try Dialog/Ore when enabled and supported. If neither graphical/native route applies, Inventory is used. Bedrock Form is selected separately for Bedrock players.

`dialog.layout` is a _menu-level selector_: `multi-action`, `item-action-list` or `ore`. It does not configure pixel coordinates.

## Supported menu types

| Menu type | Inventory | Bedrock Form | Native Dialog | Ore graphics | ArcMenu graphics |
| --- | --- | --- | --- | --- | --- |
| Common / Shop | Yes | Yes | Yes | Yes | Yes |
| Search / Favourite | Yes | Yes | Yes | No; uses native multi-action Dialog | Yes |
| Buy More file | Slot controls | Uses the source shop's quantity Form | Quantity input when this file enables Dialog | No | No |
| Item Sell / Sell All | Yes | No | No | No | No |
| In-game editor | Yes | No | No | No | No |

Search and Favourite keep their ArcMenu `ui` layout but do not inherit Ore renderer templates. Setting `dialog.layout: ore` on either type uses a native `multi-action` Dialog; these menus do not produce Ore pack assets. Buy More does not inherit graphical defaults. ArcMenu's `select-amount` operation adjusts quantity in the source screen's product details, rather than rendering a Buy More file.

The in-game editor limits its preset fields to these capabilities, including typed menus in `common_menus`. Existing unsupported fields remain available through raw section editing for cleanup.

## Where does each configuration belong?

| What you want to change                        | Correct key                                                         |
| ---------------------------------------------- | ------------------------------------------------------------------- |
| Inventory rows and buttons                     | Root `size`, `layout`, `buttons`                                    |
| Native Dialog mode, text and controls          | Menu `dialog.*`, global `menu.dialog.*`                             |
| Bedrock Form enablement, button text and icons | Menu `bedrock.*`, global `menu.bedrock.*`, `display-item.bedrock.*` |
| ArcMenu enablement                             | `menu.arcmenu.enabled` and menu `arcmenu.enabled`                   |
| Ore/ArcMenu coordinates and card appearance    | `menu.ui.*` and menu `ui.*`                                         |
| Show or hide a product in one presentation     | Entry `menu-visibility.<presentation>`                              |

`ui.mode`, `ui.visible-in`, `ui.show-sprite` and native Form/Dialog `ui.renderers` fields are **not supported**. The `ui` section is reserved for graphical drawing and placement.

## Menus that need a physical inventory

Item Sell and Sell All use a real Inventory container, even when opened from another type of screen. Inventory Buy More buttons are not automatically converted into Form or Dialog widgets; see [Buy More](buy-more-menus.md).

Related: [Configuration scopes](menu-configuration.md) · [Graphical layout - Premium](../menus-getting-started/ui-layout.md) · [Troubleshooting](troubleshooting.md).

## Force one presentation globally

```yaml
menu:
  force-ui:
    enabled: false
    type: inventory
```

Enable `menu.force-ui.enabled` in `config.yml` to override individual menu and global presentation enable switches. Supported types are `inventory`, `arcmenu`, `dialog`, `ore`, `item-action-list` and `form`. Disabling the switch restores normal presentation selection. Invalid types report a configuration error and use normal selection.

`inventory` applies to both Java and Bedrock. `arcmenu`, `dialog`, `ore` and `item-action-list` target Java; incompatible Bedrock clients use Inventory. `form` targets Bedrock, with Inventory for Java. Existing platform, edition and integration requirements still apply. Unsupported menu types retain their supported fallback: Item Sell and Sell All use physical inventories; Buy More supports Inventory/Dialog and ArcMenu product details; Search/Favourite use native Dialog when Ore is unavailable for that type. Missing ArcMenu falls back to Inventory. Forcing native `dialog` also replaces an Ore layout with `multi-action`.

Reload the plugin after changing the setting so loaded menu definitions are rebuilt.
