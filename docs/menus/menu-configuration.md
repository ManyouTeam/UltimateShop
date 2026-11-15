# ⚙️ Menu Configuration & Inheritance

Configure shared behavior in `config.yml`, reusable graphical styles in `ui/`, and individual screens in `menus/`. Shop files select a template and can override it for that shop.

## File locations

```text
plugins/UltimateShop/
├── config.yml
├── ui/
│   ├── classic.yml
│   └── bedrock.yml
├── menus/
│   ├── common_menus/
│   ├── shop_menu_templates/
│   ├── buy_more_menus/
│   ├── search_menus/
│   ├── favourite_menus/
│   └── item_sell_menus/
└── shops/
```

Menu and shop IDs are file names without `.yml`; custom subfolders do not change those IDs. Style IDs include their relative subfolder: `ui/custom/dark.yml` is selected as `custom/dark`.

## Global settings and menu settings

This example belongs in `config.yml`:

```yaml
menu:
  force-ui:
    enabled: false
    type: inventory
    style: ''
  dialog:
    enabled: false
  bedrock:
    enabled: true
  arcmenu:
    enabled: false
  ui:
    style: classic
```

The corresponding settings in a menu file have no `menu` wrapper:

```yaml
title: '&8Categories'
dialog:
  enabled: true
  layout: multi-action
bedrock:
  enabled: true
arcmenu:
  enabled: false
ui:
  style: classic
```

In normal selection, both global and local switches must allow a presentation. Menu `dialog.enabled` and `bedrock.enabled` default to `true`; menu `arcmenu.enabled` defaults to `false`. `menu.force-ui` overrides those switches when enabled. Platform and menu-type requirements still apply. See [UI Modes](ui-overview.md).

Supported native labels and options are resolved from menu `dialog.*` / `bedrock.*`, then global `menu.dialog.*` / `menu.bedrock.*`. Some integration and resource-generation settings are global only; their presentation pages identify them. Screen descriptions use local `dialog.content`, `bedrock.content` or `arcmenu.content`, falling back to root `content`.

## Shop templates and overrides

A shop selects `menus/shop_menu_templates/farming.yml` with:

```yaml
settings:
  menu: farming
  menu-settings:
    title: '&aFarming Specials'
    dialog:
      enabled: true
      layout: multi-action
    ui:
      products:
        card:
          icon-size: 40
```

`settings.menu-settings` belongs in the **shop file**. It merges into the chosen template before graphical defaults are applied.

| Value in the override | Result |
| --- | --- |
| A scalar such as `title` or `enabled` | Replaces the template value |
| A nested section | Merges recursively with the template section |
| A list such as `layout` or lore | Replaces the entire template list |
| An omitted key | Preserves the template value |

For example, overriding `dialog.layout` preserves the template's `dialog.columns`. To replace an Inventory layout, supply all its rows. A shop without `settings.menu` can define its screen directly in `settings.menu-settings`.

## Shared graphical styles

`ui/classic.yml` and `ui/bedrock.yml` provide the bundled graphical defaults. The plugin creates missing bundled style files and preserves existing files. Copy a preset to `ui/my-style.yml` to maintain your own version.

A style file contains UI fields **directly**, without a wrapping `ui` section:

```yaml
theme:
  accent: '#6CC349'
products:
  card:
    icon-size: 40
```

This partial style can be selected with `ui.style: my-style`; other fields fall back to global graphical defaults. For a complete independent layout, copy a full bundled preset before editing it.

Select the global style in `config.yml`:

```yaml
menu:
  ui:
    style: classic
    theme:
      accent: '#65E8EF'
```

Select a different style for one menu:

```yaml
ui:
  style: bedrock
  theme:
    accent: '#6CC349'
```

Normal graphical inheritance works in this order:

1. Load the global style; explicit `menu.ui` values override its defaults.
2. If a menu selects a different style, overlay that style on the resolved global defaults.
3. Fill missing menu `ui` fields from those defaults. Explicit menu fields, including shop overrides, win.

This means a different per-menu style can replace a global accent, while a menu's own `ui.theme.accent` always wins in normal selection. Nested sections merge recursively; lists are replaced as whole values.

The styles supply graphical layout and appearance for Ore/ArcMenu. `ui.style: bedrock` changes graphics; it does not turn a Java screen into a Bedrock Form.

## Force a presentation and style

In `config.yml`:

```yaml
menu:
  force-ui:
    enabled: true
    type: arcmenu
    style: bedrock
```

`type` selects the presentation globally. A nonempty `style` also overrides menu-local graphical values wherever the resolved forced defaults provide that field. Local fields absent from those defaults remain. Entry placements and product-level overrides still need to fit the chosen layout.

Leave `style: ''` to force only the presentation and retain normal style selection. Both forced presentation and forced style are ignored when `enabled: false`. Supported types and client behavior are listed in [UI Modes](ui-overview.md#force-one-presentation).

## Product and button settings

| Purpose | Location |
| --- | --- |
| Ordinary button item and actions | Menu `buttons.<id>.display-item/actions` |
| Product item, prices and trade rules | Shop `items.<id>` |
| Presentation visibility | Button/product `menu-visibility.<presentation>` |
| Graphical product placement | Menu `ui.products.placement`, overridden by product `ui.placement` |
| Ordinary graphical button placement | Button `ui.placement` |
| Graphical product price and actions | Product `ui.price` / `ui.actions`, then owning shop menu `ui.products.*`, then global `menu.ui.products.*` |
| Graphical card styling | Menu `ui.products.card` |
| Form icon and supplemental line | Active `display-item.bedrock.*` |
| Form direct product action | Product `bedrock.click-event` |

A product displayed in Search/Favourite retains its source product settings and trade rules. Graphical action/price lookup uses the owning shop menu for its menu-level defaults.

## Scope by menu type

Common and Shop support Ore and ArcMenu defaults. Search and Favourite inherit ArcMenu defaults but exclude Ore template and card-lore defaults. Item Sell and Buy More do not inherit graphical `menu.ui` settings. Buy More reads its own `dialog.enabled` for native quantity input; its source shop supplies Form/Dialog quantity labels. Sell All uses only its dedicated `menu.sell-all` screen configuration.

Reload with `/shop reload` after editing menus, styles or selectors. Ore layout and texture changes also require rebuilding and distributing its [resource pack](ore-menus-premium.md#generate-the-resource-pack).

[Layout & Buttons](layout-and-buttons.md) · [Graphical UI Layout](../menus-getting-started/ui-layout.md)
