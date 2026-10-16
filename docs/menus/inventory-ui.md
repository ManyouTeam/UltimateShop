# 📦 Inventory UI

Inventory is UltimateShop's traditional chest-style interface. It uses the menu root `title`, `size`, `layout` and `buttons`, not graphical `ui.layout` coordinates. It is the Java default and can also be selected for Bedrock clients.

## Select Inventory

```yaml
# config.yml
menu:
  ui:
    mode:
      java: inventory
      bedrock: form
```

For one menu, set `ui.mode.java: inventory`.

## Minimal example

```yaml
title: '&8Categories'
size: 9
layout:
  - '0000A0000'
buttons:
  A:
    display-item:
      material: COMPASS
      name: '&aMain menu'
    actions:
      1:
        type: open_menu
        menu: main
```

A slot containing `0` remains empty unless `buttons.0` exists. Root layout markers and special input/result slots are covered in [Layout and Buttons](layout-and-buttons.md).

## Clicks, refresh and titles

Product actions are mapped to Bukkit click types by `menu.click-event` and can be overridden in product configuration. Buttons use their `actions` and `conditions`. `menu.menu-update.circle-update` / `click-update` control Inventory item refresh; `menu.title-update.*` is available for supported Premium/Paper/PacketEvents setups. Frequent refresh can cost CPU and cause title flashing.

## Physical item deposit

[Item Sell](item-sell-menus.md) uses input slots, confirmation and price preview. [Sell All](sell-all-menu.md) sells on closing. These remain Inventory interfaces even if the initiating screen uses Form, Dialog, Ore or ArcMenu. Inventory [Buy More](buy-more-menus.md) has its own amount-selection buttons.

## Troubleshooting

For an empty entry, inspect its `layout` marker, available button/product, display conditions and `ui.visible-in`. If you requested a different UI but Inventory opened, see [UI Modes](ui-overview.md) and [Troubleshooting](troubleshooting.md).
