# ⚙️ Menu Configuration & Inheritance

Use this page to locate menu options and understand how configuration values are resolved. Default settings are available in the [repository's `config.yml`](https://github.com/ManyouTeam/UltimateShop/blob/master/plugin/src/main/resources/config.yml); use the examples and configuration supplied with your installation when checking its supported settings.

## Where a setting belongs

| Scope | YAML path | Purpose |
| --- | --- | --- |
| Global menu controls | `config.yml` → `menu.*` | Auto-open, click mappings, cooldown, refresh, quantity menus |
| Global graphical defaults | `config.yml` → `menu.ui.*` | Ore / ArcMenu canvas, cards, theme, graphic renderers |
| Individual menu | `menus/<folder>/<id>.yml` → root | `title`, `size`, `layout`, `buttons`, conditions/actions |
| Individual graphical UI | Menu root `ui.*` | Override Ore / ArcMenu graphical settings |
| Shop menu override | `shops/<shop>.yml` → `settings.menu-settings.*` | Override the chosen shop template's menu settings |
| Product/button | Entry root `ui.*`, `menu-visibility`, `dialog.*`, `display-item.*` | Graphical styling/position, display visibility, Dialog options and item display |

Select Java Dialog/Ore with `menu.dialog.enabled`, menu `dialog.enabled` and `dialog.layout`. Select Bedrock Form with `menu.bedrock.enabled` and menu `bedrock.enabled`. Opt into ArcMenu using both global and menu `arcmenu.enabled`. Inventory `size` and root `layout` remain unchanged.

## Renderer selection and graphical inheritance

```yaml
# config.yml
menu:
  dialog:
    enabled: false
  bedrock:
    enabled: true
  arcmenu:
    enabled: false
  ui:
    layout:
      canvas:
        width: 648
        height: 414
```

```yaml
# An individual menu
dialog:
  enabled: true
  layout: ore
  content: '&7Browse this shop.'
bedrock:
  enabled: true
  content: '&7Browse this shop.'
arcmenu:
  enabled: false
ui:
  products:
    card:
      icon-size: 32
```

If global and menu ArcMenu are both enabled and the integration is available, ArcMenu opens first. Otherwise, enabled/supported Dialog or Ore is tried before Inventory. Bedrock is handled separately. For Ore, `dialog.layout: ore` chooses the current graphical renderer; its actual canvas, placements and appearance still come from `ui`.

Missing keys inside the menu's `ui` inherit from global `menu.ui` recursively. Explicit menu graphical settings take precedence. Shop `settings.menu-settings` overlays the menu before these graphical defaults are applied. Existing menu-level `ui.mode` remains readable for transition compatibility. Global `menu.ui.mode` is also accepted when no explicit native/ArcMenu global switches are present; new configurations should use `menu.dialog`, `menu.bedrock` and `menu.arcmenu` instead.

## Product-specific UI values

Graphical product settings such as `ui.actions` and `ui.price` affect Ore and ArcMenu, not ordinary Form or native Dialog. The relevant lookup is **product → owning shop menu → global `menu.ui.products`**. Product-level overrides must therefore be placed at the product root, not inside its `display-item`.

```yaml
items:
  A:
    ui:
      actions:
        primary: [buy, sell]
```

Custom actions still require valid click bindings; adding a label or listing an action in `ui.actions` does not create a click handler. Read [Custom Click Event](../features/custom-click-event-premium.md).

## Global controls outside graphical `menu.ui`

| Path under `menu` | Purpose |
| --- | --- |
| `auto-open.enabled`, `auto-open.menu` | Default destination for `/shop` |
| `cooldown.click`, `cooldown.reopen` | Click/reopen throttling; `-1` disables |
| `prompt.cancel-keyword` | Chat prompt cancellation |
| `ignore-click-outside`, `anti-dupe-checker` | Inventory interaction checks |
| `title-update.*` | Inventory title refresh (Premium; PacketEvents support required) |
| `menu-update.*` | Inventory item refresh |
| `click-event.*` | Product action to Bukkit click-type mapping |
| `click-event-actions.*` | Custom product actions (Premium) |
| `buy-more-menu.*` | Quantity-menu defaults |
| `search-gui.*`, `favourite-gui.*` | Search/Favourite lists and prompt settings |
| `sell-all.*` | Global Sell All inventory |
| `dialog.*`, `bedrock.*`, `arcmenu.enabled` | Presentation selection and native renderer settings |

For example, the bundled product click mapping is:

```yaml
menu:
  click-event:
    buy: SHIFT_LEFT
    sell: RIGHT
    buy-or-sell: LEFT
    select-amount: SHIFT_RIGHT
    add-favourite: SWAP_OFFHAND
    sell-all: DROP
```

A binding can contain multiple Bukkit click names separated by `;;`; `NEVER` disables a binding. These actions and [UI product action buttons](ui-layout.md) are related but not interchangeable.

## Performance and maintenance

`menu.menu-update.circle-update` and `menu.menu-update.click-update` refresh Inventory item displays; leave periodic updates off unless your menus require them. Inventory title updates can visibly flash. ArcMenu has its own `ui.renderers.arcmenu.update-interval`. Configure menu refresh through `menu.menu-update`; `menu.shop` is not a menu refresh configuration path.

After editing YAML, run `/shop reload`. After changing Ore assets or graphical layout, regenerate and distribute its resource pack as explained in [Ore](ore-menus-premium.md).

## Related pages

[Layout and buttons](layout-and-buttons.md) · [UI modes](ui-overview.md) · [Configuration compatibility](migration-guide.md) · [Troubleshooting](troubleshooting.md)
