# Shop Menus

A Shop Menu shows products defined in a shop. The **shop file** owns product data, prices and trade restrictions; a **menu template** provides the layout, navigation buttons and presentation. Many shops can reuse the same template.

## Requirements and file locations

- Shop: `plugins/UltimateShop/shops/<shop>.yml`.
- Optional reusable template: `plugins/UltimateShop/menus/shop_menu_templates/<template>.yml`.
- The shop's `settings.menu` chooses a template.
- Use `/shop menu <shop>` or the `shop_menu` action to open a shop.

## Quick start: select a template

In `shops/farming.yml` (illustrative fragment):

```yaml
settings:
  shop-name: '&aFarming'
  menu: example-shop-menu
  menu-settings:
    title: '{shop-name}'
    content: 'Fresh supplies'
items:
  A:
    display-item:
      material: WHEAT
      name: '&eWheat'
    buy-prices:
      1:
        economy-plugin: Vault
        amount: 5
    products:
      1:
        material: WHEAT
```

The template's root `layout` must contain the product ID `A` for an Inventory slot. Product and price syntax is covered in [Products](../shops/products.md); adapt the example to your actual economy and item format.

A minimal template at `menus/shop_menu_templates/farming.yml`:

```yaml
title: '{shop-name}'
size: 9
layout:
  - '0000A0000'
```

Set `settings.menu: farming` to use this template. The plugin includes reusable sample templates such as `example-shop-menu.yml` and `daily-shop-menu.yml`.

## Inline menu settings and overrides

A shop can define its own menu settings without requiring a unique template. Overrides live under `settings.menu-settings`:

```yaml
settings:
  menu: example-shop-menu
  menu-settings:
    title: '&aFarming'
    size: 27
    ui:
      mode:
        java: dialog
```

The shop override is merged with the chosen template and then missing `ui` defaults are inherited from global `menu.ui`. Do **not** put `settings.menu-settings` in `config.yml` or in a button.

## Entries and buttons

The layout IDs resolve from the shop's own buttons, products and (Premium) copy products, together with template buttons. A product's click conditions and trade configuration remain in the shop file, not the reusable template.

For example, to add an ordinary navigation button to a template:

```yaml
buttons:
  X:
    display-item:
      material: ARROW
      name: '&eBack'
    actions:
      1:
        type: back
        menu: main
```

Add `X` to a template layout slot. For available action and condition formats, see [Action Format](../format/action-format.md), [Condition Format](../format/condition-format.md) and [Custom Click Event](../features/custom-click-event-premium.md).

## UI presentation

Java `inventory`, `dialog`, `ore` and `arcmenu` and Bedrock `form` are alternative presentations of supported shop entries. They reuse the shop's underlying products; they do not require separate shop files. For graphical Ore/ArcMenu, position products through `ui.products.placement` and buttons through `ui.placement`. Native Dialog/Form list products by their renderer rules rather than using Inventory gaps.

See [UI Modes](ui-overview.md), [Graphical Layout](ui-layout.md), [Quantity Selection](buy-more-menus.md) and [Display Item Add Lore](display-item-add-lore.md).

## Troubleshooting

If a product is missing, check the product ID in the template's `layout`, product display conditions, click/trade availability, shop-level overrides, and the chosen renderer's visibility rules. If a global UI edit appears ineffective, inspect `settings.menu-settings.ui` and explicit `ui` settings in the template.
