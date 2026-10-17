# 📚 Menu System

Menus are the screens players open. **Menu type** controls which data a screen shows; **presentation** controls how it looks. You can change the presentation without rewriting products or trade rules.

### Menu Types

All paths below are relative to `plugins/UltimateShop/`.

| Feature                       | File or configuration                                     | Guide                                                |
| ----------------------------- | --------------------------------------------------------- | ---------------------------------------------------- |
| Navigation and custom buttons | `menus/common_menus/<id>.yml`                             | [🔲 Common](general-menus.md)                        |
| Shop product list             | `shops/<shop>.yml` + `menus/shop_menu_templates/<id>.yml` | [🛍️ Shop](shop-menus.md)                            |
| Quantity selection            | `menus/buy_more_menus/<id>.yml`                           | [🔽 Buy More](buy-more-menus.md)                     |
| Search                        | `menus/search_menus/<id>.yml`                             | [🔍 Search - Premium](search-menus-premium.md)       |
| Favourite products            | `menus/favourite_menus/<id>.yml`                          | [❤️ Favourite - Premium](favourite-menus-premium.md) |
| Deposit items to sell         | `menus/item_sell_menus/<id>.yml`                          | [💰 Item Sell](item-sell-menus.md)                   |
| Sell all inventory items      | `config.yml` → `menu.sell-all`                            | [🛒 Sell All](sell-all-menu.md)                      |

Please note:

* Search and Favourite require <mark style="color:red;">**Premium**</mark>.&#x20;
* Shop-menu _templates_ contain screen layout and ordinary buttons; shop files contain product prices and trading rules.&#x20;
* Keep a menu ID unique among menus.

### Presentations

| Java client                                                                                                                                                                | Bedrock client                                              |
| -------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------- |
| [📦 Inventory](inventory-ui.md), [💬 Dialog - Premium](dialog-menus-premium.md), [🪨 Ore - Premium](ore-menus-premium.md), [✨ ArcMenu - Premium](arcmenu-menus-premium.md) | [🛏️ Form - Premium](bedrock-menus-premium.md) or Inventory |

See [presentation priority](ui-overview.md).

## First working example

Create `plugins/UltimateShop/menus/common_menus/hello.yml`:

```yaml
title: '&8Hello'
size: 9
layout:
  - '0000A0000'
buttons:
  A:
    display-item:
      material: EMERALD
      name: '&aOpen main menu'
    actions:
      1:
        type: open_menu
        menu: main
```

Run `/shop reload`, then `/shop menu hello`. The target `main` menu must exist. `0` is an empty slot **unless** you define `buttons.0`.

## Reference by task

* [🧱 Slot layout, buttons, visibility and actions](layout-and-buttons.md)
* [⚙️ Global vs menu vs product settings](menu-configuration.md)
* [📐 Graphical canvas, placement, themes and product cards - Premium](../menus-getting-started/ui-layout.md)
* [🔀 Conditional display and dynamic layout - Premium](conditional-display-premium.md)
* [🥉 Item lore and product text](display-item-add-lore.md)
* [🛠️ Configuration editor](menu-configuration.md)
* [🔄 Configuration key changes](migration-guide.md)
* [🩺 Troubleshooting](troubleshooting.md)

For formats shared with other plugin features, read [Action format](../format/action-format.md), [Condition format](../format/condition-format.md) and [Display item format](../format/display-item-format.md).
