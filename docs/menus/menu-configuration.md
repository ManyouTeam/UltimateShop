# ⚙️Menu Configuration & Inheritance

Menu configuration is split across three layers: `config.yml` holds global defaults, `ui/` stores reusable graphical styles, and `menus/` defines individual screens. Shop files select a menu template and can override template settings per shop.

## File Locations

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

Menu and shop IDs are file names without `.yml`, even if placed inside subfolders. **Graphical style IDs**, however, include their relative subfolder path (for example, `ui/custom/dark.yml` has ID `custom/dark`).

## Global vs. Menu Settings

Configure global defaults in `config.yml`:

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

In individual menu files, place the corresponding keys at the root (without the outer `menu:` wrapper):

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

During normal selection, **both the global switch and the menu's own switch must allow** a presentation for it to be used. By default, a menu's `dialog.enabled` and `bedrock.enabled` are `true`, while `arcmenu.enabled` is `false`. Enabling global `menu.force-ui` overrides these switches (though platform and menu-type limits still apply); see [UI Modes](ui-overview.md).

Native Dialog and Form labels/options read the menu's `dialog.*` / `bedrock.*` first, falling back to global `menu.dialog.*` / `menu.bedrock.*` (some integration and pack-generation options are global-only). Screen descriptions read `dialog.content`, `bedrock.content`, or `arcmenu.content`, falling back to root `content`.

## Shop Templates and Overrides

A shop selects a template from `menus/shop_menu_templates/` (such as `farming.yml`) and can override specific fields under `settings.menu-settings`:

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

`settings.menu-settings` lives in the **shop file**. It merges onto the chosen template before graphical defaults are applied:

| Override Type | Merge Behaviour |
| --- | --- |
| Scalars (`title`, `enabled`, etc.) | Replaces the template value |
| Nested sections (Maps) | Deep-merges recursively with the template |
| Lists (`layout`, lore, etc.) | Replaces the entire list |
| Omitted keys | Keeps the template value |

For example, overriding `dialog.layout` leaves `dialog.columns` from the template intact, whereas overriding `layout` requires supplying all rows. Shops without `settings.menu` can define their entire screen directly inside `settings.menu-settings`.

## Shared Graphical Styles

`ui/classic.yml` and `ui/bedrock.yml` are the built-in graphical presets (the plugin only creates missing files and never overwrites your edits). Copy a preset to `ui/my-style.yml` to customize it.

Style files contain UI fields **directly at the root**, without an outer `ui:` key:

```yaml
theme:
  accent: '#6CC349'
products:
  card:
    icon-size: 40
```

If a style file only defines a few fields, referencing `ui.style: my-style` inherits the remaining fields from the global graphical defaults. For a fully independent layout, copy an entire built-in preset first.

Set the global style in `config.yml`, or pick a different style per menu:

```yaml
# config.yml
menu:
  ui:
    style: classic
    theme:
      accent: '#65E8EF'
```

```yaml
# Individual menu file
ui:
  style: bedrock
  theme:
    accent: '#6CC349'
```

Normal graphical inheritance order:

1. Load the global style file, then apply explicit `menu.ui` overrides from `config.yml`.
2. If the menu selects a different `ui.style`, merge that style on top of the resolved global defaults.
3. Fill any missing `ui` fields on the menu; explicit fields on the menu (including shop `menu-settings`) always win.

{% hint style="info" %}
Graphical styles only affect Ore and ArcMenu layouts and visuals. `ui.style: bedrock` is a visual preset and never turns a Java screen into a Bedrock Form.
{% endhint %}

<a id="force-a-presentation-and-style"></a>

## Force a Presentation and Style

Configure `menu.force-ui` in `config.yml`:

```yaml
menu:
  force-ui:
    enabled: true
    type: arcmenu
    style: bedrock
```

* `type` forces the presentation globally across supported menus.
* When `style` is non-empty, its resolved defaults overwrite the corresponding graphical fields on menus (local fields not defined in the forced style are kept).
* Leave `style: ''` to force only the presentation while keeping per-menu style selection; `enabled: false` disables both. See [UI Modes](ui-overview.md#force-one-presentation).

## Where Product & Button Settings Live

| Purpose | Location |
| --- | --- |
| Common button item & actions | Menu `buttons.<id>.display-item` / `actions` |
| Product item, prices & trade rules | Shop `items.<id>` |
| Per-presentation visibility | Button or product root `menu-visibility.<presentation>` |
| Graphical product placement | Menu `ui.products.placement` (overridden by product `ui.placement`) |
| Common graphical button placement | Button `ui.placement` |
| Graphical product price & actions | Product `ui.price` / `ui.actions` → owning shop menu `ui.products.*` → global `menu.ui.products.*` |
| Graphical card styling | Menu `ui.products.card` |
| Bedrock Form icon & extra line | Active `display-item.bedrock.*` |
| Bedrock Form direct product action | Product `bedrock.click-event` |

Products shown in Search or Favourite menus keep their source shop's trade rules and settings; menu-level graphical defaults for actions and prices come from the product's owning shop menu.

## Menu-Type Scope

Different menu types support different levels of graphical styling:

* **Common & Shop menus**: full support for both Ore and ArcMenu defaults and card settings.
* **Search & Favourite menus**: support ArcMenu styling, but **do not use Ore** (they ignore Ore templates and card description defaults).
* **Item Sell & Buy More menus**: do not inherit global graphical `menu.ui` settings. Buy More only checks its own `dialog.enabled` to decide whether to use native quantity input, while Form/Dialog labels come from the source shop.
* **Sell All menu**: a standalone global chest screen that reads only `config.yml` → `menu.sell-all`.

Run `/shop reload` after editing menus, styles, or selectors. If you edit Ore layouts or textures, also rebuild and distribute the [resource pack](ore-menus-premium.md#generate-the-resource-pack).

[Layout & Buttons](layout-and-buttons.md) · [Graphical UI Layout](../menus-getting-started/ui-layout.md)
