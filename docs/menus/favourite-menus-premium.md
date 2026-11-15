# ❤️ Favourite Menus — Premium

Favourite menus save a player's chosen shop products and order. The list belongs to the Favourite menu ID, so actions adding products must target that same ID. Products retain their source shop prices and trade rules.

Save files in `menus/favourite_menus/`. Open the bundled menu with `/shop menu favourite` or an `open_menu` action. The folder assigns the type; Common-folder files can use `menu-type: favourite` (alias `favorite`) or the legacy `menu.favourite-gui.menu` list.

<a id="add-products"></a>

## Add a product

The bundled `add-favourite` operation uses `SWAP_OFFHAND` (F). Its custom action targets the bundled `favourite` list:

```yaml
type: add_favourite
menu: favourite
shop: '{shop}'
item: '{item}'
```

Use this inside an action list with product context. For a custom list such as `my-favourites`, change `menu` to that ID in the adding action. Products must allow favourites. Native and graphical information/extra controls can expose the same configured operation; a label alone does not create an action binding.

## Inventory example

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

Reload, add products to `my-favourites`, then open `/shop menu my-favourites`. The example selects Inventory in normal selection; global forced UI still applies.

## Favourite configuration

| Key | Purpose |
| --- | --- |
| `result-item`, `result-items` | Combined result markers; default `6` when neither is supplied |
| `edit-mode-item.<id>.normal-display-item` | Edit toggle appearance in normal mode |
| `edit-mode-item.<id>.editing-display-item` | Toggle appearance in editing mode |
| `edit-mode-item.<id>.display-item` | Fallback appearance when a variant is missing |
| `edit-mode-item.<id>.slot` | Zero-based fallback when the toggle ID is absent from layout |
| `empty-item.display-item` | Placeholder for an unfilled result slot |
| `result-lore` | Normal result instructions; `{shop}` and `{product}` identify the source |
| `editing-result-lore` | Editing instructions |
| Special control `menu-visibility` | Presentation visibility |
| Special control `ui` | ArcMenu placement and appearance |

Use one resolved edit toggle. Root result markers determine the maximum displayed source entries, filled in saved order. Graphical pagination does not independently increase this capacity.

Edit-toggle text supports `{editing}`, `{edit-mode}`, `{favourite-amount}` and `{favorite-amount}`. Empty-slot text supports the amount aliases and `{slot-index}`, the one-based result position.

## Normal and editing modes

Normal mode uses product click operations. The edit toggle switches modes without executing a trade.

| Inventory input in editing mode | Operation |
| --- | --- |
| Left click | Move forward one position |
| Right click | Move backward one position |
| Drop / Control-Drop | Remove from this list |

Removing a favourite does not remove player items or shop stock. Turn editing off to resume product operations. The saved list and order are shared across presentations.

## Other presentations

| Presentation | Normal mode | Editing mode |
| --- | --- | --- |
| Bedrock Form | Product information Form | Product opens Forward/Backward/Remove/Back controls |
| Native Dialog | Product information/actions | Dedicated edit controls; empty result slots are omitted |
| ArcMenu | Product cards, operations and details | Forward/Backward/Remove card buttons; left/right/middle edit clicks, with middle removing |

Form may display configured empty-slot buttons. ArcMenu needs `ui.placement` for the edit toggle and navigation, and `ui.products.placement` for result cards. Empty-slot graphical placement can be disabled.

Form editing text uses `bedrock.favourite-edit` with global `menu.bedrock.favourite-edit` fallback; Dialog uses `dialog.favourite-edit` and `menu.dialog.favourite-edit`. These contain `title`, `content` and `buttons.forward/backward/remove/back`; titles/content support `{item-name}`.

ArcMenu card edit labels default to the Form labels and can be overridden with `ui.text.favourite-edit.forward/backward/remove`. Adapt editing lore to each interface with [presentation conditions](display-item-add-lore.md#presentation-conditions).

Favourite does not render Ore graphics. Setting `dialog.layout: ore` uses native `multi-action` and generates no Ore assets for this type.

[UI Modes](ui-overview.md) · [Custom Click Event](../features/custom-click-event-premium.md) · [Graphical UI Layout](../menus-getting-started/ui-layout.md)
