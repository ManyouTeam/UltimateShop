# 📚 Menu System

Menus define the screens players open. A menu's **type** determines its content and controls; its **presentation** determines whether players see an Inventory, Form, Dialog, Ore or ArcMenu screen. Shop products, prices and trade conditions stay in the shop configuration.

All configuration paths in this section are relative to `plugins/UltimateShop/`. Menu and shop IDs are file names without `.yml`. Keep IDs unique so commands and actions resolve to the intended screen.

## Create your first menu

Save this complete example as `menus/common_menus/hello.yml`:

```yaml
title: '&8Hello'
size: 9
layout:
  - '0000X0000'
dialog:
  enabled: false
bedrock:
  enabled: false
arcmenu:
  enabled: false
buttons:
  X:
    display-item:
      material: BARRIER
      name: '&cClose'
    actions:
      1:
        type: close
```

Run `/shop reload`, then `/shop menu hello`. This creates a one-row Inventory with a Close button in its fifth slot. The example uses the normal presentation switches; an enabled global `menu.force-ui` takes priority over them.

Each layout character represents a slot. `X` selects `buttons.X`; undefined `0` entries leave empty slots. Learn longer IDs, click actions and visibility in [Layout & Buttons](layout-and-buttons.md).

## Choose a menu type

| Type | Configuration | Player flow |
| --- | --- | --- |
| [Common](general-menus.md) | `menus/common_menus/<id>.yml` | Navigation and custom actions |
| [Shop](shop-menus.md) | `shops/<id>.yml` and `menus/shop_menu_templates/<template>.yml` | Browse and trade shop products |
| [Buy More](buy-more-menus.md) | `menus/buy_more_menus/<id>.yml` | Select a quantity for an existing product |
| [Search — Premium](search-menus-premium.md) | `menus/search_menus/<id>.yml` | Find products by item or name |
| [Favourite — Premium](favourite-menus-premium.md) | `menus/favourite_menus/<id>.yml` | Save products and edit their order |
| [Item Sell](item-sell-menus.md) | `menus/item_sell_menus/<id>.yml` | Deposit items, preview value and confirm |
| [Sell All](sell-all-menu.md) | `config.yml` → `menu.sell-all` | Deposit items and sell on close |

Use `/shop menu <id>` for Common, Shop, Search, Favourite and Item Sell. Buy More needs a selected product; Sell All opens with `/shop sellall` or its menu action.

## Choose a presentation

Inventory uses the root `size` and `layout`. Premium adds Bedrock Form, native Java Dialog, graphical Ore and ArcMenu. Availability depends on the menu type, server platform and installed integrations. See [UI Modes](ui-overview.md) for the support table and selection order.

Graphical styles live in `ui/`. Select a shared style with global `menu.ui.style` or a menu's `ui.style`; configure coordinates with `ui.layout` and entry `ui.placement`. The `bedrock` style is a graphical preset and is separate from Bedrock Form.

## Continue configuring

- [Configuration & Inheritance](menu-configuration.md): files, templates, styles and override priority.
- [Layout & Buttons](layout-and-buttons.md): slot IDs, actions, conditions and visibility.
- [Graphical UI Layout — Premium](../menus-getting-started/ui-layout.md): areas, cards, pagination and themes.
- [Conditional Display — Premium](conditional-display-premium.md): player-dependent entries and alternatives.
- [Display Item Add Lore](display-item-add-lore.md): prices, limits and presentation-specific instructions.
- [Configuration Compatibility](migration-guide.md): current keys and migration examples.

Shared formats: [Display Item](../format/display-item-format.md), [Actions](../format/action-format.md) and [Conditions](../format/condition-format.md).
