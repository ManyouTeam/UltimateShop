# 🧱 Layout & Buttons

Root `layout` assigns entry IDs to Inventory slots. Ordinary entries are defined under `buttons`; a Shop menu can also resolve products and buttons from its shop file. Ore and ArcMenu use separate [graphical placements](../menus-getting-started/ui-layout.md).

## A complete button menu

Save as `menus/common_menus/categories.yml`:

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

Create the `farming` shop, reload with `/shop reload`, then open `/shop menu categories`. With Inventory selected, `A` occupies slot 12 and `X` occupies slot 14, counting slots from zero.

## Slot IDs

Each character is one slot. Read left to right, then top to bottom. Use nine slots per row and match `size` to the total: `9`, `18`, `27`, `36`, `45` or `54`.

- IDs are case-sensitive: `A` and `a` differ.
- Repeating an ID repeats the same entry in multiple slots.
- An undefined ID leaves an empty slot.
- `0` is an ordinary ID. Defining `buttons.'0'` fills every matching slot.

For example, this fragment fills nine slots:

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

Quote numeric-looking IDs in YAML. Decorative Inventory fillers can be hidden from list and graphical presentations as above.

## Longer IDs

Enclose a multi-character ID in backticks inside `layout`. Each enclosed token occupies **one** slot:

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

The nine slots are `0`, `food_buy`, `food_sell`, followed by six `0` slots. The backticks are layout delimiters and are not part of the button keys. Add actions to these display-only example entries to make them interactive.

## Entry sources in Shop menus

A template's layout can reference:

| Source | Location |
| --- | --- |
| Product | Shop `items.<id>` |
| Shop-specific ordinary button | Shop `buttons.<id>` |
| Reusable ordinary button | Template `buttons.<id>` |

Use distinct IDs for each source. If IDs collide, copied shop entries are checked first where available, followed by shop buttons, shop products and template buttons. The first eligible entry wins.

Ordinary buttons run their `actions`. Product entries use product trade operations and click bindings such as `buy`, `sell` and `select-amount`. Defining a navigation button with the same display material as a product does not turn it into a trade entry.

## Button actions and conditions

| Button key | Purpose |
| --- | --- |
| `display-item` | Item appearance; see [Display Item Format](../format/display-item-format.md) |
| `actions` | Actions when click conditions pass |
| `conditions` | Conditions checked when clicking |
| `fail-actions` | Actions when click conditions fail |
| `display-conditions` | Eligibility to appear — Premium |
| `menu-visibility` | Visibility by presentation |
| `dialog.layout`, `dialog.show-sprite` | Native Dialog entry appearance |
| `ui` | Ore/ArcMenu appearance and placement |

This fragment creates a visible button that checks permission on click:

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

The `vip` shop must exist. `conditions` does not hide the button; use `display-conditions` when it should disappear. Menu-root `conditions` controls opening the entire screen. Product buy/sell conditions still validate the transaction separately.

## Visibility by presentation

Place this at button or product root:

```yaml
menu-visibility:
  inventory: true
  form: true
  dialog: true
  item-action-list: false
  ore: false
  arcmenu: true
```

Unspecified values default to visible. `ore` and `item-action-list` fall back to `menu-visibility.dialog` when not explicitly set. For example, `dialog: false` hides all three unless an individual mode is explicitly enabled.

Visibility filters an existing entry; it does not create a slot, graphical placement or click action. It can be used independently of `dynamic-layout`.

## Player-dependent layouts — Premium

Enable `dynamic-layout: true` to evaluate layout placeholders and display conditions for each player. A backtick token can contain ordered alternatives separated by `||`:

```yaml
dynamic-layout: true
layout:
  - '000`A||B`00000'
```

This row has nine slots. The runtime checks `A`, then `B`, selecting the first existing entry allowed in the presentation whose display conditions pass. If neither qualifies, the slot is empty. Put fallback entries last. See [Conditional Display](conditional-display-premium.md) for a complete example.

## Entry placement by presentation

| Presentation | Placement |
| --- | --- |
| Inventory | Root layout slot |
| Form | Entry list; empty Inventory spacing is omitted |
| Native Dialog | Action/list entries sourced from the root layout |
| Ore / ArcMenu | Named graphical areas and entry placements |

An ordinary graphical button needs an explicit placement:

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

This uses the selected style's `sidebar` area. An explicitly placed ordinary button can appear graphically even when absent from root `layout`. Products can inherit `ui.products.placement`; product `ui.placement` overrides that default. No graphical Back/Close/category controls are generated from empty areas.

[Common Menus](general-menus.md) · [Shop Menus](shop-menus.md) · [Graphical UI Layout](../menus-getting-started/ui-layout.md)
