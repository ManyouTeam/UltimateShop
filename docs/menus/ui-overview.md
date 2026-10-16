# 🎨 UI Modes Overview

A **menu type** decides what a menu does; its **presentation** determines what players see. All presentations share product, action and permission rules. See [Menu System](README.md).

## Select presentations

In `config.yml`:

```yaml
menu:
  dialog:
    enabled: false
  bedrock:
    enabled: true
  arcmenu:
    enabled: false
```

In an individual menu:

```yaml
dialog:
  enabled: true
  layout: ore
  content: '&7Choose a product.'
bedrock:
  enabled: true
  content: '&7Choose a product.'
arcmenu:
  enabled: false
```

A presentation needs its global setting and the menu setting enabled. An omitted menu `dialog.enabled` or `bedrock.enabled` defaults to true; ArcMenu is opt-in (`arcmenu.enabled: false` by default). Java presentation priority is **ArcMenu → Dialog/Ore → Inventory**. If ArcMenu is not available, enabled Dialog/Ore is attempted next. Bedrock Form is selected separately.

Java native Dialog supports `dialog.layout: multi-action` and `item-action-list`; `dialog.layout: ore` selects the current Ore renderer, whose graphical layout is configured under `ui.layout`. The graphical `ui` section does not select presentation modes; `ui.mode` is not supported.

## UI comparison

| Mode | Client | Requirements and behavior |
| --- | --- | --- |
| [Inventory](inventory-ui.md) | Java / Bedrock | Traditional slot-based inventory |
| [Bedrock Form](bedrock-menus-premium.md) | Bedrock | Premium and Floodgate integration; button list and input forms |
| [Native Dialog](dialog-menus-premium.md) | Java | Premium, supported Paper Dialog APIs; `multi-action` or `item-action-list` |
| [Ore](ore-menus-premium.md) | Java | Premium, supported Paper and generated resource pack; graphical Dialog |
| [ArcMenu](arcmenu-menus-premium.md) | Java | Premium, compatible ArcMenu integration and client resources |

Buy More uses Inventory quantity controls in Inventory mode, a quantity input in Form/native Dialog, and ArcMenu's product-details controls where applicable. Item Sell and Sell All require a physical Inventory deposit interface.

## Visibility, placement and scope

Use entry-level `menu-visibility` boolean keys (`inventory`, `form`, `dialog`, `item-action-list`, `ore`, `arcmenu`) to filter a button per presentation. The omitted setting defaults to visible. Ore and item-action-list inherit the `dialog` value when not specified separately.

Only **graphical** options belong inside `menu.ui` and menu `ui`: the canvas (`layout`), graphical product/card/action styling (`products`), themes, graphics-only labels, Ore templates and ArcMenu renderer drawing options. Entry-level `ui.placement`, `ui.appearance`, `ui.font-size` and `ui.text-align` are also graphical. Traditional root Inventory `layout`, native Dialog options under `dialog`, and Form options under `bedrock` remain separate.

[Menu Configuration](menu-configuration.md) · [Graphical Layout](ui-layout.md) · [Migration](migration-guide.md)
