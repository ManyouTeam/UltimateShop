# 🧱 Layout & Buttons

A menu's root `layout` identifies entry slots. Its `buttons` section defines ordinary navigation/action buttons; shop menus can also place product IDs and shop-specific buttons. This page covers the shared Inventory-style layout, not the pixel/area coordinates used by Ore and ArcMenu.

## Example: two buttons and an Inventory layout

```yaml
title: '&8Categories'
size: 18
layout:
  - '000000000'
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

Save as `menus/common_menus/categories.yml`, reload and open with `/shop menu categories`. Each row has nine slots. Supported Inventory sizes are 9, 18, 27, 36, 45 and 54.

## Slot IDs and layout markers

- One ordinary character is one slot, read left to right and then top to bottom.
- `A` and `X` above refer to `buttons.A` and `buttons.X`. An undefined ID leaves a gap.
- `0` is not inherently a filler. Define `buttons.0` if you want one.
- IDs are case-sensitive; one logical marker occupies one slot even when the ID has multiple characters.
- Special menus reuse markers for result areas or input/confirmation slots; read their own pages.

Multi-character IDs are enclosed in backticks **inside the YAML string**:

```yaml
layout:
  - '000000000'
  - '0`food_buy``food_sell`000000'
```

The second row still represents nine slots. Corresponding `buttons.food_buy` and `buttons.food_sell` entries, or matching shop entries, must exist.

## Dynamic layout <mark style="color:red;">- Premium</mark>

Evaluate placeholders in layout markers and use ordered alternatives:

```yaml
dynamic-layout: true
layout:
  - '000000000'
  - '000`A||B`00000'
```

The first available visible candidate wins. The `dynamic-layout` option is important for player-dependent display conditions. See [Conditional Display - Premium](conditional-display-premium.md).

## Button fields

| Key within `buttons.<id>` | Use |
| --- | --- |
| `display-item` | Appearance and item metadata; see [Display Item Format](../format/display-item-format.md) |
| `actions`, `fail-actions` | Numbered actions for accepted/failed clicks |
| `conditions` | Requirements evaluated when clicked |
| `menu-visibility` | Restrict entry to particular UI presentations |
| `ui.placement` | Ore/ArcMenu graphical placement |
| `ui.appearance`, `ui.font-size`, `ui.text-align` | Graphical button/card styling |
| `dialog.show-sprite` | Native Dialog sprite preference |
| `dialog.layout` | Per-button Dialog presentation override |

Menu-level `conditions` control opening the menu. `open-actions` and `close-actions` execute when entering and leaving it. These are not the same as the click conditions/actions of one button.

Use the in-game Editor's `menu-visibility` section for presentation-specific switches, or configure its boolean keys directly in YAML. The Editor keeps `dialog.show-sprite` and button `dialog.layout` outside the graphical `ui` section.

### Display conditions <mark style="color:red;">- Premium</mark>

Use `buttons.<id>.display-conditions` to control whether a button appears. These conditions are separate from click conditions. See [Conditional Display - Premium](conditional-display-premium.md).

### Graphical button placement and Dialog options <mark style="color:red;">- Premium</mark>

`ui.placement`, `ui.appearance`, `ui.font-size` and `ui.text-align` configure Ore/ArcMenu graphics. `dialog.show-sprite` and `dialog.layout` configure native Dialog buttons. See [Graphical UI Layout - Premium](ui-layout.md) and [Native Dialog - Premium](dialog-menus-premium.md).

## Visibility is not graphical placement

```yaml
buttons:
  A:
    display-item:
      material: EMERALD
      name: '&aContinue'
    menu-visibility:
      inventory: true
      form: true
      dialog: true
      item-action-list: true
      ore: true
      arcmenu: true
    ui:
      placement:
        area: sidebar
        order: 0
```

`menu-visibility` (boolean per presentation) is evaluated independently of `dynamic-layout`; `ui.placement.enabled: false` removes the graphical placement but does not, by itself, hide an Inventory or Form entry. `display-item.bedrock.hide` is a Form-specific display rule. A button without a display name can be omitted in Form mode. See [Bedrock Form - Premium](bedrock-menus-premium.md).

Root `layout` still identifies source entries and special result/input areas for non-Inventory presentations, but graphical positions use `ui.layout` and `ui.placement`. Read [Graphical Layout - Premium](ui-layout.md).

## Related pages

[Common Menus](general-menus.md) · [Shop Menus](shop-menus.md) · [Action Format](../format/action-format.md) · [Condition Format](../format/condition-format.md) · [Display Item Add Lore](display-item-add-lore.md)
