# 💬 Native Dialog UI - Premium

Native Dialog shares product/action data with Inventory and Form. It requires Premium and Paper 1.21.9+ with supported Dialog APIs. [Ore](ore-menus-premium.md) is the graphical Dialog-based renderer covered separately.

## Select a layout

```yaml
# config.yml
menu:
  dialog:
    enabled: true
    not-auto-close: true
    auto-add-sprite:
      enabled: true
      auto-hide: true
      format: '<sprite:"{namespace}:{atlas}":{path}>'
```

```yaml
# Menu file
title: 'Farming'
dialog:
  enabled: true
  layout: multi-action
  content: '<gray>Select a product.'
  button-width: 150
  columns: 2
```

Choose `dialog.layout: multi-action` for native action buttons, `item-action-list` for clickable item rows, or `ore` for the graphical Ore renderer. Global `menu.dialog.enabled` and menu `dialog.enabled` must allow Dialog. ArcMenu takes precedence when both ArcMenu switches are enabled and its integration is available.

Buttons can override a native layout using `buttons.<id>.dialog.layout: item-action-list`; use `dialog.show-sprite` for the button or product sprite preference. `dialog.content` supplies the Dialog body; root `content` is an optional fallback.

## Native menu behaviour

Native common/shop menus use root `layout` to identify and order entries; chest gaps do not become graphical coordinates. Products open information with available operations. Ordinary buttons execute their actions with LEFT click. Display conditions and `menu-visibility` remain active. Use `menu-visibility.dialog: true` for native Dialog; `menu-visibility.item-action-list` can override the inherited value and `menu-visibility.ore` controls Ore separately.

A button-level `dialog.layout: item-action-list` can request native item-action rendering for that entry. `dialog.show-sprite` controls its label sprite choice; it is separate from the item icon supplied by item-action-list.

Provide an ordinary button with a direct `type: close` action for reliable closing and menu close actions. Not every Escape dismissal is reported by the native client interface. A fallback exit action uses the menu's inherited `default-button` label when one is needed.

## Dialog renderer reference

Global prefix: `menu.dialog`. Menu overrides: `dialog`. Native list `layout`, `button-width` and `columns` use the menu's inherited settings. Information and quantity helpers use the owning shop menu's renderer settings; favourite editing uses its favourite menu. Automatic sprite defaults remain global.

| Relative key | Meaning |
| --- | --- |
| `layout` | `multi-action` or `item-action-list`; Ore is selected with `dialog.layout: ore` |
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
| `info.buttons.buy`, `sell`, `buy-more`, `sell-all` | Renderer-specific fallback text for native Dialog; graphical labels can be configured separately through `ui.products.actions.labels` |
| `info.buttons.back` | Information Back label |
| `favourite-edit.title`, `favourite-edit.content` | Editing action screen title/body, `{item-name}` |
| `favourite-edit.buttons.forward`, `backward`, `remove`, `back` | Editing controls |

`{lang:key}` references language text. Existing language keys remain `menu.dialog.*`; restoring the native configuration path does not rename language entries.

An explicit display-item `sprite` overrides the material sprite where supported. Button/product `dialog.show-sprite` controls the unified icon choice. Use button/product `dialog.show-sprite` and button `dialog.layout` instead of display-item `dialog.show-sprite` or button `dialog.layout`. Automatic material mappings/translations are prepared on demand under `minecraft-resources/<version>/`.

## Information, quantity, search and favourites

Product information includes original lore plus generated product information. Its actions use shared `ui.products.actions.primary`, `extra` and `labels`, with product overrides under product `ui`. At most two primary actions are selected; extra actions are appended. Actions must be available in click bindings and pass capability filtering. Prices/limits/default amounts retain normal product behaviour.

`select-amount` opens a native number input from 1 through the product's configured maximum, then information. It does not use Inventory Buy More amount buttons. Before quantity selection, buying and selling keep their separate defaults. ArcMenu has a different flow, opening details directly.

Native Search uses a keyword input and result information; blank keywords show no results. Back restores the search keyword. It uses root result-slot capacity and result lore, but has no physical item filtering. Ordinary buttons are included; Inventory special action/state/no-result controls are not automatically reproduced as native controls. Native Favourite supports normal and editing flows with saved ordering; empty result slots are omitted. See [Search](search-menus-premium.md) and [Favourite](favourite-menus-premium.md).

<a id="ore-layout"></a>

<a id="ore-layout-and-card-content"></a>
<a id="ore-renderer-reference"></a>
<a id="generate-and-load-the-resource-pack"></a>

## Ore layout

Ore's graphical layout, templates and resource-pack setup are now documented in [Ore UI](ore-menus-premium.md). This anchor remains for older links.

[UI Modes](ui-overview.md) · [Ore](ore-menus-premium.md) · [Troubleshooting](troubleshooting.md)
