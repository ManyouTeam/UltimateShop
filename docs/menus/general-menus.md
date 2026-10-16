# 🔲 Common Menus

Common Menus are standalone navigation/action menus. They do not automatically list shop products. To display a shop's products, use a [Shop Menu](shop-menus.md).

## Requirements and location

Save the file under `plugins/UltimateShop/menus/common_menus/<id>.yml`. The filename (without `.yml`) is its ID. Open it using `/shop menu <id>` or an `open_menu` action. `/shop menu` requires `ultimateshop.menu`. Console can specify a target player; Premium administrators can use the command's `-b` option to bypass menu opening conditions.

## Quick start

Save as `menus/common_menus/main.yml`:

```yaml
title: '&8Main Menu'
size: 9
layout:
  - '0000A0000'
buttons:
  A:
    display-item:
      material: BOOK
      name: '&aOpen Farming'
    actions:
      1:
        type: shop_menu
        shop: farming
```

Run `/shop reload` and `/shop menu main`. `0` creates a gap unless you define `buttons.0`. The `shop_menu` action opens a shop; `open_menu` opens a standalone menu ID.

## Configuration reference

| Root key | Description |
| --- | --- |
| `title` | Inventory title; can contain language/placeholder text |
| `content` | Descriptive content for Form, Dialog and graphical headers |
| `size` | Inventory capacity (multiple of 9, up to 54) |
| `layout` | Ordered rows containing button markers |
| `buttons` | Definitions of ordinary buttons |
| `conditions` | Requirements to open |
| `open-actions`, `close-actions` | Menu lifecycle actions |
| `dynamic-layout` | Resolve per-player layout/conditional alternatives (Premium) |
| `custom-command.name`, `custom-command.description` | Optional registered Common Menu command (Premium) |
| `ui` | UI mode and renderer overrides |

For complete field types and scope, see [Configuration](menu-configuration.md) and [Layout and Buttons](layout-and-buttons.md).

## Custom command

Premium Common Menus can register a command using:

```yaml
custom-command:
  name: categories
  description: 'Open the category menu'
```

This belongs at the Common Menu root, not under `buttons` or `ui`.

## Special menu types in the common folder

Files in `common_menus` can explicitly declare `menu-type: search`, `menu-type: favourite` (or `favorite`), or `menu-type: item-sell` (or `item_sell`). `menu.search-gui.menu` and `menu.favourite-gui.menu` lists can also classify Common-folder files. Prefer the dedicated folders for new configurations. Shop templates and Buy More menus remain separate because they require shop/product context.

## Presentation and limitations

Common Menus use Inventory by default for Java unless changed globally or locally. Supported presentations and fallback rules are described in [UI Modes](ui-overview.md). Ore and ArcMenu require entry placements; they do not infer pixel positions from Inventory gaps. Refer to [Graphical Layout](ui-layout.md).

For display visibility, custom actions, or menu conditions, see [Conditional Display](conditional-display-premium.md), [Action Format](../format/action-format.md) and [Condition Format](../format/condition-format.md).

<a id="menu-types-and-files"></a>
<a id="choose-the-presentation"></a>
<a id="create-a-common-menu"></a>
<a id="button-configuration"></a>
<a id="shop-templates-and-inline-overrides"></a>
<a id="button-visibility-by-menu-presentation"></a>
<a id="global-menu-controls-in-config-yml"></a>
<a id="editing-and-troubleshooting"></a>

## Troubleshooting

If nothing appears, confirm the file is in the correct folder, the menu ID matches the command, the entry ID exists in `buttons` and the `layout` uses it. Check opening conditions, `ui.visible-in` and any Form or graphical placement restrictions. See [Troubleshooting](troubleshooting.md).

<a id="common-menu-configuration"></a>
<a id="layout-option"></a>
<a id="all-buttons-update"></a>
<a id="title-update-premium"></a>
<a id="title-update-premium-require-paper"></a>

Configuration, layout, refresh and title-update details are documented in [Configuration](menu-configuration.md) and [Layout and Buttons](layout-and-buttons.md).
