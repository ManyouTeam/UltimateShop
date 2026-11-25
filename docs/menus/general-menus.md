# 🔲Common Menus

Common menus display custom buttons for main hubs, category navigation, or info screens. Save them in `menus/common_menus/` and open them with `/shop menu <id>` or the `open_menu` action.

## Create a Navigation Menu

Save this as `menus/common_menus/main.yml`:

```yaml
title: '&8Main Menu'
size: 9
layout:
  - '000A0X000'
dialog:
  enabled: false
bedrock:
  enabled: false
arcmenu:
  enabled: false
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

Make sure the `farming` shop already exists (otherwise clicking the button will have no shop to open). Save the file, run `/shop reload`, and then run `/shop menu main` to open it.

Because `dialog`, `bedrock`, and `arcmenu` are all set to `false` in this example, it opens as a standard chest Inventory screen (unless global `menu.force-ui` is enabled in `config.yml`).

{% hint style="info" %}
**Opening a shop vs. opening another menu**:
* To open a **shop** (for buying/selling products), use `type: shop_menu` with `shop: <shop-id>`.
* To open **another Common, Search, Favourite, or Item Sell menu**, use `type: open_menu` with `menu: <menu-id>`.

Common menus only show the buttons you define under `buttons`—they never pull in shop products automatically.
{% endhint %}

## Common Menu Keys

| Key | Purpose |
| --- | --- |
| `title` | Screen title |
| `content` | Screen description (fallback for presentation-specific `content`) |
| `size`, `layout` | Inventory size and slot layout |
| `buttons` | Button display items, actions, and conditions |
| `conditions` | Conditions checked before opening the menu |
| `open-actions`, `close-actions` | Actions run on menu open and close |
| `dynamic-layout` | Per-player layout resolution (Premium) |
| `dialog`, `bedrock`, `arcmenu` | Presentation switches and native options |
| `ui` | Ore / ArcMenu style, layout, and rendering settings |

Keep the three condition scopes distinct: `buttons.<id>.conditions` checks clicks, `buttons.<id>.display-conditions` controls visibility, and root `conditions` controls opening the screen. See [Layout & Buttons](layout-and-buttons.md).

## Use Other Presentations - Premium

To use native Dialog instead, replace the `dialog` block in the example with:

```yaml
dialog:
  enabled: true
  layout: multi-action
  content: '&7Choose a category'
  button-width: 150
  columns: 2
```

Also enable global `menu.dialog.enabled` in `config.yml`. For Ore, set `dialog.layout: ore` and configure graphical placement and the resource pack; for ArcMenu, install a compatible integration and enable global/menu `arcmenu.enabled`; for Bedrock Form, enable global/menu `bedrock.enabled`.

See [UI Modes](ui-overview.md) for selection order, forced UIs, and platform support, and [Graphical UI Layout](../menus-getting-started/ui-layout.md) for areas and coordinates.

## Custom Commands - Premium

Add `custom-command` at the menu root to register a command for that menu (avoid colliding with existing commands):

```yaml
custom-command:
  name: categories
  description: Open the category menu
```

<a id="all-buttons-update"></a>
<a id="title-update-premium"></a>
<a id="title-update-premium-require-paper"></a>

## Inventory Refresh & Title Updates

In `config.yml`, `menu.menu-update.circle-update` refreshes item displays every second and `click-update` refreshes after clicks. Premium also supports dynamic title updates (`menu.title-update.enabled` / `circle-update` / `click-update`, requiring PacketEvents). See [Inventory UI](inventory-ui.md#clicks-visibility-and-refresh).

## Declaring Special Menu Types in `common_menus`

Although Search, Favourite, and Item Sell menus should live in their dedicated folders (auto-detected by folder), legacy files in `menus/common_menus/` can declare `menu-type: search`, `favourite` (alias `favorite`), or `item-sell` (alias `item_sell`) at the root (or be listed in the legacy global Search/Favourite lists).

Regardless of folder, available controls and UI support follow the resolved menu type.

[Shop Menus](shop-menus.md) · [Menu Configuration & Inheritance](menu-configuration.md)
