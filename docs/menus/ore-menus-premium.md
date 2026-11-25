# 🪨Ore UI - Premium

Ore renders graphical product cards and navigation controls for Java players using Paper's Dialog transport and a generated client resource pack. Supported for Common and Shop menus on **Paper 1.21.9+**, sharing graphical layouts and styles with ArcMenu.

## Enable Ore

Configure global dialog and Ore settings in `config.yml`:

```yaml
menu:
  dialog:
    enabled: true
    ore:
      show-item-icon:
        enabled: true
        auto-hide-one: true
      pack-format: 0
  ui:
    style: classic
```

Save as `menus/common_menus/categories.yml`:

```yaml
title: '&8Categories'
size: 9
layout:
  - '0000A0000'
dialog:
  enabled: true
  layout: ore
  content: '&7Choose a category'
arcmenu:
  enabled: false
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

Make sure the `farming` shop already exists. Run `/shop reload`, generate and load the resource pack below, and then run `/shop menu categories` (the button uses the chosen style's `sidebar` area; global `menu.force-ui` overrides switches).

<a id="generate-the-resource-pack"></a>

## Generate the Resource Pack

1. Run `/shop reload` after editing YAML so the plugin loads the latest menu definitions.
2. Run `/shop dialogpack` (requires `ultimateshop.dialogpack`) to build assets for Ore-enabled menus.
3. Merge the generated files in `plugins/UltimateShop/pack/` into the resource pack distributed to players.
4. Load the updated resource pack on the client before reopening modified screens.

Use `/shop dialogpack <format>` to specify a pack format (`69`–`10000`); global `menu.dialog.ore.pack-format: 0` uses the built-in format for your server version.

{% hint style="warning" %}
Whenever you change graphical layouts, themes, templates, or icons, you **must rerun `/shop dialogpack` and redistribute the pack**—reloading YAML alone does not push new assets to clients.
{% endhint %}

Pack generation covers Ore-enabled Common and Shop menus (including forced Ore); Search and Favourite fall back to native Dialog and generate no Ore assets.

## Layout & Appearance Keys

| Key | Purpose |
| --- | --- |
| `dialog.enabled`, `dialog.layout: ore` | Selects Ore under normal selection |
| `dialog.content` | Header content (falls back to root `content`) |
| `ui.style` | Shared graphical preset |
| `ui.layout.canvas/header/areas/pagination` | Canvas, headers, named areas, and pagination |
| `ui.products.placement` | Default product card area and placement |
| `ui.products.card` | Card icon size, padding, and Ore body line limits |
| `ui.products.price` / `actions` | Compact price line and card action buttons |
| Entry `ui.placement`, `ui.appearance` | Per-button/product placement and `button` / `card` look |
| `ui.theme` | Shared colors |
| `ui.renderers.ore.templates` | Resource pack card texture templates |
| Global `menu.dialog.ore.show-item-icon.*` | Item icon and stack-count display |
| Global `menu.dialog.ore.pack-format` | Pack metadata `pack_format` |

Root Inventory `layout` only identifies entries—it does not set graphical coordinates. Common buttons need explicit `ui.placement`, while products default to `ui.products.placement`. See [Graphical UI Layout](../menus-getting-started/ui-layout.md).

## Custom Template Textures

Place PNG source files under `plugins/UltimateShop/textures/` (for example `textures/shop/card.png`) and reference them:

```yaml
ui:
  renderers:
    ore:
      templates:
        product:
          texture: 'shop/card.png'
          selected-texture: 'shop/card-selected.png'
          selected-background: '#244A32'
          selected-border: '#65D98B'
```

Paths are relative to `textures/` (no absolute paths or `..`), and hex colors must be quoted. Rebuild and redistribute the resource pack after editing textures.

## Text Rendering & Menu-Type Support

Ore uses native text rendering, so ArcMenu font scaling will not produce identical results in Ore. `ui.products.card.lore-lines` and `description-lines` cap visible lines on the card face while keeping full text in hover tooltips.

When Ore is selected: Search and Favourite use native `multi-action`; Buy More uses native quantity input or Inventory; Item Sell, Sell All, and the In-Game Editor stay physical Inventory.

[Native Dialog](dialog-menus-premium.md) · [ArcMenu](arcmenu-menus-premium.md) · [UI Modes](ui-overview.md)
