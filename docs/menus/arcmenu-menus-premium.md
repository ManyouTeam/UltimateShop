# ✨ArcMenu UI - Premium

ArcMenu renders Common, Shop, Search, and Favourite menus as interactive graphical screens for Java players. It shares styles, named areas, placement, and product card settings with Ore, while using its own ArcMenu integration and client setup.

## Enable ArcMenu

Install a compatible ArcMenu plugin and complete its client resource setup (UltimateShop targets the **ArcMenu 3.4.3 API**, including native text input).

Enable global settings in `config.yml`:

```yaml
menu:
  arcmenu:
    enabled: true
  ui:
    style: classic
```

Save as `menus/common_menus/categories.yml`:

```yaml
title: '&8Categories'
size: 9
layout:
  - '0000A0000'
arcmenu:
  enabled: true
  content: '&7Choose a category'
buttons:
  A:
    display-item:
      material: WHEAT
      name: '&aFarming'
    actions:
      1:
        type: shop_menu
        shop: farming
    ui:
      appearance: button
      placement:
        area: sidebar
        order: 0
```

Make sure the `farming` shop already exists. Save your config, run `/shop reload`, and run `/shop menu categories` (the button appears in the chosen style's `sidebar` area). Under normal selection, Java tries ArcMenu first, then Dialog/Ore, then Inventory; see [UI Modes](ui-overview.md).

## Styles & Presets

Select `classic` or `bedrock` via global `menu.ui.style` or menu `ui.style`, or copy a file in `plugins/UltimateShop/ui/` to create your own:

| Preset | Visual Style & Layout |
| --- | --- |
| `classic` | Left vertical sidebar + right product grid |
| `bedrock` | Charcoal panels, beveled controls, green buy/hover states, light sell controls, top 5-button navigation bar + 3-column product grid |

Note that `bedrock` also names its top horizontal bar `sidebar` (`sidebar` is just an area ID, not a fixed screen edge). When switching presets, update any hardcoded `row`/`column` indices or custom area coordinates.

Menu fields override style defaults, while `menu.force-ui.style` can override local defaults; see [Menu Configuration & Inheritance](menu-configuration.md) and [Graphical UI Layout](../menus-getting-started/ui-layout.md).

## Why a Button Appears in Inventory but Not ArcMenu

**ArcMenu skips common buttons that lack `ui.placement`.** Root `layout` only positions Inventory slots; products inherit `ui.products.placement`, whereas common buttons must set their own graphical placement.

For example, to show legacy shop-link arrows in ArcMenu, add:

```yaml
ui:
  placement:
    area: sidebar
    order: 20
```

If the button is defined under a shop's `buttons`, keep its symbol in the shop menu `layout` as well. For multi-page products inside one shop, use `ui.layout.pagination` instead of manual page arrows (`previous-page-item` / `next-page-item` are Inventory-only).

## Product Actions & Quantity Controls

Product cards show up to two available actions from `ui.products.actions.primary` and remaining/listed actions in `ui.products.actions.extra` (filtered by existing click bindings, prices, conditions, and buy-only/sell-only rules).

Right-clicking a product defaults to `select-amount` (customizable via `ui.renderers.arcmenu.right-click-action`), opening the graphical **product details panel** rather than an Inventory Buy More file. When Buy More and a quantity action are available, +/- buttons and the scroll wheel adjust the count (Inventory `amount-items` and preview slots do not style graphical details).

A stack count > 1 on the item icon reflects the display item's stack size, independent of the selected trade units.

<a id="quantity-input"></a>

## Text Quantity Input

When ArcMenu's text input service is available, clicking the number between `-` and `+` lets players type a quantity directly (even when ArcMenu hides the vanilla anvil UI, live input appears inside the focused graphical field).

Finishing input (`Esc` in mouse mode) clamps valid integers between `1` and the Buy More cap; invalid input keeps the previous count. Players still click Buy or Sell to execute the trade. Prompt text comes from `ui.text.amount-input` (requires Buy More enabled, a valid quantity menu, and `select-amount`).

## ArcMenu Renderer Settings

Per-menu settings live under `ui.renderers.arcmenu`:

```yaml
ui:
  renderers:
    arcmenu:
      right-click-action: select-amount
      screen-scale: 1.5
      animation:
        enabled: true
        duration: 8
      more-menu:
        columns: 1
      tooltip:
        enabled: true
        width: 260
        font-size: 1.1
        padding: 8
```

| Relative Key | Purpose |
| --- | --- |
| `right-click-action` | Action triggered on product right-click |
| `screen-scale` | Overall graphical scale |
| `animation.enabled` / `duration` | Open animation (skipped when reopening the same menu) |
| `more-menu.columns` | Column count for the extra-actions popup |
| `tooltip.enabled` / `width` / `font-size` / `padding` | Hover tooltip appearance |

The refresh interval **always reads from global `config.yml`** in ticks (minimum `5`, or from the global style; per-menu `update-interval` is ignored):

```yaml
menu:
  ui:
    renderers:
      arcmenu:
        update-interval: 20
```

## Backgrounds, Decorations, and Tooltips

ArcMenu reads colors from resolved `ui.theme` (window and title backgrounds toggle independently, and `theme.decorations` controls frames, corner accents, and header dividers; see [Theme and Text](../menus-getting-started/ui-layout.md#theme-and-text)).

Tooltips lay out formatted lore as left-aligned blocks and scale down if oversized; mouse mode adapts to surface bounds, cursor sensitivity, player preference, margin, and vertical pitch limits, while touch/Vision mode uses surface bounds.

## Menu-Type Behaviour

* **Search & Favourite**: use ArcMenu graphical layouts and dedicated controls (source capacity is still capped by root result slots); Favourite edit mode provides reorder and remove controls.
* **Buy More**: handled inside the product details panel.
* **Item Sell / Sell All**: remain physical Inventory screens.

ArcMenu never uses `/shop dialogpack` (that command builds Ore assets only).

[Search Menus](search-menus-premium.md) · [Favourite Menus](favourite-menus-premium.md) · [Buy More Menus](buy-more-menus.md)
