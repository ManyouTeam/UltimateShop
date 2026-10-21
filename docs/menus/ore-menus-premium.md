# 🪨 Ore UI - Premium

Ore draws graphical cards and controls using the supported Paper Dialog API and a generated client resource pack. It shares graphical canvas/area settings with ArcMenu, but it is **selected through `dialog.layout: ore`**, not `ui.mode`.

## Quick start

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
```

In `menus/common_menus/categories.yml` (the global `menu.ui` defaults provide the canvas and named `sidebar` area):

```yaml
title: '&8Categories'
size: 9
layout:
  - '0000A0000'
dialog:
  enabled: true
  layout: ore
  content: '&7Browse categories'
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

The `farming` shop must exist. Reload, run `/shop dialogpack` and make sure clients **load the newly generated pack**. For a graphical **shop** menu, configure `ui.products.placement` as well; see [📐 Graphical layout - Premium](../menus-getting-started/ui-layout.md).

## What Ore reads

| Key                                             | Purpose                                            |
| ----------------------------------------------- | -------------------------------------------------- |
| `dialog.enabled` / global `menu.dialog.enabled` | Allow the Dialog renderer                          |
| `dialog.layout: ore`                            | Choose Ore                                         |
| `dialog.content`                                | Header content (root `content` fallback)           |
| `ui.layout.canvas/header/areas/pagination`      | Logical graphical screen and areas                 |
| `ui.products.placement/card/price/actions`      | Graphical product cards and controls               |
| Entry `ui.placement`                            | Where ordinary buttons or specific products appear |
| Entry `ui.appearance`                           | Graphical button/card style                        |
| `ui.theme`                                      | Shared graphic colors                              |
| `ui.renderers.ore.templates`                    | Generated background/selected-state assets         |
| Global `menu.dialog.ore.show-item-icon.*`       | Icon generation and quantity label behavior        |
| Global `menu.dialog.ore.pack-format`            | Resource-pack format selection                     |

The root Inventory `layout` remains useful to identify entries and special result markers, but its chest coordinates do **not** translate to pixels. Navigation buttons must have graphical placements. Product cards use `ui.products.placement` unless a specific product supplies its own `ui.placement`.

## Customize a graphical card texture

Put PNGs under `plugins/UltimateShop/textures/shop/`, then add:

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

This is **graphical** `ui.renderers.ore.templates`. Source PNG paths must be relative; do not use absolute filesystem paths or `..`. Colors should be quoted RGB strings. Shared layout/card/theme keys may take precedence over template geometry and base styling.

## Layout and text constraints

* Prefer vertical coordinates/heights divisible by 9; put cards in an area tall enough for them.
* Cards need enough height for icon, name, descriptions and operation buttons; undersized or overlapping entries can fail validation.
* `ui.products.card.lore-lines` limits lore shown in Ore card bodies; longer information can remain in tooltips.
* Material names may be client-translated; the server cannot measure every translated string's final width precisely.
* ArcMenu-specific font scaling does not imply the same rendered size in Ore.

Check [Graphical layout - Premium](../menus-getting-started/ui-layout.md) for bounds, grids, stacks, free placement and pagination.

## Generate and distribute the pack

1. Run `/shop dialogpack` after changing Ore layout, theme, templates or related icons.
2. Find the generated output under `plugins/UltimateShop/pack/`.
3. Serve the resulting resource pack to players and verify that the client accepted it.
4. Reopen the menu to verify the actual layout.

`/shop dialogpack <format>` can override the pack format when needed. A regular `/shop reload` updates server-side YAML but **does not ensure that the client downloaded new assets**.

If you see Inventory instead, check both Dialog switches, the supported API/runtime, the menu's `dialog.layout` and console layout/pack errors. If cards overlap, validate graphical areas before regenerating again.

[📐 Graphical layout - Premium](../menus-getting-started/ui-layout.md) · [💬 Native Dialog - Premium](dialog-menus-premium.md) · [🩺 Troubleshooting](troubleshooting.md).
