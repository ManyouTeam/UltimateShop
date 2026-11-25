# ❤️Favourite Menus - Premium

Favourite menus store each player's saved products and their order. Every list belongs to a specific favourite menu ID, so add-favourite actions must target that same ID. Saved products keep their source shop's prices and trade rules.

Files live in `menus/favourite_menus/` (auto-detected by folder) and the built-in menu opens via `/shop menu favourite` or `open_menu`. Legacy files in `common_menus` can set `menu-type: favourite` (alias `favorite`) or use the global `menu.favourite-gui.menu` list.

<a id="add-products"></a>

## Add Products to Favourites

The built-in `add-favourite` action binds to `SWAP_OFFHAND` (`F`) and targets the built-in `favourite` list:

```yaml
type: add_favourite
menu: favourite
shop: '{shop}'
item: '{item}'
```

Use this in any action list with product context. For a custom list such as `my-favourites`, change `menu` to `my-favourites` (and ensure the product allows favouriting). Native and graphical info/extra-action controls automatically expose the configured action, though setting labels alone never creates a binding.

## Inventory Example

Save as `menus/favourite_menus/my-favourites.yml`:

```yaml
title: 'Favourites'
content: 'Your saved products.'
size: 27
layout:
  - '000000000'
  - '011111110'
  - '0000D0000'
dialog:
  enabled: false
bedrock:
  enabled: false
arcmenu:
  enabled: false
result-item: '1'
edit-mode-item:
  D:
    normal-display-item:
      material: NAME_TAG
      name: '&eEdit favourites'
    editing-display-item:
      material: WRITABLE_BOOK
      name: '&aFinish editing'
empty-item:
  display-item:
    material: GRAY_STAINED_GLASS_PANE
    name: '&7Empty favourite slot'
result-lore:
  - '&7Shop: {shop}'
  - '&7Product: {product}'
editing-result-lore:
  - '&eLeft: move forward'
  - '&eRight: move backward'
  - '&cDrop: remove'
```

Reload, add products to `my-favourites`, and run `/shop menu my-favourites` (global `menu.force-ui` overrides menu switches).

## Favourite Configuration

| Key | Purpose |
| --- | --- |
| `result-item`, `result-items` | Favourite product slot symbols (combined; defaults to `'6'` if omitted) |
| `edit-mode-item.<id>.normal-display-item` | Edit toggle appearance in normal mode |
| `edit-mode-item.<id>.editing-display-item` | Edit toggle appearance in edit mode |
| `edit-mode-item.<id>.display-item` | Fallback appearance when mode-specific items are omitted |
| `edit-mode-item.<id>.slot` | Zero-based fallback slot if the toggle ID is not in `layout` |
| `empty-item.display-item` | Placeholder item shown in unfilled result slots |
| `result-lore` | Normal-mode extra lore (`{shop}` and `{product}` show source) |
| `editing-result-lore` | Edit-mode hint lore |
| Special control `menu-visibility` | Per-presentation visibility |
| Special control `ui` | ArcMenu placement and appearance |

Only one resolved edit toggle is used per menu. Root `result-item` slot count caps how many saved products are shown in saved order; graphical pagination does not increase capacity.

Edit-toggle text supports `{editing}`, `{edit-mode}`, `{favourite-amount}`, and `{favorite-amount}`. Empty-slot text supports those count aliases plus `{slot-index}` (1-based result position).

## Normal vs. Edit Mode

Normal mode runs product click actions; clicking the edit toggle only switches modes without trading:

| Inventory Input in Edit Mode | Action |
| --- | --- |
| Left-click | Move one slot forward |
| Right-click | Move one slot backward |
| Drop / Ctrl+Drop | Remove from this list |

Removing a favourite never deletes player items or shop stock, and toggling edit mode off restores normal trading. The saved list and order are shared across all presentations.

## Behaviour in Other Presentations

| Presentation | Normal Mode | Edit Mode |
| --- | --- | --- |
| Bedrock Form | Opens product info form | Selecting a product shows Forward / Backward / Remove / Back |
| Native Dialog | Product info and actions | Dedicated edit controls (empty result slots omitted) |
| ArcMenu | Product cards, actions, and details | Cards show Forward / Backward / Remove; supports Left / Right / Middle-click editing (Middle removes) |

Bedrock Form can show configured empty-slot buttons. In ArcMenu, the edit toggle and navigation need `ui.placement`, while saved product cards use `ui.products.placement` (empty-slot graphical placement can be disabled).

Form edit text reads `bedrock.favourite-edit` (fallback `menu.bedrock.favourite-edit`); Dialog reads `dialog.favourite-edit` (`menu.dialog.favourite-edit`), including `title`, `content`, and `buttons.forward/backward/remove/back` (title/content support `{item-name}`). ArcMenu card edit labels default to the Form labels and can be overridden via `ui.text.favourite-edit.forward/backward/remove`; use [presentation conditions](display-item-add-lore.md#presentation-conditions) for per-UI edit lore.

Favourite menus **do not render Ore graphics** (`dialog.layout: ore` falls back to native `multi-action` and generates no Ore resources).

[UI Modes](ui-overview.md) · [Custom Click Event](../features/custom-click-event-premium.md) · [Graphical UI Layout](../menus-getting-started/ui-layout.md)
