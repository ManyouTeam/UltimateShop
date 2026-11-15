# 🔲 Common Menus

Common menus display configured buttons for navigation and custom actions. Save them in `menus/common_menus/` and open them with `/shop menu <id>` or an `open_menu` action.

## Create a navigation menu

Save as `menus/common_menus/main.yml`:

```yaml
title: '&8Main Menu'
size: 9
layout:
  - '000A0X000'
dialog:
  enabled: false
bedrock:
  enabled: false
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
  X:
    display-item:
      material: BARRIER
      name: '&cClose'
    actions:
      1:
        type: close
```

Create the `farming` shop, run `/shop reload`, then `/shop menu main`. The example selects Inventory in normal selection; global `menu.force-ui` can override the switches.

Use `type: shop_menu` with `shop` to open a shop. Use `type: open_menu` with `menu` for another Common, Search, Favourite or Item Sell menu. A Common menu does not automatically populate shop products.

## Common menu keys

| Key | Purpose |
| --- | --- |
| `title` | Screen title |
| `content` | Shared description fallback |
| `size`, `layout` | Inventory size and entry IDs |
| `buttons` | Ordinary display items, actions and conditions |
| `conditions` | Conditions for opening the menu |
| `open-actions`, `close-actions` | Actions for menu lifecycle events |
| `dynamic-layout` | Per-player layout evaluation — Premium |
| `dialog`, `bedrock`, `arcmenu` | Presentation switches and supported native options |
| `ui` | Ore/ArcMenu style, layout and drawing settings |

Button click conditions belong in `buttons.<id>.conditions`; button display conditions belong in `buttons.<id>.display-conditions`. Root `conditions` applies to the whole menu. See [Layout & Buttons](layout-and-buttons.md).

## Use another presentation — Premium

To use native Dialog, replace the example's Dialog switch with:

```yaml
dialog:
  enabled: true
  layout: multi-action
  content: '&7Choose a category'
  button-width: 150
  columns: 2
```

Also enable global `menu.dialog.enabled`. For Ore, select `dialog.layout: ore`, configure graphical placements and distribute its generated pack. For ArcMenu, enable the global/menu ArcMenu switches and configure graphical placements with a compatible integration. Bedrock Form uses the global/menu Bedrock switches.

[UI Modes](ui-overview.md) explains priority, forced selection and supported clients. [Graphical UI Layout](../menus-getting-started/ui-layout.md) explains styles and areas.

## Custom command — Premium

Add this at menu root to register a command for the menu:

```yaml
custom-command:
  name: categories
  description: Open the category menu
```

The command opens this menu for the player. Choose a name that does not conflict with an existing command.

<a id="all-buttons-update"></a>
<a id="title-update-premium"></a>
<a id="title-update-premium-require-paper"></a>

## Inventory refresh and title updates

Global `menu.menu-update.circle-update` refreshes item displays every second; `click-update` refreshes after clicks. Premium title updates use `menu.title-update.enabled/circle-update/click-update` and require PacketEvents. Full settings are listed in [Inventory UI](inventory-ui.md#clicks-visibility-and-refresh).

## Typed files in the Common folder

The dedicated Search, Favourite and Item Sell folders classify their files automatically. Existing Common-folder files can declare root `menu-type: search`, `favourite` (alias `favorite`) or `item-sell` (alias `item_sell`). Search/Favourite IDs can also be classified by their global legacy menu lists.

Prefer dedicated folders for new typed menus. Their specialized controls and presentation support follow their declared type, even when stored in `common_menus`.

[Shop Menus](shop-menus.md) · [Configuration & Inheritance](menu-configuration.md)
