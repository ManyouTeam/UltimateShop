# 🔄 Menu Configuration Compatibility

Use the current file structure and keys below when updating menu configurations. Presentation selection and graphical drawing have separate settings; older graphical selectors are not automatically interpreted as native controls.

## Configuration locations

| Configuration | Current location |
| --- | --- |
| Common menus | `menus/common_menus/` |
| Shop templates | `menus/shop_menu_templates/` |
| Quantity menus | `menus/buy_more_menus/` |
| Search / Favourite / Item Sell | Their dedicated subfolders under `menus/` |
| Shared graphical styles | `ui/<style>.yml` |
| Global settings | `config.yml` → `menu` |
| Shop-specific overrides | Shop `settings.menu-settings` |

Legacy top-level specialized menu folders and old Common-menu locations are moved into the organized menu folders at startup. Keep a backup before upgrades and retain unique IDs. Current dedicated folders classify specialized menus automatically; existing Common-folder files can use `menu-type`.

## Native presentation keys

| Purpose | Current key |
| --- | --- |
| Allow Dialog | Global `menu.dialog.enabled` and menu `dialog.enabled` |
| Choose native Dialog/Ore | Menu `dialog.layout: multi-action`, `item-action-list` or `ore` |
| Dialog description and dimensions | Menu `dialog.content`, `dialog.button-width`, `dialog.columns` |
| Dialog label sprite | Entry `dialog.show-sprite`; global `menu.dialog.auto-add-sprite.*` |
| Ore icon/pack behavior | Global `menu.dialog.ore.show-item-icon.*`, `menu.dialog.ore.pack-format` |
| Allow Form | Global `menu.bedrock.enabled` and menu `bedrock.enabled` |
| Form text and behavior | Menu `bedrock.*`, global `menu.bedrock.*` |
| Form entry icon/text/hiding | Active `display-item.bedrock.icon/extra-line/hide` |
| Direct Form product operation | Product `bedrock.click-event` |
| Allow ArcMenu | Global `menu.arcmenu.enabled` and menu `arcmenu.enabled` |
| Force presentation/style | Global `menu.force-ui.enabled/type/style` |
| Entry visibility | Entry `menu-visibility.inventory/form/dialog/item-action-list/ore/arcmenu` |

`ui.mode`, `ui.visible-in`, `ui.show-sprite`, `ui.renderers.form` and `ui.renderers.dialog` do not select or configure native presentations. Move intended settings to their supported paths above.

## Graphical keys

| Purpose | Current key |
| --- | --- |
| Select a shared style | Global `menu.ui.style` or menu `ui.style` |
| Canvas, headers, areas and pagination | `ui.layout.*` |
| Product placement defaults | `ui.products.placement` |
| Card appearance, compact prices and operations | `ui.products.card/price/actions` |
| Individual entry placement | Entry `ui.placement` |
| Button/card appearance and font | Entry `ui.appearance/font-size/text-align` |
| Colors and graphical labels | `ui.theme`, `ui.text` |
| Ore texture templates | `ui.renderers.ore.templates` |
| ArcMenu interaction/rendering | `ui.renderers.arcmenu` |

Style files contain these fields directly, without a `ui` wrapper. Existing inline `menu.ui` defaults remain supported. Move reusable graphical definitions into a style when appropriate and select it by ID.

## Example: select Ore with a shared style

This fragment belongs at menu root:

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

Enable global Dialog, give ordinary graphical buttons explicit placements, reload, then generate/distribute the Ore pack. Inventory slot coordinates are not converted into graphical positions. No category sidebar or Back button is inferred from old actions.

Search/Favourite use native Dialog when Ore is selected. Buy More does not use graphical styles. Item Sell and Sell All remain physical inventories. Check [supported menu types](ui-overview.md#supported-menu-types) before adding presentation fields to those files.

[Configuration & Inheritance](menu-configuration.md) · [Graphical UI Layout](../menus-getting-started/ui-layout.md)
