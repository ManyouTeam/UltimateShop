# 🪨 Ore UI — Premium

Ore draws product cards and navigation controls through the Paper Dialog route and a generated resource pack. It supports Common and Shop menus on supported Paper 1.21.9+ servers. Graphical layout and styles are shared with ArcMenu.

## Enable Ore

In `config.yml`:

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

Create the target shop, reload, generate the pack below, and load it on the Java client before opening `/shop menu categories`. This uses the selected style's `sidebar` area. Global forced presentation selection can override the switches.

## Generate the resource pack

1. Run `/shop reload` after editing YAML so the menu definitions are loaded.
2. Run `/shop dialogpack` to build the enabled Ore menus. Permission: `ultimateshop.dialogpack`.
3. Use the generated output under `plugins/UltimateShop/pack/` in the resource pack distributed to players.
4. Have clients load the updated pack before opening the changed screens.

`/shop dialogpack <format>` supplies an explicit pack format; accepted command values are `69` through `10000`. Global `menu.dialog.ore.pack-format: 0` selects the built-in version format. Rebuild and redistribute after changing graphical layout, theme, templates or icons. Reloading YAML alone does not distribute new client assets.

Pack generation includes Common/Shop menus using Ore, including a valid forced Ore selection. Search/Favourite are native Dialog fallbacks and do not generate Ore assets.

## Layout and appearance

| Key | Purpose |
| --- | --- |
| `dialog.enabled`, `dialog.layout: ore` | Select the route in normal selection |
| `dialog.content` | Header content, then local root `content` |
| `ui.style` | Shared graphical preset |
| `ui.layout.canvas/header/areas/pagination` | Canvas, areas, text and pagination |
| `ui.products.placement` | Default product placement |
| `ui.products.card` | Card icon/padding and Ore body lore limits |
| `ui.products.price/actions` | Product price text and operation selection |
| Entry `ui.placement`, `ui.appearance` | Individual button/product placement and button/card appearance |
| `ui.theme` | Shared colors |
| `ui.renderers.ore.templates` | Resource-pack texture templates |
| Global `menu.dialog.ore.show-item-icon.*` | Item icon and quantity display |
| Global `menu.dialog.ore.pack-format` | Pack metadata format |

Root Inventory layout identifies entries but does not provide graphical coordinates. Ordinary controls need explicit placements. Product cards use `ui.products.placement` unless individually overridden. See [Graphical UI Layout](../menus-getting-started/ui-layout.md) for geometry and capacity rules.

## Custom template textures

Put PNG sources in `plugins/UltimateShop/textures/`, for example `textures/shop/card.png`, and configure:

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

Texture paths are relative to `textures/`; use neither absolute paths nor `..`. Quote RGB colors. Shared layout, card and theme values can supply generated template geometry/base colors. Regenerate the pack after edits.

## Text and supported menu flows

Ore uses native text rendering. ArcMenu font scale settings do not imply identical Ore output. `ui.products.card.lore-lines` limits product body lore; `description-lines` limits category descriptions. Full applicable lore can remain in hover information.

Search/Favourite use native `multi-action` when Ore is selected. Buy More uses native quantity input or Inventory. Item Sell, Sell All and the in-game editor keep physical Inventory screens.

[Native Dialog](dialog-menus-premium.md) · [ArcMenu](arcmenu-menus-premium.md) · [UI Modes](ui-overview.md)
