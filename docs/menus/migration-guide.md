# 🔄Menu Configuration Compatibility

Use the current directory structure and keys below when upgrading or organizing menu configs. Presentation selection and graphical drawing are configured separately—older graphical selectors are not interpreted as native control settings.

## Current File Locations

| Configuration | Current Location |
| --- | --- |
| Common menus | `menus/common_menus/` |
| Shop menu templates | `menus/shop_menu_templates/` |
| Buy More menus | `menus/buy_more_menus/` |
| Search / Favourite / Item Sell | Dedicated subfolders under `menus/` |
| Shared graphical styles | `ui/<style>.yml` |
| Global defaults | `config.yml` → `menu` |
| Per-shop overrides | Shop `settings.menu-settings` |

Legacy root-level special folders and common menu locations migrate on startup (back up before upgrading and keep IDs unique). Dedicated subfolders auto-detect menu types, while legacy files in `common_menus` can declare `menu-type`.

## Native Presentation Keys

| Purpose | Current Key |
| --- | --- |
| Allow Dialog | Global `menu.dialog.enabled` and menu `dialog.enabled` |
| Choose native Dialog / Ore | Menu `dialog.layout: multi-action`, `item-action-list`, or `ore` |
| Dialog text & sizing | Menu `dialog.content`, `dialog.button-width`, `dialog.columns` |
| Dialog sprite icon | Entry `dialog.show-sprite`, global `menu.dialog.auto-add-sprite.*` |
| Ore icon / pack behaviour | Global `menu.dialog.ore.show-item-icon.*`, `menu.dialog.ore.pack-format` |
| Allow Bedrock Form | Global `menu.bedrock.enabled` and menu `bedrock.enabled` |
| Form text & behaviour | Menu `bedrock.*`, global `menu.bedrock.*` |
| Form entry icon / extra line / hide | `display-item.bedrock.icon` / `extra-line` / `hide` |
| Form direct product action | Product `bedrock.click-event` |
| Allow ArcMenu | Global `menu.arcmenu.enabled` and menu `arcmenu.enabled` |
| Force presentation / style | Global `menu.force-ui.enabled` / `type` / `style` |
| Per-presentation visibility | Entry `menu-visibility.inventory/form/dialog/item-action-list/ore/arcmenu` |

`ui.mode`, `ui.visible-in`, `ui.show-sprite`, `ui.renderers.form`, and `ui.renderers.dialog` do not select or configure native presentations—migrate them to the supported keys above.

## Graphical UI Keys

| Purpose | Current Key |
| --- | --- |
| Select shared style | Global `menu.ui.style` or menu `ui.style` |
| Canvas, headers, areas & pagination | `ui.layout.*` |
| Default product placement | `ui.products.placement` |
| Card appearance, compact price & actions | `ui.products.card` / `price` / `actions` |
| Per-entry placement | Entry `ui.placement` |
| Button/card appearance & text align | Entry `ui.appearance` / `font-size` / `text-align` |
| Colors & graphical labels | `ui.theme`, `ui.text` |
| Ore texture templates | `ui.renderers.ore.templates` |
| ArcMenu interaction & rendering | `ui.renderers.arcmenu` |

Style files in `ui/` hold these fields directly at the root (no outer `ui:` key). Move reusable graphical definitions into shared styles and reference them by ID.

## Example: Select Ore with a Shared Style

Place this at the menu root:

```yaml
dialog:
  enabled: true
  layout: ore
bedrock:
  enabled: true
arcmenu:
  enabled: false
ui:
  style: classic
  products:
    placement:
      area: content
```

Enable global `menu.dialog.enabled`, add explicit `ui.placement` to common graphical buttons, reload, and run `/shop dialogpack` to build and distribute the resource pack (Inventory slots never auto-convert into graphical coordinates or infer sidebar/back buttons).

Under Ore, Search and Favourite fall back to native Dialog, Buy More uses no graphical style, and Item Sell / Sell All stay physical Inventory; see [Supported Menu Types](ui-overview.md#supported-menu-types).

[Menu Configuration & Inheritance](menu-configuration.md) · [Graphical UI Layout](../menus-getting-started/ui-layout.md)
