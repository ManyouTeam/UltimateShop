# 🛍️ Shop menus

A shop file defines **products, prices and trading**. A shop-menu template defines **which entries appear and where**, plus navigation buttons. One template may be reused by many shops.

## 📁 Two files, two responsibilities

- Shop file: `plugins/UltimateShop/shops/<shop-id>.yml`.
- Template: `plugins/UltimateShop/menus/shop_menu_templates/<template-id>.yml`.
- `settings.menu` inside the shop selects a template.
- Open with `/shop menu <shop-id>` or a `shop_menu` action.

## ✅ Example 1: menu template

Create `menus/shop_menu_templates/farming.yml`:

```yaml
title: '{shop-name}'
size: 9
layout:
  - '0000A0000'
```

`A` identifies a product supplied by the **shop**, not a button defined in the template.

## ✅ Example 2: shop file

Create `shops/farming.yml`:

```yaml
settings:
  shop-name: '&aFarming'
  menu: farming
items:
  A:
    display-item:
      material: WHEAT
      name: '&eWheat'
    products:
      1:
        material: WHEAT
    buy-prices:
      1:
        economy-plugin: Vault
        amount: 5
```

This example needs your Vault-compatible economy. Reload and open `/shop menu farming`. For supported product/pricing variants, follow [Product configuration](../shops/products.md).

## 🔀 Override a template for one shop

Use `settings.menu-settings` in the **shop** file:

```yaml
settings:
  menu: farming
  menu-settings:
    title: '&aFarming Specials'
    dialog:
      enabled: true
      layout: multi-action
    bedrock:
      enabled: true
      content: '&7Browse farming products'
```

Overrides are applied to the template *before* global graphical `menu.ui` defaults fill any missing graphical fields. `settings.menu-settings` does **not** belong in `config.yml` or a menu template.

## 🧱 Adding navigation buttons

A template can define its own buttons:

```yaml
buttons:
  X:
    display-item:
      material: ARROW
      name: '&eBack'
    actions:
      1:
        type: back
```

Add `X` to an available `layout` slot. Product IDs and ordinary buttons must not collide in the visible layout. Products can also define `menu-visibility`, `display-conditions` and graphical `ui.placement`.

## 🎨 Inventory / Dialog / Ore / ArcMenu / Form

The **same shop items** can appear in different presentations. Inventory uses the template's root `layout`. Native Dialog and Form collect items as lists; Ore and ArcMenu use `ui.layout`, `ui.products.placement` and individual button `ui.placement`. A blank graphical area does not automatically create shop-category buttons.

Use [UI selection](ui-overview.md) and [Graphical layout](ui-layout.md) for the prerequisites and full configuration.

## 🩺 If a product is missing

Check that `settings.menu` names a real template, `items.A` exists, `layout` references `A` for Inventory, the product is permitted for the player, and the product's `menu-visibility` and display conditions are satisfied. If graphical settings seem unchanged, check menu/template `ui` and the shop's `settings.menu-settings.ui`; explicit overrides take priority.

[Quantity selection](buy-more-menus.md) · [Search](search-menus-premium.md) · [Troubleshooting](troubleshooting.md).
