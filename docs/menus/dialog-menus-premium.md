# 💬Dialog and Ore Menus - Premium

Native Dialog and Ore use the same menu/product data as Inventory and Form. This build requires Paper 1.21.9+ and Premium for Dialog support. Bedrock players use their separately configured Bedrock mode.

## Select a layout

```yaml
# config.yml
menu:
  ui:
    mode:
      java: dialog
      bedrock: form
    renderers:
      dialog:
        layout: multi-action
        button-width: 150
        columns: 2
        not-auto-close: true
        auto-add-sprite:
          enabled: true
          auto-hide: true
          format: '<sprite:"{namespace}:{atlas}":{path}>'
```

Menu overrides go under root `ui`; shared body text goes under root `content`:

```yaml
title: 'Farming'
content: '<gray>Select a product.'
ui:
  mode:
    java: dialog
    bedrock: inherit
  renderers:
    dialog:
      layout: item-action-list
      button-width: 180
      columns: 2
```

| Mode / setting | Presentation | Resource pack |
| --- | --- | --- |
| `java: dialog`, `dialog.layout: multi-action` | Native action buttons in columns | Not required for ordinary text/buttons |
| `java: dialog`, `dialog.layout: item-action-list` | Native item icons and clickable labels | Not required for vanilla item icons |
| `java: ore` | Graphical cards using the shared layout | Generated Ore pack required |

Here `dialog.layout` abbreviates `ui.renderers.dialog.layout`. Do not use root `dialog.enabled`, root `dialog.content` or removed sidebar/layout formats; the renderer reads unified settings directly and does not generate or read the removed root sections.

## Native menu behaviour

Native common/shop menus use root `layout` to identify and order entries; chest gaps do not become graphical coordinates. Products open information with available operations. Ordinary buttons execute their actions with LEFT click. Display conditions and `ui.visible-in` remain active. Use `dialog` in the visibility list for both native layouts; use `ore` separately for graphical Dialogs.

A button-level `ui.renderers.dialog.layout: item-action-list` can request native item-action rendering for that entry. `ui.show-sprite` controls its label sprite choice; it is separate from the item icon supplied by item-action-list.

Provide an ordinary button with a direct `type: close` action for reliable closing and menu close actions. Not every Escape dismissal is reported by the native client interface. A fallback exit action uses the menu's inherited `default-button` label when one is needed.

## Dialog renderer reference

Global prefix: `menu.ui.renderers.dialog`. Menu overrides: `ui.renderers.dialog`. Native list `layout`, `button-width` and `columns` use the menu's inherited settings. Information and quantity helpers use the owning shop menu's renderer settings; favourite editing uses its favourite menu. Automatic sprite defaults remain global.

| Relative key | Meaning |
| --- | --- |
| `layout` | `multi-action` or `item-action-list`; Ore is selected with `ui.mode.java: ore` |
| `button-width` | Native list action width, default 150 |
| `columns` | Native multi-action columns, default 2 |
| `not-auto-close` | Keep information Dialog open after an action where supported |
| `auto-add-sprite.enabled` | Automatically prefix labels with material sprites |
| `auto-add-sprite.auto-hide` | Hide sprite tags in text paths where sprite rendering is not suitable |
| `auto-add-sprite.format` | Sprite template using `{namespace}`, `{atlas}`, `{path}` |
| `default-button` | Fallback exit action label |
| `details` | Ore product View Detail action label |
| `search.input` | Keyword input label |
| `search.buttons.search` | Search submit label |
| `buy-more.display-item` | Show selected item in quantity Dialog |
| `buy-more.title` | Quantity Dialog title, `{item-name}` |
| `buy-more.input` | Quantity input label |
| `buy-more.buttons.confirm` | Continue to information label |
| `info.display-item` | Show item preview in product information |
| `info.title` | Information title, `{item-name}` and `{amount}` |
| `info.buttons.buy`, `sell`, `buy-more`, `sell-all` | Legacy/renderer text settings; current shared action labels use `ui.products.actions.labels` and Form fallback labels |
| `info.buttons.back` | Information Back label |
| `favourite-edit.title`, `favourite-edit.content` | Editing action screen title/body, `{item-name}` |
| `favourite-edit.buttons.forward`, `backward`, `remove`, `back` | Editing controls |

`{lang:key}` references language text. Existing language keys remain `menu.dialog.*`; moving renderer configuration under unified UI does not rename language entries.

An explicit display-item `sprite` overrides the material sprite where supported. Button/product `ui.show-sprite` controls the unified icon choice. Removed display-item `dialog.show-sprite` and button `dialog.layout` aliases are not read. Automatic material mappings/translations are prepared on demand under `minecraft-resources/<version>/`.

## Information, quantity, search and favourites

Product information includes original lore plus generated product information. Its actions use shared `ui.products.actions.primary`, `extra` and `labels`, with product overrides under product `ui`. At most two primary actions are selected; extra actions are appended. Actions must be available in click bindings and pass capability filtering. Prices/limits/default amounts retain normal product behaviour.

`select-amount` opens a native number input from 1 through the product's configured maximum, then information. It does not use Inventory Buy More amount buttons. Before quantity selection, buying and selling keep their separate defaults. ArcMenu has a different flow, opening details directly.

Native Search uses a keyword input and result information; blank keywords show no results. Back restores the search keyword. It uses root result-slot capacity and result lore, but has no physical item filtering. Ordinary buttons are included; Inventory special action/state/no-result controls are not automatically reproduced as native controls. Native Favourite supports normal and editing flows with saved ordering; empty result slots are omitted. See [Search](search-menus-premium.md) and [Favourite](favourite-menus-premium.md).

<a id="ore-layout"></a>

## Ore layout and card content

Select `ui.mode.java: ore`. Ore shares these settings with ArcMenu:

- `ui.layout.canvas`, named `header` entries, `areas` and `pagination`.
- `ui.products.placement` and per-entry `ui.placement`.
- `ui.appearance`, `ui.text-align`, presentation visibility and display conditions.
- Product price/action settings, card icon/padding/lore settings and `ui.theme`.

The full coordinate example and every shared field are in [Shared layout](arcmenu-menus-premium.md#shared-layout). Ordinary navigation buttons need explicit placements. Root `layout` continues to identify entries and result/input markers. The renderer does not import a sidebar from `main.yml` or generate arbitrary navigation buttons.

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
