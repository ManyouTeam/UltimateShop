# 🎨 UI Modes Overview

A **menu type** determines what a menu does; an **UI mode** determines its client presentation. Changing presentation does not change shops, products, prices or folder names. See [Menu System](README.md) for menu types.

## Select the mode

Global defaults in `config.yml`:

```yaml
menu:
  ui:
    mode:
      java: inventory
      bedrock: form
```

Per-menu overrides at the menu root:

```yaml
ui:
  mode:
    java: dialog
    bedrock: inherit
```

`inherit` uses the global mode. Java accepts `inventory`, `dialog`, `ore`, `arcmenu`. Bedrock accepts `inventory` and `form`. Invalid values report an error and resolve to Inventory; unsupported integrations and types may fall back to Inventory.

## UI comparison

| Mode | Client | Requirements and behavior |
| --- | --- | --- |
| [Inventory](inventory-ui.md) | Java / Bedrock | Traditional slot-based inventory; Java default |
| [Bedrock Form](bedrock-menus-premium.md) | Bedrock | Premium and Floodgate integration; list, information and input forms |
| [Native Dialog](dialog-menus-premium.md) | Java | Premium, Paper 1.21.9+; `multi-action` or `item-action-list` |
| [Ore](ore-menus-premium.md) | Java | Premium, supported Paper and generated resource pack; graphical Dialog-based cards |
| [ArcMenu](arcmenu-menus-premium.md) | Java | Premium, compatible ArcMenu plugin and client resources |

## Supported menu behavior

- **Common, Shop, Search and Favourite** use the applicable implemented presentations, subject to Premium/server/integration requirements.
- **Buy More** displays Inventory-specific amount buttons in Inventory mode. Form and native Dialog use a quantity input; ArcMenu uses its details panel. Layouts are not identical across modes.
- **Item Sell and Sell All** always require an Inventory deposit interface, because real items are inserted into slots.
- **Native Dialog** `ui.renderers.dialog.layout` accepts `multi-action` or `item-action-list`. `ore` is chosen through `ui.mode.java`, not that list layout setting.

Entry-level `ui.visible-in` filters the allowed presentations independently of the selected mode. Values include `inventory`, `form`, `dialog`, `ore` and `arcmenu`. See [Layout and Buttons](layout-and-buttons.md).

## Configuration locations

`menu.ui` in `config.yml` holds the global `mode`, graphical `layout`, shared `products` and `theme`, and `renderers.form` / `renderers.dialog` / `renderers.ore` / `renderers.arcmenu`. Individual menus override those values under root `ui`. `ui.renderers.inventory` is reserved; Inventory `size` and `layout` stay at the menu root. Bedrock icons use `display-item.bedrock` rather than moving all item properties under `ui`.

[Menu Configuration](menu-configuration.md) · [Graphical Layout](ui-layout.md) · [Migration](migration-guide.md)
