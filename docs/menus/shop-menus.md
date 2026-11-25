# 🛍️Shop Menus

Shop menus separate data from layout: shop files hold products, prices, and trade rules, while menu templates hold layout, navigation buttons, and presentation settings. Multiple shops can share one template.

## Create a Shop and Template

Save the template as `menus/shop_menu_templates/farming.yml`:

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

With Vault and an economy plugin installed, run `/shop reload` and `/shop menu farming`. In Inventory UI, slot `A` shows shop product `items.A` and `X` shows the template's Close button (an enabled global `menu.force-ui` overrides presentation switches).

{% hint style="info" %}
`settings.menu: farming` selects the **template ID**, while `/shop menu farming` opens the **shop ID**—they do not have to match. Shop templates are used by shops and cannot be opened standalone as Common menus.
{% endhint %}

## Override a Template for One Shop

Use `settings.menu-settings` in the shop file to override title, presentation, or graphical style on top of a shared template:

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

Overrides merge recursively onto the template (lists such as `layout` are replaced whole), and global graphical defaults fill any missing `ui` fields. Shops that do not share a template can omit `settings.menu` and define their full screen inside `settings.menu-settings`. See [Menu Configuration & Inheritance](menu-configuration.md).

## Add Products and Navigation

Define products under `items` in the shop and reference their IDs in the template `layout`. Navigation buttons can live in template `buttons` (shared) or shop `buttons` (shop-specific); avoid duplicate IDs across sources.

Product clicks use `menu.click-event` and per-product overrides, while common buttons run their `actions`. Graphical layout changes appearance and coordinates only—it never alters [Products](../shops/products.md) pricing or trade logic.

## Fill Product Slots Across Shops with `shop-items`

In a shop menu template, `shop-items` marks which layout characters act as generic product slots. For example, the four `P` slots below fill left-to-right with the first four products in the shop's `items` section (unused slots stay empty):

```yaml
size: 9
shop-items: ['P']
layout:
  - '0PPPP000X'
```

To bind fixed positions to the 1st, 2nd, and 3rd products, use `shop-items: ['A', 'B', 'C']`.

Other slots can still hold common buttons (like `X`) or explicit product IDs, and product display conditions and visibility still apply. Individual shops can override `shop-items` via `settings.menu-settings.shop-items` (called **Shop Item Symbols** in the in-game editor).

## Add Page Buttons to an Inventory Shop

When using a repeated `shop-items` symbol, add `previous-page-item.<char>` and `next-page-item.<char>` keyed by their layout characters. Each page holds as many products as there are matching product slots in `layout`:

```yaml
size: 18
shop-items: ['P']
layout:
  - '0PPPPPPP0'
  - 'l0000000r'

previous-page-item:
  l:
    hide-on-first-page: true
    display-item:
      material: ARROW
      name: '&ePrevious Page'
    hidden-item:
      material: GRAY_STAINED_GLASS_PANE
      name: ' '

next-page-item:
  r:
    hide-on-last-page: true
    display-item:
      material: ARROW
      name: '&eNext Page'
```

By default, Previous hides on page 1 and Next hides on the last page (showing `hidden-item` if configured, or air). Set `hide-on-first-page` / `hide-on-last-page` to `false` to keep the arrows visible (clicking at the boundary stays on the same page). Pagination swaps product slots only and leaves common buttons in place; both buttons can also be edited in the in-game editor.

## Present the Same Shop Across UIs - Premium

The same shop can be shown in multiple client UIs, each reading its own layout settings:

* **Inventory**: uses the root `size` and `layout` slots.
* **Bedrock Form & Native Dialog**: turns the active products and buttons into a list or action controls.
* **Ore & ArcMenu (Graphical Shops)**: uses the chosen style's `ui.layout`, places product cards in `ui.products.placement`, and places common buttons in their own `ui.placement`.

In **Ore / ArcMenu graphical shops**, which products appear depends on whether `dynamic-layout` is enabled:

* **With `dynamic-layout` off (default)**: any eligible product in the shop's `items` section appears in the graphical product area automatically—even if you did not put its ID in the chest `layout`.
* **With `dynamic-layout: true`**: only products picked by the `layout` rules appear (unless another product explicitly sets its own `ui.placement`).
* **Common buttons (Back, Close, navigation, etc.)**: never appear automatically in graphical UIs—you must always give them their own `ui.placement`. Every entry still respects `menu-visibility` and `display-conditions`.

[UI Modes](ui-overview.md) · [Graphical UI Layout](../menus-getting-started/ui-layout.md) · [Buy More Menus](buy-more-menus.md)
