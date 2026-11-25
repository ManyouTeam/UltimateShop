# 🧱Layout & Buttons

Root `layout` maps entry IDs to Inventory slots: common buttons are defined under `buttons`, while shop menus can also reference shop products and shop buttons. Ore and ArcMenu use separate [graphical placement settings](../menus-getting-started/ui-layout.md).

## Complete Button Menu Example

Save this as `menus/common_menus/categories.yml`:

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

Create the `farming` shop, run `/shop reload`, and open `/shop menu categories`. In Inventory UI, slots are zero-indexed, placing `A` in slot 12 and `X` in slot 14.

## Slot IDs

`layout` is read left-to-right, top-to-bottom. Each row has 9 slots, and `size` must match the total slot count (`9`, `18`, `27`, `36`, `45`, or `54`).

* **Case-sensitive**: `A` and `a` are distinct IDs.
* **Repeatable**: repeating an ID places the same entry in multiple slots.
* **Empty slots**: undefined IDs stay empty.
* **`0` is a normal ID**: defining `buttons.'0'` fills every `0` slot (commonly used for filler glass panes).

```yaml
layout:
  - '000000000'
buttons:
  '0':
    display-item:
      material: GRAY_STAINED_GLASS_PANE
      name: ' '
    menu-visibility:
      form: false
      dialog: false
      ore: false
      arcmenu: false
```

{% hint style="tip" %}
Quote numeric YAML keys such as `'0'`. The snippet above also shows how to keep filler panes in Inventory while hiding them in Form, Dialog, and graphical UIs.
{% endhint %}

## Multi-Character IDs

Wrap multi-character IDs in backticks (`` ` ``) inside `layout`. Each backtick token occupies **one** slot:

```yaml
layout:
  - '0`food_buy``food_sell`000000'
buttons:
  food_buy:
    display-item:
      material: EMERALD
      name: '&aBuy food'
  food_sell:
    display-item:
      material: GOLD_INGOT
      name: '&eSell food'
```

This row defines 9 slots: `0`, `food_buy`, `food_sell`, and six `0` slots. Backticks are layout delimiters only—do not include them in `buttons` keys.

## Shop Menu Entry Sources

A shop menu template's `layout` can reference three sources:

| Source | Location |
| --- | --- |
| Products | Shop `items.<id>` |
| Shop-specific common buttons | Shop `buttons.<id>` |
| Reusable common buttons | Template `buttons.<id>` |

Use distinct IDs across sources. If an ID collides, the plugin picks the first eligible match in this priority order: **copied shop entries → shop `buttons` → shop `items` → template `buttons`**.

Common buttons run their own `actions`, whereas products use shop trade logic and click bindings (`buy`, `sell`, `select-amount`, etc.). A navigation button never becomes a tradable product just by sharing a material.

## Button Actions & Conditions

| Key | Purpose |
| --- | --- |
| `display-item` | Button appearance; see [Display Item Format](../format/display-item-format.md) |
| `actions` | Actions run when click `conditions` pass |
| `conditions` | Conditions checked on click |
| `fail-actions` | Actions run when click `conditions` fail |
| `display-conditions` | Controls whether the button is shown (Premium) |
| `menu-visibility` | Per-presentation visibility |
| `dialog.layout`, `dialog.show-sprite` | Native Dialog appearance for this entry |
| `ui` | Ore / ArcMenu appearance and placement |

The example below keeps the button visible but checks permission on click:

```yaml
buttons:
  A:
    display-item:
      material: DIAMOND
      name: '&bVIP Shop'
    conditions:
      1:
        type: permission
        permission: shop.vip
    actions:
      1:
        type: shop_menu
        shop: vip
    fail-actions:
      1:
        type: message
        message: '&cYou do not have permission!'
```

`conditions` blocks clicks without hiding the button; use `display-conditions` to hide entries. Root menu `conditions` control opening the whole screen, while product buy/sell conditions validate trades separately.

## Per-Presentation Visibility

Add `menu-visibility` at a button or product root:

```yaml
menu-visibility:
  inventory: true
  form: true
  dialog: true
  item-action-list: false
  ore: false
  arcmenu: true
```

Unspecified keys default to `true`. If `ore` or `item-action-list` is omitted, it falls back to `menu-visibility.dialog` (so `dialog: false` hides all three unless overridden).

`menu-visibility` only filters existing entries—it never creates slots, graphical placements, or click bindings, and works independently of `dynamic-layout`.

## Per-Player Dynamic Layout - Premium

When `dynamic-layout: true` is enabled, layout placeholders and `display-conditions` are evaluated per player. Use `||` inside a backtick token to list fallback candidates in priority order:

```yaml
dynamic-layout: true
layout:
  - '000`A||B`00000'
```

This row still has 9 slots. At runtime, the plugin checks `A` then `B`, picking the first entry that exists, is visible in the current presentation, and passes `display-conditions` (or leaves the slot empty if none pass). Put your fallback entry last; see [Conditional Display](conditional-display-premium.md).

## Entry Placement Across Presentations

| Presentation | Placement Source |
| --- | --- |
| Inventory | Root `layout` slots |
| Bedrock Form | Ordered list of entries (empty Inventory spacers ignored) |
| Native Dialog | Action/list entries read in root `layout` order |
| Ore / ArcMenu | Named graphical areas and `ui.placement` |

Common graphical buttons must set `ui.placement` explicitly (and will appear in graphical UIs even if omitted from root `layout`):

```yaml
buttons:
  A:
    display-item:
      material: WHEAT
      name: '&aFarming'
    actions:
      1:
        type: shop_menu
        shop: farming
    ui:
      appearance: button
      placement:
        area: sidebar
        order: 0
```

Products inherit `ui.products.placement` and merge their own `ui.placement` overrides. Empty areas never auto-generate Back, Close, or category buttons.

[Common Menus](general-menus.md) · [Shop Menus](shop-menus.md) · [Graphical UI Layout](../menus-getting-started/ui-layout.md)
