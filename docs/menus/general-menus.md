# 🔲 Common menus

A Common menu is a navigation/action screen. It displays **buttons you define**; it does not automatically load a shop's products.

## Create one

Save as `plugins/UltimateShop/menus/common_menus/main.yml`:

```yaml
title: '&8Main Menu'
size: 9
layout:
  - '000A0X000'
buttons:
  A:
    display-item:
      material: WHEAT
      name: '&aFarming'
    actions:
      1:
        type: shop_menu
        shop: farming
  X:
    display-item:
      material: BARRIER
      name: '&cClose'
    actions:
      1:
        type: close
```

`A` is placed at the fourth slot and `X` at the sixth. The `farming` shop must exist; alternatively use `type: open_menu` and `menu: <menu-id>` to open another Common menu. Reload with `/shop reload`, then use `/shop menu main`.

## Common menu keys

| Key | Purpose |
| --- | --- |
| `title` | Screen title |
| `size`, `layout` | Inventory capacity and slot markers |
| `buttons` | Actions and display item for each marker |
| `conditions` | Conditions required to open the whole menu |
| `open-actions`, `close-actions` | Menu lifecycle actions |
| `dynamic-layout` | Conditional/ordered layout alternatives where supported |
| `dialog`, `bedrock`, `arcmenu` | Client-specific presentation switches and content |
| `ui` | Ore/ArcMenu graphical drawing and entry positioning |

Keep `conditions` for menu entry separate from `buttons.A.conditions` for individual click actions.

## Custom menu command <mark style="color:red;">- Premium</mark>

Use `custom-command.name` and `custom-command.description` to register a command for this menu.

## Dynamic layout <mark style="color:red;">- Premium</mark>

Use `dynamic-layout` for player-dependent layout markers and ordered alternatives. See [Conditional Display - Premium](conditional-display-premium.md).

## Use another presentation <mark style="color:red;">- Premium</mark>

For a native Dialog, add to the **menu root**:

```yaml
dialog:
  enabled: true
  layout: multi-action
  content: '&7Choose a category'
  button-width: 150
  columns: 2
```

Also enable `menu.dialog.enabled` in `config.yml`. To use graphical Ore, set `dialog.layout: ore` **and** configure `ui.layout` and `buttons.<id>.ui.placement`. To use ArcMenu, enable global and menu `arcmenu.enabled`, install its integration and set graphical placements. For Bedrock Form, use menu `bedrock.enabled/content` and global `menu.bedrock.enabled`.

Learn presentation priority in [🎨 UI modes](ui-overview.md) and graphical coordinates in [📐 Layout - Premium](ui-layout.md).

## Alternative Common-folder menu types

The dedicated `search_menus`, `favourite_menus` and `item_sell_menus` folders choose those menu types automatically. Files placed in `common_menus` can also opt in with root `menu-type: search`, `favourite` / `favorite`, or `item-sell` / `item_sell`. Search/Favourite classification may also follow the global menu lists. Prefer the dedicated folder for a new menu, and do not reuse an ID across types.

## Troubleshooting

If a button is missing, verify `layout` contains its ID, that `buttons.<id>` exists, that conditions pass and that `menu-visibility` allows the active presentation. For Ore/ArcMenu, ensure the button has a graphical `ui.placement`; inventory coordinates do not create graphical positions.

[🧱 Buttons and slot markers](layout-and-buttons.md) · [🛍️ Shop menus](shop-menus.md) · [🩺 Troubleshooting](troubleshooting.md).
