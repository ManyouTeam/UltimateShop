# ⚙️ Menu Configuration & Inheritance

UltimateShop allows each menu to have its own configuration while also supporting shared menu templates across multiple shops.

Global settings provide common defaults, while individual menus and shops can override specific options without duplicating the entire configuration.

This guide explains how menu configurations are organized, inherited, and prioritized.

## Configuration Structure

Menu configurations are primarily stored in three locations:

```
plugins/UltimateShop/
│
├── config.yml
│   └── menu                 # Global menu settings
│
├── menus/
│   ├── common_menus/        # Common menus
│   ├── shop_menu_templates/ # Shop menu templates
│   ├── buy_more_menus/      # Amount selection menus
│   ├── search_menus/        # Search menus
│   ├── favourite_menus/     # Favourite menus
│   └── item_sell_menus/     # Item sell menus
│
└── shops/
    ├── farming.yml         # Shop configuration
    └── mining.yml
```

### Global Configuration (`config.yml`)

The `menu` section in `config.yml` controls global menu features and default settings.

For example:

```yaml
menu:
  dialog:
    enabled: true

  bedrock:
    enabled: true

  arcmenu:
    enabled: false

  ui:
    theme:
      accent: '#65E8EF'
```

| Section        | Description                                                                       |
| -------------- | --------------------------------------------------------------------------------- |
| `menu.dialog`  | Global settings for Dialog and Ore                                                |
| `menu.bedrock` | Global settings for Bedrock Form                                                  |
| `menu.arcmenu` | Global settings for ArcMenu                                                       |
| `menu.ui`      | Default graphical layout and appearance for Ore and ArcMenu                       |
| Other `menu.*` | Global features such as menu updates, click actions, search, and amount selection |

### Menu Configuration (`menus/`)

Each YAML file in the `menus` directory defines a menu, including its title, layout, buttons, and presentation settings.

For example, `menus/common_menus/main.yml`:

```yaml
title: '&8Main Menu'
size: 27

layout:
  - '000000000'
  - '0000A0000'
  - '000000000'

dialog:
  enabled: true
  layout: multi-action

buttons:
  A:
    display-item:
      material: EMERALD
      name: '&aOpen Shop'

    actions:
      1:
        type: shop_menu
        shop: farming
```

Common menus use their own configuration files directly.

Shop menus can reference a shared template through `settings.menu`, allowing multiple shops to reuse the same menu structure.

### Shop Configuration (`shops/`)

Shop files define products and trading rules. They can also select a menu template or override its configuration.

For example, `shops/farming.yml`:

```yaml
settings:
  menu: default

  menu-settings:
    title: '&aFarming Shop'

    dialog:
      layout: ore
```

* `settings.menu`: Specifies the menu template to use.
* `settings.menu-settings`: Overrides menu settings for this particular shop.

Multiple shops can share the same template while using different titles, layouts, or presentation settings through `menu-settings`.

### How Configurations Work Together

**Common Menu:**

```
config.yml
  └── Global presentation settings
                ↓
menus/common_menus/main.yml
  └── Individual menu settings
```

**Shop Menu:**

```
menus/shop_menu_templates/default.yml
                  │
                  ▼
          Load menu template
                  │
                  ▼
shops/farming.yml
  └── settings.menu-settings
                  │
                  ▼
       Override template settings
                  │
                  ▼
config.yml → menu.ui
  └── Fill missing graphical settings
                  │
                  ▼
         Final menu configuration
```

Only graphical `ui` settings use the recursive default-filling mechanism shown above. Dialog, Bedrock, and other global settings are resolved through their respective configuration lookup methods.

## Configuration Inheritance & Priority

UltimateShop uses different inheritance rules depending on the configuration type.

### Shop Menu Template Overrides

The `settings.menu-settings` section allows a shop to override specific settings from its menu template.

Suppose `menus/shop_menu_templates/default.yml` contains:

```
title: '&8Default Shop'

size: 27

layout:
  - '000000000'
  - '0000A0000'
  - '000000000'

dialog:
  enabled: true
  layout: multi-action
  columns: 2

ui:
  products:
    card:
      icon-size: 32
      padding: 9
```

In `shops/farming.yml`:

```
settings:
  menu: default

  menu-settings:
    title: '&aFarming Shop'

    dialog:
      layout: ore

    ui:
      products:
        card:
          icon-size: 48
```

The resulting configuration is equivalent to:

```
title: '&aFarming Shop'

size: 27

layout:
  - '000000000'
  - '0000A0000'
  - '000000000'

dialog:
  enabled: true
  layout: ore
  columns: 2

ui:
  products:
    card:
      icon-size: 48
      padding: 9
```

**Merge Rules:**

| Situation                                   | Result                            |
| ------------------------------------------- | --------------------------------- |
| An option is defined in the shop override   | The shop's value takes priority   |
| An option is missing from the shop override | The template's value is preserved |
| An option contains nested sections          | Sections are merged recursively   |
| An option contains a list                   | The entire list is replaced       |

You only need to include the settings you want to change in `settings.menu-settings`.

If a shop does not specify `settings.menu` but provides `settings.menu-settings`, UltimateShop can also create its shop menu directly from those settings.

### Graphical UI Inheritance

Ore and ArcMenu use the `ui` section for graphical settings.

Global configuration:

```
menu:
  ui:
    products:
      card:
        icon-size: 32
        padding: 9

    theme:
      background: '#141E30'
      accent: '#65E8EF'
```

Individual menu configuration:

```
ui:
  products:
    card:
      icon-size: 48

  theme:
    accent: '#FFAA00'
```

The resulting graphical configuration is:

```
ui:
  products:
    card:
      icon-size: 48
      padding: 9

  theme:
    background: '#141E30'
    accent: '#FFAA00'
```

**Inheritance Rules:**

* Values explicitly defined in the menu take priority.
* Missing values are inherited from global `menu.ui`.
* Nested sections are filled recursively without overwriting existing values.
* Explicit `false` values, empty strings, and empty lists are preserved.

For shop menus, the processing order is:

**Menu Template → Shop Overrides → Global Graphical Defaults**

### Other Presentation Settings

Native settings for Dialog, Bedrock Form, and ArcMenu generally follow this lookup order:

1. Current menu configuration.
2. Global settings in `config.yml`.
3. Built-in fallback values.

For example:

```
# config.yml
menu:
  dialog:
    not-auto-close: true
```

An individual menu can override this setting:

```
dialog:
  not-auto-close: false
```

In this case, the menu uses `false`.

However, **presentation enablement does not follow ordinary override behavior**. If a presentation is disabled globally, an individual menu cannot force-enable it with `enabled: true`.

Some settings are also read exclusively from the global configuration, such as Bedrock client detection and certain resource-pack generation options.

Menu content uses a separate fallback mechanism:

| Presentation | Content Lookup Order          |
| ------------ | ----------------------------- |
| Dialog / Ore | `dialog.content` → `content`  |
| Bedrock Form | `bedrock.content` → `content` |
| ArcMenu      | `arcmenu.content` → `content` |

If neither value is configured, an empty string is used.

## Menu Presentation Selection

UltimateShop supports multiple presentations for the same menu content.

| Presentation     | Global Setting                    | Menu Setting                            |
| ---------------- | --------------------------------- | --------------------------------------- |
| 📦 Inventory     | No additional enablement required | No additional enablement required       |
| 💬 Dialog        | `menu.dialog.enabled`             | `dialog.enabled`                        |
| 🪨 Ore           | `menu.dialog.enabled`             | `dialog.enabled` + `dialog.layout: ore` |
| ✨ ArcMenu        | `menu.arcmenu.enabled`            | `arcmenu.enabled`                       |
| 🛏️ Bedrock Form | `menu.bedrock.enabled`            | `bedrock.enabled`                       |

Dialog, Ore, ArcMenu, and Bedrock Form require the Premium edition and their respective runtime requirements.

### Java Client Priority

For menus that support these presentations, the selection priority is:

**ArcMenu → Dialog / Ore → Inventory**

Dialog and Ore are selected using `dialog.layout`:

| Value              | Presentation                 |
| ------------------ | ---------------------------- |
| `multi-action`     | Native Dialog action buttons |
| `item-action-list` | Native Dialog item list      |
| `ore`              | Ore graphical interface      |

For example:

```
dialog:
  enabled: true
  layout: ore

arcmenu:
  enabled: true
```

If both presentations are globally enabled and supported by the runtime, ArcMenu takes priority.

If ArcMenu cannot be used, UltimateShop can attempt Dialog/Ore. When no alternative presentation is available, Inventory is used.

### Bedrock Clients

For detected Bedrock players, UltimateShop determines whether to use Form based on `bedrock.enabled` and global `menu.bedrock.enabled`.

Bedrock Form is selected separately and does not participate in the Java client presentation priority.

### Default Values

If a menu does not explicitly define these settings:

| Menu Setting      | Default Value |
| ----------------- | ------------- |
| `dialog.enabled`  | `true`        |
| `bedrock.enabled` | `true`        |
| `arcmenu.enabled` | `false`       |

These defaults do not guarantee that a presentation will be used. Global settings and runtime requirements must also be satisfied.

## Button & Product Configuration

In addition to menu-level settings, UltimateShop allows individual buttons and products to define presentation-specific behavior.

### Presentation Visibility

Use `menu-visibility` to control which presentations can display a button or product.

For example:

```
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

| Option             | Presentation     |
| ------------------ | ---------------- |
| `inventory`        | Inventory        |
| `form`             | Bedrock Form     |
| `dialog`           | Native Dialog    |
| `item-action-list` | Dialog item list |
| `ore`              | Ore              |
| `arcmenu`          | ArcMenu          |

All options default to visible.

For `ore` and `item-action-list`, if their visibility is not explicitly configured, they inherit the value of `menu-visibility.dialog`.

For example:

```
menu-visibility:
  dialog: false
  ore: true
```

This hides the entry in native Dialog and Item Action List while allowing it to appear in Ore.

### Product Graphical Settings

Ore and ArcMenu allow individual products to override graphical settings.

For example, inside a shop file:

```
items:
  A:
    ui:
      price:
        enabled: false

      actions:
        primary:
          - buy
```

For supported product-level settings such as `ui.price` and `ui.actions`, the lookup priority is:

**Product `ui.*` → Menu `ui.products.*` → Global `menu.ui.products.*` → Default Value**

These settings only affect graphical product presentation in Ore and ArcMenu. They do not change the action buttons of native Dialog or Bedrock Form.

### Graphical Element Placement

Ore and ArcMenu use `ui.placement` to determine the graphical position of buttons and products.

For example:

```
buttons:
  A:
    display-item:
      material: EMERALD
      name: '&aExample'

    ui:
      appearance: button

      placement:
        area: sidebar
        order: 0
```

The `ui.placement` section references areas defined in the menu's `ui.layout.areas`.

Products can also inherit a default graphical placement from the menu's `ui.products.placement`.

Note that `menu-visibility` controls whether an entry is allowed to appear in a particular presentation, while `ui.placement` determines how it is positioned in graphical interfaces.

These settings serve different purposes and do not replace one another.

## Configuration Reference

The following table summarizes common menu settings and their locations.

| Configuration                          | YAML Path                             |
| -------------------------------------- | ------------------------------------- |
| Menu title                             | `title`                               |
| Menu content                           | `content`                             |
| Inventory size                         | `size`                                |
| Inventory slot layout                  | `layout`                              |
| Menu buttons                           | `buttons`                             |
| Menu opening conditions                | `conditions`                          |
| Menu opening actions                   | `open-actions`                        |
| Menu closing actions                   | `close-actions`                       |
| Dynamic layout                         | `dynamic-layout`                      |
| Dialog settings                        | `dialog`                              |
| Bedrock Form settings                  | `bedrock`                             |
| ArcMenu settings                       | `arcmenu`                             |
| Graphical canvas and areas             | `ui.layout`                           |
| Graphical product cards                | `ui.products.card`                    |
| Graphical product prices               | `ui.products.price`                   |
| Graphical product actions              | `ui.products.actions`                 |
| Graphical theme colors                 | `ui.theme`                            |
| Graphical text                         | `ui.text`                             |
| Ore graphical templates                | `ui.renderers.ore`                    |
| ArcMenu rendering settings             | `ui.renderers.arcmenu`                |
| Button or product visibility           | `menu-visibility`                     |
| Button or product graphical placement  | `ui.placement`                        |
| Button Dialog presentation options     | `dialog.show-sprite`, `dialog.layout` |
| Bedrock Form icons and additional text | `display-item.bedrock`                |
| Shop menu template                     | `settings.menu`                       |
| Shop-specific menu overrides           | `settings.menu-settings`              |
