# 🛍️ Shop Menus

A shop file holds products, prices and trade rules. Its menu template holds layout, navigation buttons and presentation settings. Multiple shops can share one template.

## Create a shop and template

Save this template as `menus/shop_menu_templates/farming.yml`:

```yaml
title: '{shop-name}'
size: 9
layout:
  - '0000A0X00'
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

Save the shop as `shops/farming.yml`:

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

With Vault and a compatible economy installed, run `/shop reload`, then `/shop menu farming`. In normal Inventory selection, marker `A` displays shop product `items.A`, and `X` displays the template's Close button. Global `menu.force-ui` can override the local presentation switches.

`settings.menu: farming` selects the template ID; `/shop menu farming` opens the **shop** ID. These IDs can differ. Shop templates are used by shops rather than opened as standalone Common menus.

## Override one shop's template

In the shop file:

```yaml
settings:
  shop-name: '&aFarming'
  menu: farming
  menu-settings:
    title: '&aFarming Specials'
    dialog:
      enabled: true
      layout: multi-action
    bedrock:
      enabled: true
      content: '&7Browse farming products'
    ui:
      style: bedrock
```

Overrides merge recursively into the template; lists such as `layout` replace the full list. Global graphical defaults fill missing `ui` fields afterward. `settings.menu-settings` is also usable without a template when it supplies the complete screen definition. See [Configuration & Inheritance](menu-configuration.md).

## Add products and navigation

Add products under shop `items`, then reference their IDs in the template's Inventory layout. Ordinary navigation buttons can live in template `buttons` or shop `buttons`. Keep IDs distinct across these sources.

Product clicks use `menu.click-event` and product overrides. Ordinary buttons use their action lists. Product pricing and rewards follow [Products](../shops/products.md); graphical layout does not change trading rules.

## Present the same shop in different interfaces — Premium

- Inventory uses root `size` and `layout`.
- Form and native Dialog turn selected entries into lists or actions.
- Ore and ArcMenu use the selected style's `ui.layout`, `ui.products.placement`, and individual button `ui.placement`.

In a graphical shop, non-dynamic product collection can include products absent from root layout, using the default product placement. Dynamic layouts retain their selected products unless additional products have explicit graphical placement. Navigation controls always need their own placement. Use `menu-visibility` and display conditions to control eligibility.

[UI Modes](ui-overview.md) · [Graphical UI Layout](../menus-getting-started/ui-layout.md) · [Buy More](buy-more-menus.md)
