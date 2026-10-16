# 🪨 Ore UI - Premium

Ore is UltimateShop's graphical Dialog-based renderer. It requires Premium, supported Paper (1.21.9+), and a generated client resource pack. Select `ore` separately from native Dialog's `multi-action`/`item-action-list` layout. Bedrock players follow `bedrock.enabled`.

## Quick start

```yaml
# config.yml
menu:
  dialog:
    enabled: true
    ore:
      show-item-icon:
        enabled: true
        auto-hide-one: true
      pack-format: 0
  bedrock:
    enabled: true
```

Set `dialog.enabled: true` and `dialog.layout: ore` in the menu itself. If both ArcMenu switches are enabled and its plugin is available, ArcMenu has priority; otherwise Ore is used when supported.

Give ordinary navigation buttons graphical placements (see [Graphical Layout](ui-layout.md)); Inventory gaps do not automatically become Ore coordinates.

## Ore layout and card content

Select `dialog.layout: ore`. Ore shares these settings with ArcMenu:

- `ui.layout.canvas`, named `header` entries, `areas` and `pagination`.
- `ui.products.placement` and per-entry `ui.placement`.
- `ui.appearance`, `ui.text-align`, presentation visibility and display conditions.
- Product price/action settings, card icon/padding/lore settings and `ui.theme`.

Find coordinates and all shared fields in [Graphical Layout](ui-layout.md). Ordinary navigation buttons need explicit placements. Root `layout` continues to identify entries and result/input markers. The renderer does not import a sidebar from `main.yml` or generate arbitrary navigation buttons.

Use vertical coordinates/heights/gaps in multiples of 9. All card entries, including category cards, require height at least 99. Invalid bounds, overlapping widgets and invalid templates report errors. Category cards are clickable across the card; product cards provide primary operations and View Detail, with extra operations in information.

Ore product bodies display original item lore and generated `add-lore`, limited by `ui.products.card.lore-lines` (bundled default 6). Ordinary category descriptions use `description-lines`. Complete information remains in tooltips. Use `@t[ore]`/`@t[ore-hover]` to distinguish body and tooltip content. ArcMenu cards use compact `ui.products.price` text instead; ArcMenu does not use `price-lines` for its compact price display.

Native translatable item names are retained for client translation. Ore uses width-offset characters for alignment; the server's width estimate for a translatable component is approximate and can differ by client language/font. Font-scale keys used by ArcMenu do not scale Ore's native font.

## Ore renderer reference

Graphical templates remain under `menu.ui.renderers.ore.templates` and per-menu `ui.renderers.ore.templates`. Non-graphical icon and resource-pack settings use `menu.dialog.ore.show-item-icon` and `menu.dialog.ore.pack-format`.

| Relative key | Meaning |
| --- | --- |
| `menu.dialog.ore.show-item-icon.enabled` | Generate/render Ore item icons (global only) |
| `menu.dialog.ore.show-item-icon.auto-hide-one` | Hide count text for icon amount 1 (global only) |
| `menu.dialog.ore.pack-format` | Resource pack format; 0 selects the runtime's automatic format |
| `templates.product` | Additional generated card template settings |
| `templates.button` | Additional generated ordinary-button template settings |

Templates are based on graphical elements, so `product` also applies to ordinary entries with `appearance: card`. Shared layout/card/theme values override these template fields: `type`, `width`, `height`, `padding`, `icon-size`, `name-y`, `name-lines`, `lore-y`, `lore-lines`, `details-y`, `background`, `border`, `accent`, `footer`. Set the corresponding shared layout/card/theme option rather than expecting those template values to win.

The remaining template options are:

| Template key | Meaning |
| --- | --- |
| `texture` | Optional normal-state background PNG relative to the plugin `textures/` directory |
| `selected-texture` | Optional selected-state PNG in the same directory |
| `selected-background` | Selected-state background, `#RRGGBB` |
| `selected-border` | Selected-state border, `#RRGGBB` |

Example:

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

Supply those files under `plugins/UltimateShop/textures/shop/`. Paths must be relative PNG paths using letters, numbers, `_`, `-` and `/`; absolute paths and `..` are rejected. Template RGB colours must be quoted `#RRGGBB`. Shared theme alpha is stripped for Ore templates.

## Generate and load the resource pack

Run `/shop dialogpack` after changing Ore layout, theme, templates or relevant assets. An optional `/shop dialogpack <format>` overrides the format for that generation; the argument must be an integer from 69 to 10000. The command works in console and requires `ultimateshop.dialogpack`; the generated pack is written under `plugins/UltimateShop/pack/`. Resource preparation can run asynchronously. Distribute/load the resulting pack through your server resource-pack setup, and ensure the client loads the regenerated pack.

A reload alone does not guarantee that the client has updated assets. If the interface is misaligned or reports missing resources, check the ErrorManager output, layout validity, referenced PNGs, generated pack and active client resource pack. If Inventory opens, verify Premium, global/menu `dialog.enabled`, `dialog.layout: ore`, Paper/API support and whether that menu type supports Dialog.


[UI Modes](ui-overview.md) · [Native Dialog](dialog-menus-premium.md) · [Graphical Layout](ui-layout.md) · [Troubleshooting](troubleshooting.md)
