# 📦 Inventory interface

Inventory is the normal chest-style menu. Its rows come from the **root** `layout` and `size`; `ui.layout` does not affect it.

## 🚀 Open Inventory instead of Dialog / ArcMenu / Form

For a Java menu, disable its Dialog and ArcMenu choices (or disable those presentations globally):

```yaml
dialog:
  enabled: false
arcmenu:
  enabled: false
```

If Bedrock players must also receive Inventory instead of Form, disable their Form choice:

```yaml
bedrock:
  enabled: false
```

These blocks belong at the **menu root**. Global equivalents are `menu.dialog.enabled`, `menu.arcmenu.enabled` and `menu.bedrock.enabled` in `config.yml`. There is **no** `ui.mode.java: inventory` setting.

## ✅ Working Inventory example

Save as `menus/common_menus/categories.yml`:

```yaml
title: '&8Categories'
size: 18
layout:
  - '000000000'
  - '000A0X000'
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

Create the `farming` shop before opening this example. Reload with `/shop reload` and open with `/shop menu categories`. Each row has nine slots; `0` is empty until you define `buttons.0`.

## 🧱 How Inventory handles items

- `buttons.<id>` describes a navigation/action button. Shop menus additionally resolve product IDs from the shop.
- `layout` controls order and slots. `ui.placement` does **not** move Inventory items.
- `menu-visibility.inventory: false` hides a button or product here without hiding it in Form/Ore/ArcMenu.
- `display-conditions` control whether an entry is available to a player.
- `menu.click-event` and product-specific click bindings govern product actions. Ordinary buttons use `actions`.

See [Layout and buttons](layout-and-buttons.md) for markers and [Conditional display](conditional-display-premium.md) for dynamic entries.

## 🔄 Refresh, titles and deposit screens

`menu.menu-update.circle-update` and `click-update` control item refresh. `menu.title-update.*` contains optional title update behavior. Avoid frequent refresh if the contents rarely change.

[Item Sell](item-sell-menus.md) and [Sell All](sell-all-menu.md) are physical Inventory screens. [Buy More](buy-more-menus.md) has a dedicated Inventory quantity selection layout.

## 🩺 If Inventory opens unexpectedly

Check the global *and* menu-level switches, runtime integration availability and whether the requested menu type supports the chosen presentation. An empty Inventory slot usually means a missing layout marker, a missing entry, a failed display condition or `menu-visibility.inventory: false`.

[Presentation guide](ui-overview.md) · [Menu troubleshooting](troubleshooting.md).
