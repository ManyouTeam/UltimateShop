# ✨ ArcMenu graphical menus — Premium

ArcMenu presents Common, Shop, Search and Favourite menus as interactive graphical screens. It shares `ui.layout`, `ui.products`, `ui.placement` and `ui.theme` with Ore, but uses its own client integration and interaction model.

## 🚀 Enable ArcMenu

In `config.yml`:

```yaml
menu:
  arcmenu:
    enabled: true
```

In your menu:

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

This uses the default global `menu.ui.layout.areas.sidebar` graphical area. Install a compatible ArcMenu integration and required client resources. If both ArcMenu and Dialog are enabled, Java players use **ArcMenu first** when available. If ArcMenu cannot open, the menu-opening flow can attempt Dialog/Ore, then Inventory. Bedrock players use their separate `bedrock.enabled` selection.

## 🎛️ Product interaction

- Graphical product cards use `ui.products.actions.primary` for up to two primary operation buttons and `ui.products.actions.extra` for an extra-action menu.
- Listing an action does not create a trade binding. Available actions still come from `menu.click-event` and product overrides.
- `select-amount` normally opens ArcMenu product details; quantity adjustment requires Buy More to be enabled for the product and a suitable quantity binding.
- Product right-click defaults to `select-amount`. Change it with `ui.renderers.arcmenu.right-click-action`.
- A shop's default buy/sell quantities apply until the player selects another quantity.

ArcMenu product details are **not** the Inventory Buy More menu. The latter's `amount-items` and `layout` do not control ArcMenu's detail panel.

## ⚙️ ArcMenu graphical settings

Defaults live at `menu.ui.renderers.arcmenu`; most renderer settings support per-menu overrides at `ui.renderers.arcmenu`. **`update-interval` is currently read only from the global `menu.ui.renderers.arcmenu.update-interval` path.**

```yaml
ui:
  renderers:
    arcmenu:
      right-click-action: select-amount
      screen-scale: 1.35
      update-interval: 20
      animation:
        enabled: true
        duration: 8
      more-menu:
        columns: 1
      tooltip:
        enabled: true
        width: 260
        font-size: 0.9
        padding: 8
```

| Key relative to `ui.renderers.arcmenu` | Effect |
| --- | --- |
| `right-click-action` | Direct action triggered by product right-click |
| `screen-scale` | Overall graphical scale |
| `update-interval` | **Global only:** graphical refresh interval, in ticks (minimum 5) |
| `animation.enabled/duration` | Opening animation |
| `more-menu.columns` | Extra-action popup layout |
| `tooltip.enabled/width/font-size/padding` | Hover-tooltip visibility and appearance |

The renderer constrains numeric settings to supported ranges. Very large fonts can truncate text; use sensible area and card sizes rather than relying on scale alone.

## 📐 Layout and theme are shared with Ore

Define a canvas, named `areas` and optional `pagination` under menu `ui.layout` or global `menu.ui.layout`. Ordinary buttons require `ui.placement`; products use `ui.products.placement` unless overridden. In Search/Favourite screens the root result-slot count still limits source results, regardless of how many cards a graphical area could hold.

See [📐 Graphical layout](ui-layout.md) for the complete schema and correct scopes. ArcMenu does not require Ore's generated pack merely to display its own controls; follow the ArcMenu integration's client setup. The `/shop dialogpack` command concerns **Ore assets**.

## 🩺 Troubleshooting

If Inventory opens, check the two ArcMenu switches, plugin integration, resource setup and supported menu type. Item Sell and Sell All require physical inventories; Buy More uses ArcMenu product details instead of a separate Inventory-style ArcMenu screen. Use [Menu troubleshooting](troubleshooting.md) for symptoms.

[🎨 Presentation selection](ui-overview.md) · [📐 Layout](ui-layout.md) · [🪨 Ore](ore-menus-premium.md).
