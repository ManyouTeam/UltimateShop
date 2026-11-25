# 🔍Search Menus - Premium

Search menus let players find visible shop products by item and/or name. Inventory supports dropping a real item into an input slot or typing a chat keyword; Bedrock Form, Native Dialog, and ArcMenu each provide their own keyword input flow while keeping source shop prices and trade rules.

Files live in `menus/search_menus/` (auto-detected by folder) and the built-in menu opens via `/shop menu search` or `open_menu`. Legacy files in `common_menus` can declare `menu-type: search` at the root or be listed in `config.yml` → `menu.search-gui.menu`.

<a id="inventory-example"></a>

## Inventory Example

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

Reload and run `/shop menu my-search`: place an item into `1` slots to filter by item, click `H` to enter a keyword in chat, and click `C` to clear (global `menu.force-ui` overrides menu switches).

## Search Configuration

| Key | Purpose |
| --- | --- |
| `input-item`, `input-items` | Physical item-filter symbols (combined; defaults to `'1'` if omitted) |
| `result-item` | Result slot symbol (defaults to `'6'`; count determines result capacity) |
| `action-items.<id>.action-type` | `input-name` (alias `search-name`) or `clear-search` |
| `action-items.<id>.display-item` | Special action button appearance |
| `clear-search-item.<id>` | Legacy clear action section (defaults to `clear-search`) |
| `state-items.<id>.empty-input.display-item` | State display before any input |
| `state-items.<id>.has-input.display-item` | State display when input is active |
| `no-result-item.slot` | Zero-based slot for the no-results item |
| `no-result-item.display-item` | No-results item appearance |
| `result-lore` | Extra lore on results (`{shop}` and `{product}` show source) |

Common navigation belongs in `buttons`; place special action and state IDs in root `layout`, reserve input/result symbols, and set a valid `no-result-item.slot` (see [Layout & Buttons](layout-and-buttons.md)).

State items support `{result-amount}` (total matches), `{showing-amount}` (currently shown), `{input-amount}` (deposited filter items), and `{name-keyword}`. These placeholders are for search state rendering only.

## Keyword and Item Input

* **Item filter**: placing an item in an input slot matches products associated with that item.
* **Name filter**: entering a keyword matches product or item names (pure text search needs no deposited item); name matching uses [Localized Item Name](../features/localized-item-name-premium.md).

Global chat-prompt keywords in `config.yml`:

```yaml
menu:
  prompt:
    cancel-keyword: cancel
  search-gui:
    prompt:
      clear-keyword: clear
```

`cancel` keeps the previous keyword and `clear` resets it (built-in `{lang}` values follow the active language). Clearing search or closing/leaving the menu returns deposited filter items to the player.

## Behaviour Across Presentations

| Presentation | Input | Controls |
| --- | --- | --- |
| Inventory | Physical item + chat keyword | Special actions, state, no-result item, and common buttons |
| Bedrock Form | Form text input for keyword | Search button and common navigation (no physical input/state slots) |
| Native Dialog | Native keyword input | Search submit and result info (no physical input slots) |
| ArcMenu | Chat keyword input | Graphical actions, state, no-result control, and product cards |

In Form and Dialog, empty keywords show no products, reopening pre-fills the previous keyword, and cancelling preserves it; backing out of product info returns to search. Internal search submits/cancels do not re-run menu `open-actions` / `close-actions`—only leaving via normal navigation does.

Form labels read menu `bedrock.search.*` then global `menu.bedrock.search.*`; Dialog labels read `dialog.search.*` and `menu.dialog.search.*`.

## Graphical Search

Search supports **ArcMenu** graphical UI: results use `ui.products.placement`, while special controls and common buttons use `ui.placement`. Root result-slot capacity still caps how many source results are collected—graphical pagination does not raise that cap.

Search **does not render Ore graphics** or generate Ore packs (`dialog.layout: ore` falls back to native `multi-action`). See [UI Modes](ui-overview.md).

[Favourite Menus](favourite-menus-premium.md) · [ArcMenu UI](arcmenu-menus-premium.md) · [Graphical UI Layout](../menus-getting-started/ui-layout.md)
