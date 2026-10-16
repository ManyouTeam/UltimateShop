# Ore UI — Premium

Ore is UltimateShop's graphical Dialog-based renderer. It requires Premium, supported Paper (1.21.9+), and a generated client resource pack. Select `ore` separately from native Dialog's `multi-action`/`item-action-list` layout. Bedrock players follow `ui.mode.bedrock`.

## Quick start

```yaml
# config.yml
menu:
  ui:
    mode:
      java: ore
      bedrock: form
```

Give ordinary navigation buttons graphical placements (see [Graphical Layout](ui-layout.md)); Inventory gaps do not automatically become Ore coordinates.

## Ore layout and card content

Select `ui.mode.java: ore`. Ore shares these settings with ArcMenu:

- `ui.layout.canvas`, named `header` entries, `areas` and `pagination`.
- `ui.products.placement` and per-entry `ui.placement`.
- `ui.appearance`, `ui.text-align`, presentation visibility and display conditions.
- Product price/action settings, card icon/padding/lore settings and `ui.theme`.

Find coordinates and all shared fields in [Graphical Layout](ui-layout.md). Ordinary navigation buttons need explicit placements. Root `layout` continues to identify entries and result/input markers. The renderer does not import a sidebar from `main.yml` or generate arbitrary navigation buttons.

Use vertical coordinates/heights/gaps in multiples of 9. All card entries, including category cards, require height at least 99. Invalid bounds, overlapping widgets and invalid templates report errors. Category cards are clickable across the card; product cards provide primary operations and View Detail, with extra operations in information.

Ore product bodies display original item lore and generated `add-lore`, limited by `ui.products.card.lore-lines` (bundled default 6). Ordinary category descriptions use `description-lines`. Complete information remains in tooltips. Use `@t[ore]`/`@t[ore-hover]` to distinguish body and tooltip content. ArcMenu cards use compact `ui.products.price` text instead; the current ArcMenu code does not read the bundled `price-lines` key.

Native translatable item names are retained for client translation. Ore uses width-offset characters for alignment; the server's width estimate for a translatable component is approximate and can differ by client language/font. Font-scale keys used by ArcMenu do not scale Ore's native font.

## Ore renderer reference

Global prefix: `menu.ui.renderers.ore`. Per-menu template overrides: `ui.renderers.ore.templates`. Icon switches and pack format are currently read globally.

| Relative key | Meaning |
| --- | --- |
| `show-item-icon.enabled` | Generate/render Ore item icons |
| `show-item-icon.auto-hide-one` | Hide count text for icon amount 1 |
| `pack-format` | Resource pack format; 0 selects the runtime's automatic format |
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

Run `/shop dialogpack` after changing Ore layout, theme, templates or relevant assets. An optional `/shop dialogpack <format>` overrides the format for that generation; the argument must be an integer from 69 to 10000. The command works in console and requires `ultimateshop.dialogpack`; the generated pack is written under `plugins/UltimateShop/pack/`. Resource preparation can run asynchronously. Distribute/load the resulting pack through your server resource-pack setup, and ensure the client uses the new version.

A reload alone does not guarantee that the client has updated assets. If the interface is misaligned or reports missing resources, check the ErrorManager output, layout validity, referenced PNGs, generated pack and client pack version. If Inventory opens, verify Premium, Paper/API support, mode overrides and whether the requested menu type supports Dialog.


[UI Modes](ui-overview.md) · [Native Dialog](dialog-menus-premium.md) · [Graphical Layout](ui-layout.md) · [Troubleshooting](troubleshooting.md)
