# 🧱 Layout & Buttons

## Basic Layout

UltimateShop uses `layout` to define menu slots and `buttons` to configure regular menu buttons. Shop menus can also reference shop products and shop-specific buttons directly in their layouts.

This guide covers Inventory slot layouts, button configuration, and display rules. For graphical positioning in Ore and ArcMenu, see [Graphical UI Layout](../menus-getting-started/ui-layout.md).

### Basic Example

A common menu typically consists of three main sections: `size`, `layout`, and `buttons`.

```yaml
title: '&8Main Menu'
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

Save this configuration as `menus/common_menus/main.yml`. After reloading the plugin, open the menu using `/shop menu main`.

### Slot Layout

The example above creates an Inventory menu with 18 slots arranged in two rows.

**First row:**

```
0 0 0 0 0 0 0 0 0
```

**Second row:**

```
0 0 0 A 0 X 0 0 0
```

Here, `A` and `X` reference `buttons.A` and `buttons.X`. The remaining positions are empty unless matching entries are defined.

| Option    | Description                                          |
| --------- | ---------------------------------------------------- |
| `title`   | Menu title                                           |
| `size`    | Number of Inventory slots                            |
| `layout`  | Defines which entry ID occupies each slot            |
| `buttons` | Defines regular button appearances and click actions |

Supported Inventory sizes are `9`, `18`, `27`, `36`, `45`, and `54`, with nine slots per row.

## Layout Characters and IDs

The `layout` section uses strings to represent slots. Entries are read from left to right, then from top to bottom.

### Single-Character IDs

By default, each character represents one slot.

For example:

```yaml
layout:
  - 'A0B0C0D0E'
```

This layout contains nine slots. `A`, `B`, `C`, `D`, and `E` reference their corresponding buttons or products.

Keep the following rules in mind:

* IDs are case-sensitive. `A` and `a` are different IDs.
* The same ID can appear in multiple slots.
* An ID without a matching entry does not create a button.
* `0` is a regular ID, not a reserved empty-slot marker.

For example:

```yaml
layout:
  - '000000000'

buttons:
  '0':
    display-item:
      material: GRAY_STAINED_GLASS_PANE
      name: ' '
```

Every slot marked with `0` will display a gray stained glass pane.

If no matching `0` entry exists, those slots normally remain empty.

### Multi-Character IDs

To use an ID containing multiple characters, enclose it in backticks within the layout string. The entire ID will occupy a single slot.

For example:

```yaml
layout:
  - '0`food_buy``food_sell`000000'
```

This produces the following nine slots:

```
0 | food_buy | food_sell | 0 | 0 | 0 | 0 | 0 | 0
```

The corresponding buttons can use their full IDs:

```
buttons:
  food_buy:
    display-item:
      material: EMERALD
      name: '&aBuy Food'

  food_sell:
    display-item:
      material: GOLD_INGOT
      name: '&eSell Food'
```

Backticks are only used to identify multi-character IDs inside `layout`. They are not part of the IDs themselves.

Each layout row should resolve to nine slots to avoid shifting subsequent entries into unexpected positions.

## Regular Buttons and Shop Products

A layout can reference different types of entries depending on the menu type.

### Common Menu Buttons

Common menus define their buttons directly under the `buttons` section of the menu file.

For example:

```yaml
layout:
  - '0000A0000'

buttons:
  A:
    display-item:
      material: CHEST
      name: '&aOpen Shop'

    actions:
      1:
        type: shop_menu
        shop: farming
```

Here, `A` references `buttons.A`.

Buttons can open other menus, open shops, close menus, or perform other supported actions.

### Shop Menu Entries

Shop menus can display both products and regular buttons.

For example, in `menus/shop_menu_templates/default.yml`:

```yaml
title: '{shop-name}'
size: 9

layout:
  - '0000A0X00'

buttons:
  X:
    display-item:
      material: ARROW
      name: '&eBack'

    actions:
      1:
        type: back
```

In `shops/farming.yml`:

```yaml
settings:
  menu: default

items:
  A:
    display-item:
      material: WHEAT
      name: '&eWheat'

    products:
      1:
        material: WHEAT

    buy-prices:
      1:
        economy-plugin: Vault
        amount: 5
```

The resulting layout contains:

| ID  | Source                                                | Purpose                    |
| --- | ----------------------------------------------------- | -------------------------- |
| `A` | `shops/farming.yml` → `items.A`                       | Displays the wheat product |
| `X` | `menus/shop_menu_templates/default.yml` → `buttons.X` | Displays the back button   |

Shop files can also define shop-specific regular buttons under their own `buttons` section.

Therefore, a shop layout can reference shop products, shop-specific buttons, and buttons defined in the menu template.

Avoid using the same ID for different entry types. When IDs conflict, UltimateShop selects an entry according to its internal lookup priority rather than displaying multiple entries in the same slot.

## Button Configuration

Each regular button can define its display item, click actions, conditions, and presentation-specific options.

### Available Options

| Option               | Description                                                      |
| -------------------- | ---------------------------------------------------------------- |
| `display-item`       | Display item, name, and other item properties                    |
| `actions`            | Actions executed when the button is clicked                      |
| `fail-actions`       | Actions executed when click conditions are not met               |
| `conditions`         | Conditions required to execute click actions                     |
| `display-conditions` | Conditions required for the button to appear (Premium)           |
| `menu-visibility`    | Controls visibility in different menu presentations              |
| `dialog.layout`      | Overrides the Dialog presentation style for an individual button |
| `dialog.show-sprite` | Controls whether a Dialog item sprite is displayed               |
| `ui`                 | Graphical appearance and placement for Ore and ArcMenu           |

### Click Actions and Conditions

Use `actions` to define what happens when a player clicks a button.

For example:

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

When a player clicks the button, UltimateShop checks `conditions` first:

* If the conditions are met, `actions` are executed.
* If the conditions are not met, `fail-actions` are executed.

The `conditions` section controls click behavior. It does not automatically hide the button.

### Display Conditions <mark style="color:red;">- Premium</mark>

Use `display-conditions` when a button should only appear if certain requirements are met.

For example:

```yaml
buttons:
  A:
    display-item:
      material: DIAMOND
      name: '&bVIP Shop'

    display-conditions:
      1:
        type: permission
        permission: shop.vip
```

This button is only eligible to appear when the player has the `shop.vip` permission.

`display-conditions` and `conditions` serve different purposes:

| Option               | Purpose                                                                  |
| -------------------- | ------------------------------------------------------------------------ |
| `display-conditions` | Determines whether the button appears                                    |
| `conditions`         | Determines whether clicking the button successfully executes its actions |

If display conditions depend on permissions, placeholders, or other player-specific states, enable `dynamic-layout` so the layout can be evaluated for each player.

### Dynamic Layout <mark style="color:red;">- Premium</mark>

Dynamic layouts allow a slot to display different entries depending on the player's state.

For example:

```yaml
dynamic-layout: true

layout:
  - '000000000'
  - '000`A||B`00000'

buttons:
  A:
    display-item:
      material: DIAMOND
      name: '&bVIP Shop'

    display-conditions:
      1:
        type: permission
        permission: shop.vip

    actions:
      1:
        type: shop_menu
        shop: vip

  B:
    display-item:
      material: EMERALD
      name: '&aNormal Shop'

    actions:
      1:
        type: shop_menu
        shop: normal
```

The backtick-enclosed expression `A||B` defines multiple candidates for a single slot.

UltimateShop evaluates the candidates from left to right:

1. If `A` exists and satisfies its display conditions, display `A`.
2. Otherwise, check `B`.
3. If `B` satisfies its display conditions, display `B`.
4. If no candidate is available, leave the slot empty.

As a result, players with the `shop.vip` permission see VIP Shop, while other players see Normal Shop.

Multiple candidate IDs can be separated using `||`. The entire expression still occupies only one slot.

When `dynamic-layout` is enabled, layout IDs can also contain PlaceholderAPI placeholders, allowing entries to be resolved dynamically for each player.

## Buttons in Different Menu Presentations

The same button can be used in Inventory, Bedrock Form, Dialog, Ore, and ArcMenu. However, these presentations do not use identical layout systems.

### Presentation Visibility

Use `menu-visibility` to control which presentations can display a button.

For example:

```yaml
buttons:
  A:
    display-item:
      material: EMERALD
      name: '&aExample'

    menu-visibility:
      inventory: true
      form: true
      dialog: true
      item-action-list: false
      ore: false
      arcmenu: true
```

| Option             | Presentation            |
| ------------------ | ----------------------- |
| `inventory`        | Inventory               |
| `form`             | Bedrock Form            |
| `dialog`           | Native Dialog           |
| `item-action-list` | Dialog Item Action List |
| `ore`              | Ore                     |
| `arcmenu`          | ArcMenu                 |

Presentations are visible by default when their visibility options are not specified.

If `ore` or `item-action-list` is not explicitly configured, it uses `menu-visibility.dialog` as its fallback.

### Layout Behavior by Presentation

| Presentation | Layout Behavior                                               |
| ------------ | ------------------------------------------------------------- |
| Inventory    | Uses the root `layout` to determine slot positions            |
| Bedrock Form | Builds a list from menu entries                               |
| Dialog       | Builds action buttons or item lists from menu entries         |
| Ore          | Uses `ui.layout` and `ui.placement` for graphical positioning |
| ArcMenu      | Uses `ui.layout` and `ui.placement` for graphical positioning |

Inventory slot positions are not automatically converted into Ore or ArcMenu graphical coordinates.

For example:

```yaml
buttons:
  A:
    display-item:
      material: EMERALD
      name: '&aOpen Shop'

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

In Ore and ArcMenu, `ui.placement` determines the graphical area and ordering of the button.

A regular button with an explicit `ui.placement` can also become a graphical menu entry even if its ID does not appear in the root `layout`.

Products can inherit their default graphical placement from the menu's `ui.products.placement`.

`menu-visibility` determines whether an entry is allowed to appear in a presentation, while `ui.placement` determines where it appears in graphical interfaces. These settings serve different purposes.

For canvas dimensions, named areas, grids, and graphical coordinates, see [Graphical UI Layout](../menus-getting-started/ui-layout.md).
