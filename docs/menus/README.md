# 📚Menu System

Menus define the screens players open in-game. A menu's **type** determines its content and interaction flow, while its **presentation** determines whether players see an Inventory, Bedrock Form, native Java Dialog, Ore, or ArcMenu screen. Product prices, stock limits, and trade conditions always remain in the shop configuration.

{% hint style="info" %}
All file paths in this section are relative to `plugins/UltimateShop/`. Menu and shop IDs are file names without the `.yml` extension—keep IDs unique within their folders so commands and actions open the intended screen.
{% endhint %}

## Create Your First Menu

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

Run `/shop reload`, then `/shop menu hello`. This opens a one-row Inventory menu with a Close button in the fifth slot. Other presentations are disabled here; if `menu.force-ui` is enabled globally in `config.yml`, the forced UI takes priority.

In `layout`, each character represents a slot: `X` maps to `buttons.X`, while undefined `0` entries stay empty. See [Layout & Buttons](layout-and-buttons.md) for multi-character IDs, click actions, conditions, and visibility controls.

## Choose a Menu Type

| Type | Configuration Path | Player Flow |
| --- | --- | --- |
| [Common](general-menus.md) | `menus/common_menus/<id>.yml` | Category navigation, info panels, and custom actions |
| [Shop](shop-menus.md) | `shops/<id>.yml` and `menus/shop_menu_templates/<template>.yml` | Browse shop products and buy/sell |
| [Buy More](buy-more-menus.md) | `menus/buy_more_menus/<id>.yml` | Choose a trade quantity for a selected product |
| [Search - Premium](search-menus-premium.md) | `menus/search_menus/<id>.yml` | Search visible shop products by item or name |
| [Favourite - Premium](favourite-menus-premium.md) | `menus/favourite_menus/<id>.yml` | Save favourite products and reorder them |
| [Item Sell](item-sell-menus.md) | `menus/item_sell_menus/<id>.yml` | Deposit items, preview payout, and confirm |
| [Sell All](sell-all-menu.md) | `config.yml` → `menu.sell-all` | Deposit items and sell automatically on close |

Use `/shop menu <id>` to open Common, Shop, Search, Favourite, and Item Sell menus. Buy More requires a selected product first; Sell All opens via `/shop sellall` or the `sell_all_menu` action.

## Choose a Presentation

Inventory screens use the root `size` and `layout`. Premium adds **Bedrock Form**, **native Java Dialog**, **graphical Ore**, and **ArcMenu**, depending on the menu type, server platform, and installed integrations (see [UI Modes](ui-overview.md)).

Graphical styles live in `ui/`. Select a shared style via global `menu.ui.style` or a menu's `ui.style`, and control coordinates with `ui.layout` and entry `ui.placement`.

{% hint style="info" %}
`ui.style: bedrock` is only a graphical appearance preset for Ore and ArcMenu—it is completely separate from Bedrock Form UI.
{% endhint %}

## Next Steps

* [Menu Configuration & Inheritance](menu-configuration.md): file structure, shop templates, styles, and override priority.
* [Layout & Buttons](layout-and-buttons.md): slot IDs, actions, conditions, and per-presentation visibility.
* [Graphical UI Layout - Premium](../menus-getting-started/ui-layout.md): canvas, areas, product cards, pagination, and themes.
* [Conditional Display - Premium](conditional-display-premium.md): per-player entries and fallback slots.
* [Display Item Add Lore](display-item-add-lore.md): prices, limits, and presentation-specific hints.
* [Menu Configuration Compatibility](migration-guide.md): current keys and migration notes.

Shared formats: [Display Item Format](../format/display-item-format.md), [Action Format](../format/action-format.md), and [Condition Format](../format/condition-format.md).
