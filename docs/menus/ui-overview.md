# 🎨UI Modes

Menus can be presented as Inventory slots, native Form/Dialog controls, or graphical cards. A menu's **type** determines which presentations it supports; switching presentations changes only the UI, never product prices, conditions, or saved data.

## Available Presentations

| Presentation | Client & Requirements | Layout Setup |
| --- | --- | --- |
| [Inventory](inventory-ui.md) | Java & Bedrock, standard chest screen | Root `size`, `layout`, `buttons` |
| [Bedrock Form - Premium](bedrock-menus-premium.md) | Bedrock with supported Geyser / Floodgate | `bedrock.*`; entries become a list |
| [Native Dialog - Premium](dialog-menus-premium.md) | Java on supported Paper 1.21.9+ | `dialog.layout: multi-action` or `item-action-list` |
| [Ore - Premium](ore-menus-premium.md) | Supported Paper Dialog runtime + generated resource pack | `dialog.layout: ore` + graphical `ui` |
| [ArcMenu - Premium](arcmenu-menus-premium.md) | Java with compatible ArcMenu plugin & client setup | `arcmenu.enabled` + graphical `ui` |

Native `multi-action` and `item-action-list` do not require an Ore resource pack. ArcMenu client requirements follow its own integration.

## Normal Selection Order

Global switches in `config.yml`:

```yaml
menu:
  dialog:
    enabled: true
  bedrock:
    enabled: true
  arcmenu:
    enabled: false
```

Individual menu files can restrict further:

```yaml
dialog:
  enabled: true
  layout: multi-action
bedrock:
  enabled: true
arcmenu:
  enabled: false
```

* **Java players**: tried in order **ArcMenu → Dialog / Ore → Inventory**. If ArcMenu is unavailable, it falls back to enabled and supported Dialog or Inventory; `dialog.layout` chooses native layout vs. Ore.
* **Bedrock players**: checked separately for Bedrock Form. To keep both clients on Inventory, use `force-ui` below.

By default, a menu's `dialog.enabled` and `bedrock.enabled` are `true`, while `arcmenu.enabled` is `false`. Enabling a global switch alone does not guarantee runtime or menu-type compatibility.

<a id="supported-menu-types"></a>

## Supported Menu Types

| Menu Type | Inventory | Bedrock Form | Native Dialog | Ore | ArcMenu |
| --- | --- | --- | --- | --- | --- |
| Common / Shop | Yes | Yes | Yes | Yes | Yes |
| Search / Favourite | Yes | Yes | Yes | Falls back to native `multi-action` | Yes |
| Buy More | Slot controls | Source shop quantity form | Native quantity input | No separate graphical UI | Source screen product details |
| Item Sell / Sell All | Yes | No | No | No | No |
| In-Game Editor | Yes | No | No | No | No |

Notes:
* Search and Favourite menus with `dialog.layout: ore` fall back to native `multi-action` and generate no Ore assets.
* Buy More's Inventory layout is not converted into Form or graphical canvases.
* Item Sell and Sell All always open a physical Inventory screen, even when triggered from another presentation.

<a id="force-one-presentation"></a>

## Force One Presentation

Force a presentation globally in `config.yml`:

```yaml
menu:
  force-ui:
    enabled: true
    type: inventory
    style: ''
```

This overrides global and per-menu switches while still respecting platform, version, integration, and menu-type requirements:

| Forced `type` | Java | Bedrock |
| --- | --- | --- |
| `inventory` | Inventory | Inventory |
| `arcmenu` | ArcMenu when available, else Inventory | Inventory |
| `dialog` | Native Dialog when available (Ore also becomes `multi-action`) | Inventory |
| `item-action-list` | Native `item-action-list` when supported | Inventory |
| `ore` | Ore for Common/Shop; native `multi-action` for Search/Favourite | Inventory |
| `form` | Inventory | Bedrock Form when available |

Unsupported types still fall back (for example, Item Sell and Sell All stay Inventory; forcing `dialog` leaves existing `item-action-list` menus as `item-action-list`).

Set `style` to a style ID like `bedrock` to force graphical defaults too, or leave `''` to keep per-menu style selection; see [Menu Configuration & Inheritance](menu-configuration.md#force-a-presentation-and-style). Invalid `type` values log a config error and fall back to normal selection.

## Configure Layout, Content, and Visibility

| What to Change | Where |
| --- | --- |
| Inventory slot positions | Root `size`, `layout` |
| Native Dialog controls | `dialog.*` |
| Bedrock Form text & behaviour | `bedrock.*` |
| Graphical style | Global `menu.ui.style` or menu `ui.style` |
| Ore / ArcMenu coordinates | `ui.layout` and entry `ui.placement` |
| Product card defaults | `ui.products.*` |
| Hide an entry in one presentation | Entry `menu-visibility.<presentation>` |

Root `content` is the shared fallback description, overridden by `dialog.content`, `bedrock.content`, or `arcmenu.content`. Graphical `ui` controls drawing and layout only—there is no `ui.mode` selector.

## Text Width in ArcMenu and Dialog

UltimateShop calculates text width using `plugins/UltimateShop/glyph-widths.yml`. If your server uses non-English characters, default widths may cause truncation, unexpected wrapping, or misalignment in ArcMenu or Dialog (including Ore).

Configure pixel widths in `glyph-widths.yml` under `shared.width` for single code points (e.g. `U+0041`) or inclusive ranges (e.g. `U+0041-U+005A`); later rules override earlier ones, and `ore.width` overrides `shared.width` for Ore. Run `/shop reload` and reopen the menu to verify.

[Menu System](README.md) · [Layout & Buttons](layout-and-buttons.md) · [Graphical UI Layout](../menus-getting-started/ui-layout.md)
