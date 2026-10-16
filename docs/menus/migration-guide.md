# 🔄 Menu Configuration Compatibility

Use this guide when adapting an existing UltimateShop menu, copying examples from another installation or switching between menu presentations. **Keep supported configuration keys unchanged unless there is a functional reason to move them.** The menu system distinguishes between menu types, client UI modes and item display settings.

## Review an existing configuration

1. Back up `config.yml`, `shops/` and `menus/` before making changes.
2. Compare your files with the default configuration and menu examples provided with your installation.
3. Keep product IDs, menu IDs, Inventory `size`, root `layout` and `buttons` unless you intend to redesign them.
4. When selecting a different UI mode, configure only the settings required by that renderer. Avoid a global search-and-replace of `bedrock`, `dialog` or `ui`.
5. Test Common, Shop, Search, Favourite, Buy More, Item Sell and Sell All where applicable. Include Bedrock testing if you use Forms.
6. Run `/shop reload` after editing YAML. Regenerate and distribute the Ore resource pack after graphical layout or asset changes.

## Configuration locations

| Purpose | Configuration path | Notes |
| --- | --- | --- |
| Global Java/Bedrock mode | `config.yml` → `menu.ui.mode.java` / `bedrock` | `inventory` is the Java default, `form` the Bedrock default |
| Menu mode override | Menu root `ui.mode` | `inherit` selects the global mode |
| Shop-specific override | Shop `settings.menu-settings.ui` | Merged into the menu before global UI defaults |
| Native Dialog settings | `menu.ui.renderers.dialog` / menu `ui.renderers.dialog` | `multi-action` and `item-action-list` are native layouts |
| Ore renderer settings | `menu.ui.renderers.ore` | Select Ore with `ui.mode.java: ore` |
| ArcMenu settings | `menu.ui.renderers.arcmenu` | Requires the ArcMenu integration |
| Bedrock Form settings | `menu.ui.renderers.form` | Menu-specific settings can override supported global fields |
| Shared graphical layout | `ui.layout`, `ui.products`, `ui.theme`, `ui.text` | Used by Ore/ArcMenu, not Inventory slot positioning |
| Form item icon and text | `display-item.bedrock.icon`, `extra-line`, `hide` | **These belong in the display item, not under `ui`** |
| Direct Form product click | Product root `bedrock.click-event` | Separate from display-item `bedrock` icon settings |

## Check configuration paths when reusing examples

A copied configuration may contain options that are not read by the selected renderer. Check their placement and purpose before removing or renaming anything.

| Option found in a copied configuration | Configuration to use |
| --- | --- |
| Menu root `bedrock.enabled` / `bedrock.content` | `ui.mode.bedrock` and root `content` |
| Menu root `dialog.enabled` / `dialog.content` | `ui.mode.java`, `ui.renderers.dialog` and root `content` |
| Button `dialog.layout` | Button `ui.renderers.dialog.layout` for native Dialog |
| Display-item `dialog.show-sprite` | Button/product `ui.show-sprite` where supported |
| `menu-visibility` | Button/product `ui.visible-in` |
| Separate graphical sidebar/coordinate settings | `ui.layout` and entry `ui.placement` |
| `menu.sell-all.ignore-items` | `sell.sell-all.ignore-items` |
| `menu.shop` refresh controls | `menu.menu-update.circle-update` / `click-update` |

**Do not infer that a key is unsupported merely because an example omits it.** Inventory root `layout`, display-item `bedrock` icon settings and product-root `bedrock.click-event` have distinct, valid purposes.

## Menu type and file locations

Search, Favourite and Item Sell files can use their dedicated folders. A file in `menus/common_menus/` can also specify `menu-type: search`, `favourite` (or `favorite`), or `item-sell` (or `item_sell`). The `menu.search-gui.menu` and `menu.favourite-gui.menu` lists are also used to classify entries from the common menu folder. Keep working configurations that use those options; the dedicated folders are a convenient alternative, not a reason to rename unrelated settings.

## UI mode example

Menu-level presentation settings are located under root `ui`, while title and content remain at the menu root:

```yaml
title: '&8Shop'
content: '&7Select a product'
ui:
  mode:
    java: dialog
    bedrock: form
  renderers:
    dialog:
      layout: multi-action
      columns: 2
```

Bedrock Form icon settings belong inside the entry's `display-item`:

```yaml
buttons:
  A:
    display-item:
      material: DIAMOND
      name: '&bOffers'
      bedrock:
        icon: 'path;;textures/items/diamond.png'
        extra-line: '&7Open offers'
```

## Verify the result

If global `menu.ui` changes do not take effect, inspect explicit settings in the selected template and shop `settings.menu-settings`. If graphical buttons are missing, check their `ui.placement` and visibility. For Ore resource changes, generate and load the resource pack. Item Sell and Sell All remain Inventory deposit interfaces.

[Menu Configuration](menu-configuration.md) · [UI Modes](ui-overview.md) · [Troubleshooting](troubleshooting.md)
