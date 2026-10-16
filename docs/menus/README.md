# 📚 Menu System

UltimateShop menus control navigation, product selection and the presentation of trades. This section explains menu types, interface modes and configuration rules. Start with the example files included with your installation, and review any existing custom configuration before changing its layout or UI settings.

## Two independent concepts

**Menu type** determines *what* a menu does: Common, Shop, Buy More, Search, Favourite, Item Sell or Sell All. **UI mode** determines *how* a supported menu is displayed: Inventory, Bedrock Form, native Dialog, Ore or ArcMenu. Changing the UI mode does not change the menu's type or trade rules.

- [Menu types](#menu-types): choose the feature you want.
- [UI modes](ui-overview.md): choose how Java and Bedrock players see it.
- [Configuration and inheritance](menu-configuration.md): understand where settings belong.
- [Layout and buttons](layout-and-buttons.md): create a clickable Inventory menu.

## Menu types

| Type | Location under `plugins/UltimateShop/` | Documentation |
| --- | --- | --- |
| Common | `menus/common_menus/` | [Common](general-menus.md) |
| Shop | `menus/shop_menu_templates/`, or shop `settings.menu-settings` | [Shop](shop-menus.md) |
| Buy More | `menus/buy_more_menus/` | [Buy More](buy-more-menus.md) |
| Search (Premium) | `menus/search_menus/` | [Search](search-menus-premium.md) |
| Favourite (Premium) | `menus/favourite_menus/` | [Favourite](favourite-menus-premium.md) |
| Item Sell | `menus/item_sell_menus/` | [Item Sell](item-sell-menus.md) |
| Sell All | `config.yml`: `menu.sell-all` (no menu file) | [Sell All](sell-all-menu.md) |

Dedicated Search, Favourite and Item Sell folders determine the menu type. The Common folder also accepts `menu-type: search`, `favourite` (or `favorite`) and `item-sell` (or `item_sell`). Keep menu IDs distinct. Shop templates require a shop; Buy More menus require a selected product.

## Create a menu in three steps

1. Create `plugins/UltimateShop/menus/common_menus/hello.yml`.
2. Paste this configuration:

```yaml
title: '&8Hello'
size: 9
layout:
  - '0000A0000'
buttons:
  A:
    display-item:
      material: EMERALD
      name: '&aOpen the main menu'
    actions:
      1:
        type: open_menu
        menu: main
```

3. Run `/shop reload`, then `/shop menu hello`. The command requires `ultimateshop.menu`.

`0` represents an unassigned slot in this example, not an automatically generated glass pane. For a decorative filler, define a button with ID `0`. [Layout and buttons](layout-and-buttons.md) explains button IDs, conditions and multi-character markers.

## Choose the next chapter

New users should read [Common Menus](general-menus.md) and [Shop Menus](shop-menus.md) first. For Java Dialog/Ore or ArcMenu, use [UI modes](ui-overview.md) before setting up an integration. If you are adapting existing YAML files, consult the [configuration compatibility guide](migration-guide.md). For syntax shared with shops, use [Action Format](../format/action-format.md), [Condition Format](../format/condition-format.md) and [Display Item Format](../format/display-item-format.md).
