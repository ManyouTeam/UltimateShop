# 🔄 Menu Configuration Compatibility

Most menu options retain their long-standing names. The graphical `ui` section is reserved for Ore and ArcMenu canvas/layout, appearance and controls, not native GUI selection. Existing newer `ui.mode`, `ui.visible-in` and Dialog/Form renderer paths remain readable where implemented. A global `menu.ui.mode` selector is honored only when no explicit `menu.dialog.enabled`, `menu.bedrock.enabled` or `menu.arcmenu.enabled` switch is configured; new examples should use the original keys.

## Native configuration keys

| Purpose | Config location |
| --- | --- |
| Enable native Dialog globally | `config.yml`: `menu.dialog.enabled` |
| Enable Dialog for a menu | Menu root `dialog.enabled`, default true |
| Native Dialog / Ore selection | Menu root `dialog.layout: multi-action`, `item-action-list` or `ore` |
| Dialog text and width | Menu root `dialog.content`, `dialog.button-width`, `dialog.columns` |
| Global Dialog labels and sprite options | `menu.dialog.*` |
| Global Ore icon and pack format | `menu.dialog.ore.*` |
| Enable Bedrock Form | `menu.bedrock.enabled` and menu `bedrock.enabled` |
| Form content and labels | Menu root `bedrock.content` / `bedrock.*`; global `menu.bedrock.*` |
| Product Form icon and text | `display-item.bedrock.icon`, `extra-line`, `hide` |
| Product Form action shortcut | Product root `bedrock.click-event` |
| Per-presentation visibility | Entry `menu-visibility.inventory/form/dialog/item-action-list/ore/arcmenu` |
| Button sprite | Button `dialog.show-sprite` |
| Button Dialog layout | Button `dialog.layout` |
| ArcMenu opt-in | Global `menu.arcmenu.enabled`, menu `arcmenu.enabled` |

## Graphical configuration keys

`menu.ui` and menu `ui` configure graphical canvas areas and pagination (`ui.layout`), UI cards and graphical buttons (`ui.products`, entry `ui.placement`, `ui.appearance`, `ui.font-size`, `ui.text-align`), theme, labels, and graphic-only `ui.renderers.ore` / `ui.renderers.arcmenu` settings.

`dialog.layout: ore` uses the **current** Ore renderer with the graphical layout model. Legacy Ore sidebar auto-import, manual template coordinates and other removed layout algorithms are not restored simply by accepting native `dialog` keys. Configure Ore positioning through `ui.layout` and `ui.placement`.

## Presentation priority

```yaml
# config.yml
menu:
  dialog:
    enabled: true
  bedrock:
    enabled: true
  arcmenu:
    enabled: true
```

```yaml
# menu file
dialog:
  enabled: true
  layout: ore
  content: '&7Products'
bedrock:
  enabled: true
  content: '&7Products'
arcmenu:
  enabled: true
```

Java selection is **ArcMenu → Dialog/Ore → Inventory**. ArcMenu requires a compatible integration, and native Dialog/Ore requires supported Paper APIs. Bedrock follows Form configuration independently.

## Existing configurations

- Keep `layout`, `buttons`, product IDs, conditions, actions and the special search/favourite/quantity/input nodes unchanged.
- Keep `display-item.bedrock` and product `bedrock.click-event`.
- Move newly written renderer controls out of `ui.renderers.form` and `ui.renderers.dialog` into `bedrock` and `dialog`. Graphical renderer options remain under `ui.renderers.ore` and `ui.renderers.arcmenu`.
- Do not rewrite old Ore visual placement values expecting pixel-identical output: current Ore uses graphical canvas/areas.
- Menu folder locations are documented separately; changing these configuration keys does not move files.

After changing an Ore graphical layout or assets, regenerate its resource pack with `/shop dialogpack` and distribute the updated pack. See [Graphical Layout](ui-layout.md), [Dialog](dialog-menus-premium.md) and [Bedrock Form](bedrock-menus-premium.md).
