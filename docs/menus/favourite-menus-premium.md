# ❤️Favourite Menus - Premium

Favourite menus store a player's chosen shop products and their order. Files live in `plugins/UltimateShop/menus/favourite_menus/`; the bundled example is `favourite.yml`. Open with `/shop menu favourite` or an `open_menu` action. Saved lists are associated with the favourite menu ID, so use that same ID when adding a product.

Files in the dedicated folder are automatically Favourite menus. Common-folder files can use `menu-type: favourite` (alias `favorite`) or be listed under `menu.favourite-gui.menu` in `config.yml`. Dedicated files do not require that legacy list. Keep IDs unique.

## Add products

The built-in default binds `add-favourite` to `SWAP_OFFHAND` (F). Its configured action in `menu.click-event-actions.add-favourite` uses the `add_favourite` action:

```yaml
type: add_favourite
menu: favourite
shop: '{shop}'
item: '{item}'
```

Use it within an action list with the relevant shop/product context. Products must allow favourites. Shared Form/Dialog/graphical extra actions can expose the same operation; adding an action label alone does not create its click binding. See [Custom Click Event](../features/custom-click-event-premium.md).

## Menu example

```yaml
menu-type: favourite
title: 'Favourites'
content: 'Your saved products.'
size: 27
layout:
  - '000000000'
  - '011111110'
  - '0000D0000'
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

## Configuration reference

All [common menu settings](general-menus.md#common-menu-configuration) remain available. Ordinary navigation controls belong under `buttons`.

| Additional key | Meaning |
| --- | --- |
| `result-item` | Result marker used in `layout` |
| `result-items` | List of additional result markers; combined with singular `result-item`; when neither is supplied, marker `6` is used |
| `edit-mode-item.<id>.normal-display-item` | Display Item Format when editing is off |
| `edit-mode-item.<id>.editing-display-item` | Display when editing is on |
| `edit-mode-item.<id>.display-item` | Fallback display if the current normal/editing variant is missing |
| `edit-mode-item.<id>.slot` | Optional zero-based fallback slot when the ID is not found in `layout` |
| `edit-mode-item.<id>.ui` | Visibility/placement metadata for the edit toggle |
| `empty-item.display-item` | Placeholder shown in unoccupied result slots |
| `empty-item.ui` | Visibility/graphical placement of empty-slot placeholders |
| `result-lore` | Extra lore in normal mode; supports `{shop}` and `{product}` |
| `editing-result-lore` | Extra lore in edit mode; keep instructions appropriate to the presentation |

Edit-toggle displays support `{editing}` and `{edit-mode}` (true/false), plus `{favourite-amount}` and `{favorite-amount}`. Empty-slot displays support those amount aliases and `{slot-index}` (one-based result position).

Result slots are filled in saved order, limited by the number of configured result slots. Increasing graphical pagination capacity does not by itself increase that source limit. Use one edit toggle: the runtime keeps one resolved edit-mode button/slot rather than a collection of independent toggles.

## Normal and editing modes

In Inventory normal mode, product clicks use the product's configured click operations. Selecting the edit toggle switches to editing; it does not buy or sell. In Inventory editing:

| Input | Operation |
| --- | --- |
| Left click | Move the product one position forward |
| Right click | Move it one position backward |
| Drop / Control-Drop | Remove it from this favourite list |

Removing a favourite does not remove shop stock or player items. Toggle editing off to return to product operations. Saved product references and order are shared across presentations.

## Presentation-specific configuration

Select the interface in this menu's `ui.mode`, or inherit global defaults.

| Presentation | Normal mode | Editing mode |
| --- | --- | --- |
| Inventory | Product click bindings | Left/right/drop controls above |
| Form | Product information Form | Selecting a product opens Forward/Backward/Remove/Back; closing returns to the editing list |
| Native Dialog / Ore | Product information/actions | Dedicated editing controls; native Dialog omits empty result slots |
| ArcMenu | Product cards and shared actions/details | Forward/Backward/Remove buttons on each card; also left/right/middle edit clicks (middle removes) |

Form can display configured empty-slot buttons. Ore/ArcMenu require graphical `ui.placement` for the toggle and ordinary controls, and `ui.products.placement` for result cards. The bundled example disables graphical placement on empty slots.

Global Form editing text is under `menu.ui.renderers.form.favourite-edit`: `title`, `content`, and `buttons.forward`, `backward`, `remove`, `back`. Dialog has the same structure under `menu.ui.renderers.dialog.favourite-edit`. Titles/content accept `{item-name}`.

ArcMenu card labels default to the Form favourite edit labels. Override them per menu with `ui.text.favourite-edit.forward`, `backward`, `remove`. Use presentation conditions in result lore when different clients need different instructions; see [Add Lore](display-item-add-lore.md).
