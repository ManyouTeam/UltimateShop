# 🔍 Search Menus - Premium

Search menus find products visible to the player by physical item and/or name. Store files in `plugins/UltimateShop/menus/search_menus/`; the bundled example is `search.yml`. Open with `/shop menu search` or an `open_menu` action.

A file in this folder is automatically a Search menu. For a file in `menus/common_menus/`, use `menu-type: search` or list its ID under `menu.search-gui.menu` in `config.yml`. Dedicated-folder files do not need that list. Keep IDs unique.

<a id="inventory-example"></a>

## Create an Inventory Search menu

```yaml
title: 'Search'
content: 'Find a product.'
size: 27
layout:
  - '000HNC000'
  - '001111100'
  - '066666660'
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

## Search-only keys

The [Common menu settings](general-menus.md) remain available. Use ordinary `buttons` for Back/Close/navigation.

| Additional key | Meaning |
| --- | --- |
| `input-item` | Single input-slot marker, default `1` when no input markers are configured |
| `input-items` | Additional input markers; combined with `input-item` |
| `result-item` | Result-slot marker, default `6`; occurrences determine how many results can be shown |
| `action-items.<id>.action-type` | `input-name` (alias `search-name`) for keyword entry; `clear-search` to clear the search |
| `action-items.<id>.display-item` | Appearance of the special action button |
| `clear-search-item.<id>` | Legacy action-button section; defaults `action-type` to `clear-search` |
| `state-items.<id>.empty-input.display-item` | Display before items/keywords are present |
| `state-items.<id>.has-input.display-item` | Display when search input is present |
| `no-result-item.slot` | Fixed Inventory slot, zero-based; `-1` means no valid slot |
| `no-result-item.display-item` | Display when a search has no matches |
| `result-lore` | Extra lore on result entries; `{shop}` and `{product}` identify the source |
| Special button `menu-visibility` | Boolean presentation visibility for a special button |
| Special button `ui.placement` / `ui.appearance` | Ore/ArcMenu position and appearance |

Layout markers use the [normal token rules](layout-and-buttons.md), including backtick-delimited longer IDs. Do not fill input/result markers with ordinary buttons. Place each special action/state ID in root `layout`; use a valid `no-result-item.slot`. Result capacity remains relevant to non-Inventory views.

Status display placeholders are `{result-amount}` (all matches), `{showing-amount}` (shown matches), `{input-amount}` (physical input count) and `{name-keyword}` (keyword). These are search status values, not global placeholders available in arbitrary menus.

## Search and input lifecycle

Inventory input slots accept physical item stacks. Search finds shop products linked to those items; a keyword filters by product/item names. Text-only search works without item stacks. Name matching uses the shared item-name/localization system; see [Localized Item Name - Premium](../features/localized-item-name-premium.md).

In Inventory, `input-name` opens a chat prompt. The shared cancellation and clearing settings in `config.yml` are:

```yaml
menu:
  prompt:
    cancel-keyword: cancel
  search-gui:
    prompt:
      clear-keyword: clear
```

`{lang}` uses the configured language value. Cancel leaves the prior keyword; clear removes it. Clear-search also returns physical input items in Inventory. Closing/navigating away returns remaining input rather than consuming display/result icons. Result icons are GUI items, not player-owned stock.

## How search differs by presentation

| Presentation | Search input and controls |
| --- | --- |
| Inventory | Physical item slots, chat keyword entry, action/state/no-result items |
| Form | Dedicated Search button opens a Custom Form; ordinary buttons are included; no physical input or Inventory special status controls |
| Native Dialog / Ore | Keyword input and result information; no physical item deposit filter |
| ArcMenu | Chat keyword entry, clear-search, action/state/no-result controls, graphical result cards |

Form opens its home first. Search input prefills the previous keyword; cancel returns to that home and preserves it. Form/Dialog blank keywords show no products. Selecting a result opens information; Back restores the search and keyword. Editing/submitting/cancelling the built-in input flow does not repeatedly run menu open/close actions. Ordinary navigation buttons leave the search flow and run with LEFT click in Form/Dialog.

Form text defaults are `menu.bedrock.search.input` and `menu.bedrock.search.buttons.search`; Dialog uses `menu.dialog.search.*`. Chat keyword controls still use the prompt keywords above. Choose presentations through the global/menu `dialog.enabled`, `bedrock.enabled` and `arcmenu.enabled` switches.

For ArcMenu/Ore, configure `ui.layout`, `ui.products.placement`, and `ui.placement` on graphical controls. Layout pagination does not remove the result-slot capacity defined at root. See [ArcMenu - Premium](arcmenu-menus-premium.md) and [Dialog - Premium](dialog-menus-premium.md) and [Ore - Premium](ore-menus-premium.md) for exact renderer differences.

## Related documentation

[UI Modes](ui-overview.md) · [Layout and Buttons](layout-and-buttons.md) · [Troubleshooting](troubleshooting.md)
