# 🎨 UI Modes

A presentation displays a menu using Inventory slots, native controls or graphical cards. Menu type determines which presentations are supported. Changing the presentation retains product prices, conditions and saved data.

## Available presentations

| Presentation | Client and requirements | Layout settings |
| --- | --- | --- |
| [Inventory](inventory-ui.md) | Java or Bedrock; standard menu interface | Root `size`, `layout` and `buttons` |
| [Bedrock Form — Premium](bedrock-menus-premium.md) | Bedrock with a supported Geyser/Floodgate setup | `bedrock.*`; entries become a list |
| [Native Dialog — Premium](dialog-menus-premium.md) | Java on supported Paper 1.21.9+ | `dialog.layout: multi-action` or `item-action-list` |
| [Ore — Premium](ore-menus-premium.md) | Supported Paper Dialog runtime and generated client pack | `dialog.layout: ore`, plus graphical `ui` |
| [ArcMenu — Premium](arcmenu-menus-premium.md) | Java with a compatible ArcMenu installation and client setup | `arcmenu.enabled`, plus graphical `ui` |

The native `multi-action` and `item-action-list` layouts do not require Ore's generated resource pack. ArcMenu follows its own integration's client requirements.

## Normal presentation selection

Global switches in `config.yml` enable the available routes:

```yaml
menu:
  dialog:
    enabled: true
  bedrock:
    enabled: true
  arcmenu:
    enabled: false
```

Individual menu files can narrow those choices:

```yaml
dialog:
  enabled: true
  layout: multi-action
bedrock:
  enabled: true
arcmenu:
  enabled: false
```

For Java players, the opening order is **ArcMenu → Dialog/Ore → Inventory**. When ArcMenu cannot open, an enabled and supported Dialog route may still be used. The menu's `dialog.layout` selects the native layout or Ore.

Bedrock Form is checked separately for detected Bedrock players. To guarantee Inventory for both clients, use the forced Inventory setting below. Menu `dialog.enabled` and `bedrock.enabled` default to `true`; `arcmenu.enabled` defaults to `false`. Global enablement alone does not guarantee a compatible runtime or menu type.

## Supported menu types

| Menu type | Inventory | Bedrock Form | Native Dialog | Ore | ArcMenu |
| --- | --- | --- | --- | --- | --- |
| Common / Shop | Yes | Yes | Yes | Yes | Yes |
| Search / Favourite | Yes | Yes | Yes | Native multi-action fallback | Yes |
| Buy More | Dedicated slot controls | Source shop's quantity Form | Native quantity input | No separate graphical screen | Source screen's product details |
| Item Sell / Sell All | Yes | No | No | No | No |
| In-game editor | Yes | No | No | No | No |

Search/Favourite with `dialog.layout: ore` use native `multi-action` and do not generate Ore assets. Buy More's Inventory layout is not rendered as a Form or graphical canvas. Item Sell/Sell All always provide physical Inventory slots, including when launched from another presentation.

## Force one presentation

Use `config.yml` to choose a presentation across menus:

```yaml
menu:
  force-ui:
    enabled: true
    type: inventory
    style: ''
```

This overrides global and menu-local presentation enable switches. It preserves platform, edition, integration and menu-type requirements.

| Forced `type` | Java | Bedrock |
| --- | --- | --- |
| `inventory` | Inventory | Inventory |
| `arcmenu` | ArcMenu when available; otherwise Inventory | Inventory |
| `dialog` | Native Dialog when available | Inventory |
| `item-action-list` | Native item-action-list when supported | Inventory |
| `ore` | Ore for Common/Shop; native multi-action for Search/Favourite | Inventory |
| `form` | Inventory | Form when available |

Unsupported routes retain their supported fallback. Item Sell/Sell All remain Inventory. Buy More may use native quantity input for a forced Dialog route; ArcMenu quantity controls remain part of product details. Forcing `dialog` replaces an Ore layout with `multi-action`; an existing native item-action-list selection remains native.

Set `style` to a style ID such as `bedrock` to force graphical defaults too, or leave it empty to retain normal style selection. See [Configuration & Inheritance](menu-configuration.md#force-a-presentation-and-style). An invalid `type` reports a configuration error and restores normal selection. Reload after changing the setting.

## Configure layout, content and visibility

| Change | Key |
| --- | --- |
| Inventory positions | Root `size`, `layout` |
| Native Dialog controls | `dialog.*` |
| Bedrock Form text and behavior | `bedrock.*` |
| Graphical style | Global `menu.ui.style` or menu `ui.style` |
| Ore/ArcMenu coordinates | `ui.layout`, entry `ui.placement` |
| Product card defaults | `ui.products.*` |
| Hide one entry in a presentation | Entry `menu-visibility.<presentation>` |

Root `content` is the shared description fallback; `dialog.content`, `bedrock.content` and `arcmenu.content` override it for their route. The graphical `ui` section controls drawing and placement. It does not select a presentation with `ui.mode`.

[Menu System](README.md) · [Layout & Buttons](layout-and-buttons.md) · [Graphical UI Layout](../menus-getting-started/ui-layout.md)
