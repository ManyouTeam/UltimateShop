# Native Dialog UI — Premium

Native Dialog shares product/action data with Inventory and Form. It requires Premium and Paper 1.21.9+ with supported Dialog APIs. [Ore](ore-menus-premium.md) is the graphical Dialog-based renderer covered separately.

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

## Ore layout

Ore's graphical layout, templates and resource-pack setup are now documented in [Ore UI](ore-menus-premium.md). This anchor remains for older links.

[UI Modes](ui-overview.md) · [Ore](ore-menus-premium.md) · [Troubleshooting](troubleshooting.md)
