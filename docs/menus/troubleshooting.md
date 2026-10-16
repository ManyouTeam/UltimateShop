# 🩺 Menu troubleshooting

Work from the symptom, not by changing unrelated keys. File paths below are relative to `plugins/UltimateShop/`.

## Menu cannot open

1. Verify the ID and file location: `menus/common_menus/<id>.yml` for Common, `shops/<shop>.yml` for a Shop menu.
2. Use `/shop reload` after editing YAML, then `/shop menu <id>` to try again.
3. Check root `conditions` and command permissions. A shop opens by its **shop ID**, not necessarily the template filename.
4. Check console errors for missing shop products or invalid menu files.

## Java opens Inventory instead of Dialog, Ore or ArcMenu <mark style="color:red;">- Premium</mark>

| Intended presentation | Check first |
| --- | --- |
| Native Dialog | Both global `menu.dialog.enabled` and menu `dialog.enabled`; supported Paper Dialog API and Premium |
| Ore | Dialog switches above **plus** menu `dialog.layout: ore`; graphical layout and loaded Ore pack |
| ArcMenu | Global `menu.arcmenu.enabled` and menu `arcmenu.enabled`; compatible integration and client setup |

Java priority is ArcMenu → Dialog/Ore → Inventory. If ArcMenu cannot open, Dialog/Ore must still be enabled to serve as the next option. Item Sell/Sell All remain physical Inventory interfaces.

**Do not use `ui.mode.java`:** it is not a recognized selection key.

## Bedrock opens Inventory, or Form image is missing <mark style="color:red;">- Premium</mark>

- Check global `menu.bedrock.enabled` and menu `bedrock.enabled`.
- Verify Floodgate detection and the ability to deliver forms from your backend.
- Explicit Form icons use `display-item.bedrock.icon: 'path;;…'` or `url;;…`. `path;;` is a **client** texture path.
- Automatic Form icons depend on the material texture mapping; Java custom model data does not automatically supply a Bedrock image.
- A blank display-item name or `display-item.bedrock.hide: true` can intentionally omit a Form button.

## A button/product is missing

| Interface | Check |
| --- | --- |
| Any | Source entry ID, display conditions, `menu-visibility` and whether the menu type exposes it |
| Inventory | Root `layout` marker, `size` and slot count |
| Form | Button name, `display-item.bedrock.hide`, source slot order |
| Native Dialog | Source `layout` and applicable `menu-visibility.dialog` / `item-action-list` |
| Ore/ArcMenu | Named `ui.layout.areas`, button `ui.placement`, product `ui.products.placement`, bounds and pagination |

`menu-visibility` filters one presentation. `ui.placement.enabled: false` disables graphical placement **only**; it does not hide an Inventory slot. Root `layout` and graphical `ui.layout` are different concepts.

## Graphical elements overlap or disappear <mark style="color:red;">- Premium</mark>

Check the global/menu `ui.layout` canvas and named areas, placement indices, grid capacity, card size and free-coordinate bounds. Explicit menu `ui` values and shop `settings.menu-settings.ui` can override global `menu.ui` defaults. Review [Graphical layout - Premium](ui-layout.md) before adjusting renderer-specific options.

## Ore layout did not update after reload <mark style="color:red;">- Premium</mark>

Server-side YAML reload does not distribute new client assets. Regenerate with `/shop dialogpack`, serve the contents of `pack/` and confirm the client accepted the updated pack. Check optional PNG references under `textures/` and log messages about template validation.

## Search, Favourite or quantity controls behave differently

These menus use native input/detail views in Form/Dialog and a graphical flow in ArcMenu. They do not necessarily reproduce Inventory input slots and status markers one-for-one. Result-slot capacity, configured actions and product permissions still apply. See [Search - Premium](search-menus-premium.md), [Favourite - Premium](favourite-menus-premium.md) and [Buy More](buy-more-menus.md).

## Frequent refresh or title flashing

Check `menu.menu-update.circle-update`, `click-update` and `menu.title-update`. Enable periodic Inventory updates only when item displays actually need them. ArcMenu has its own `ui.renderers.arcmenu.update-interval`.

[⚙️ Configuration](menu-configuration.md) · [🧱 Layout and buttons](layout-and-buttons.md).
