# 🔄 Menu key reference

This page documents the **current** configuration structure. It is not a promise to accept superseded configuration formats.

## ✅ Native keys

| Purpose | Use this key |
| --- | --- |
| Enable Java Dialog | Global `menu.dialog.enabled`, menu `dialog.enabled` |
| Choose Dialog or Ore | Menu `dialog.layout: multi-action` / `item-action-list` / `ore` |
| Native Dialog content and dimensions | `dialog.content`, `dialog.button-width`, `dialog.columns` |
| Native Dialog sprites | `dialog.show-sprite` on an entry; `menu.dialog.auto-add-sprite.*` globally |
| Ore item-icon and pack behavior | `menu.dialog.ore.show-item-icon.*` and `menu.dialog.ore.pack-format` |
| Enable Bedrock Form | Global `menu.bedrock.enabled`, menu `bedrock.enabled` |
| Form labels and behavior | Menu `bedrock.*` / global `menu.bedrock.*` |
| Form icon, extra text or hide rule | `display-item.bedrock.icon/extra-line/hide` |
| Bedrock product shortcut | Product `bedrock.click-event` |
| ArcMenu enablement | Global `menu.arcmenu.enabled`, menu `arcmenu.enabled` |
| Visibility for individual entry | `menu-visibility.inventory/form/dialog/item-action-list/ore/arcmenu` |

## 🎨 Graphical-only keys

| Purpose | Use this key |
| --- | --- |
| Logical canvas and placement areas | `ui.layout.*` |
| Default graphical product placement | `ui.products.placement.*` |
| Product-card appearance / price / controls | `ui.products.card/price/actions.*` |
| Button/item graphical position | Entry `ui.placement.*` |
| Graphical appearance and font scale | Entry `ui.appearance/font-size/text-align` |
| Graphical colors and labels | `ui.theme.*`, `ui.text.*` |
| Ore graphical templates | `ui.renderers.ore.templates.*` |
| ArcMenu drawing and mouse interaction | `ui.renderers.arcmenu.*` |

The menu-wide `ui` section inherits missing values from `config.yml` → `menu.ui`. Menu `settings.menu-settings` can override a shop template.

## ❌ Keys that are no longer read

Do not configure `ui.mode`, `ui.visible-in`, `ui.show-sprite`, `ui.renderers.form` or `ui.renderers.dialog`. They will not select presentations or configure native controls.

## 🔍 Choose the correct layout

```yaml
# A menu, not config.yml
dialog:
  enabled: true
  layout: ore
bedrock:
  enabled: true
arcmenu:
  enabled: false
ui:
  products:
    placement:
      area: content
```

`dialog.layout` chooses the renderer; `ui.layout` configures graphics. The Ore renderer does not import old sidebars or infer pixel coordinates from Inventory `layout`. Give visible graphical entries a placement.

[Menu configuration](menu-configuration.md) · [Presentations](ui-overview.md) · [Graphical layout](ui-layout.md).
