# 🔍 Search Menus — Premium

Search finds shop products visible to the player by item and/or name. Inventory supports physical item filters and chat keyword input. Form, native Dialog and ArcMenu provide their own keyword flows while retaining source product prices and trade rules.

Store files in `menus/search_menus/`. Open the bundled screen with `/shop menu search` or an `open_menu` action. Files in this folder are automatically Search menus. Common-folder files can use `menu-type: search` or the legacy `menu.search-gui.menu` ID list in `config.yml`.

<a id="inventory-example"></a>

## Inventory example

Save as `menus/search_menus/my-search.yml`:

```yaml
title: 'Search'
content: 'Find a product.'
size: 27
layout:
  - '000HNC000'
  - '001111100'
  - '066666660'
dialog:
  enabled: false
bedrock:
  enabled: false
arcmenu:
  enabled: false
input-item: '1'
result-item: '6'
action-items:
  H:
    action-type: input-name
    display-item:
      material: HOPPER
      name: '&eEnter a name'
  C:
    action-type: clear-search
    display-item:
      material: BARRIER
      name: '&cClear'
state-items:
  N:
    empty-input:
      display-item:
        material: PAPER
        name: '&7Waiting for input'
    has-input:
      display-item:
        material: COMPASS
        name: '&a{showing-amount}/{result-amount} results'
no-result-item:
  slot: 22
  display-item:
    material: BARRIER
    name: '&cNo results'
result-lore:
  - '&7Shop: {shop}'
  - '&7Product: {product}'
```

Reload, then open `/shop menu my-search`. In normal selection, this uses Inventory: deposit filter items in `1` slots, click `H` to enter a keyword, or `C` to clear. Global forced UI takes priority over the local switches.

## Search configuration

| Key | Purpose |
| --- | --- |
| `input-item`, `input-items` | Combined physical input markers; default `1` if none are configured |
| `result-item` | Result marker; default `6`; occurrences set result capacity |
| `action-items.<id>.action-type` | `input-name` (alias `search-name`) or `clear-search` |
| `action-items.<id>.display-item` | Special action appearance |
| `clear-search-item.<id>` | Legacy action section; defaults to `clear-search` |
| `state-items.<id>.empty-input.display-item` | Status before input |
| `state-items.<id>.has-input.display-item` | Status after input |
| `no-result-item.slot` | Zero-based Inventory slot for no-match display |
| `no-result-item.display-item` | No-match appearance |
| `result-lore` | Extra result lore; `{shop}` and `{product}` refer to its source |

Ordinary navigation belongs in `buttons`. Put special action/state IDs in root `layout`, reserve input/result markers, and use a valid no-result slot. Longer IDs follow [Layout & Buttons](layout-and-buttons.md).

Status items support `{result-amount}` (all matches), `{showing-amount}` (displayed matches), `{input-amount}` (physical input count) and `{name-keyword}`. These values belong to search status rendering rather than arbitrary menu text.

## Keyword and item input

Inventory physical inputs filter products linked to those items. A keyword filters product/item names; text-only search works without deposited stacks. Name matching uses the shared [localized item-name system](../features/localized-item-name-premium.md).

Chat prompt keywords are global in `config.yml`:

```yaml
menu:
  prompt:
    cancel-keyword: cancel
  search-gui:
    prompt:
      clear-keyword: clear
```

`cancel` preserves the prior keyword; `clear` removes it. Bundled `{lang}` values use the configured language. Clear-search also returns physical Inventory input. Closing or navigating away returns remaining deposited stacks. Result/display icons are GUI items, not deposited stock.

## Presentation behavior

| Presentation | Input | Controls |
| --- | --- | --- |
| Inventory | Physical items and chat keyword | Configured action, state and no-result items |
| Bedrock Form | Custom Form keyword input | Search button and ordinary navigation; no physical input/status slots |
| Native Dialog | Native keyword input | Search submission and result information; no physical input slots |
| ArcMenu | Chat keyword input | Graphical action/state/no-result controls and product cards |

Form opens its home screen before keyword entry. Blank Form/Dialog keywords show no products. Input prefills the previous keyword; cancellation preserves it. Product information Back restores the search. Internal input submission/cancellation does not repeatedly run menu lifecycle actions; ordinary navigation leaves the flow.

Form labels use menu `bedrock.search.*` with global `menu.bedrock.search.*` fallback. Dialog uses `dialog.search.*` and global `menu.dialog.search.*`.

## Graphical search

Search supports ArcMenu graphics. Configure `ui.products.placement` for results and `ui.placement` on ordinary and special controls. Root result-slot capacity still limits how many source results can be shown; graphical pagination does not expand that limit.

Search does not render Ore graphics or generate Ore assets. `dialog.layout: ore` selects native `multi-action` for this type. Presentation switches and forced selection follow [UI Modes](ui-overview.md).

[Favourite Menus](favourite-menus-premium.md) · [ArcMenu](arcmenu-menus-premium.md) · [Graphical UI Layout](../menus-getting-started/ui-layout.md)
