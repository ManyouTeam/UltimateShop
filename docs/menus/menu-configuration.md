# ⚙️ Menu Configuration & Inheritance

Use this page to locate menu options and understand how configuration values are resolved. Default settings are available in the [repository's `config.yml`](https://github.com/ManyouTeam/UltimateShop/blob/master/plugin/src/main/resources/config.yml); use the examples and configuration supplied with your installation when checking its supported settings.

## Where a setting belongs

| Scope | YAML path | Purpose |
| --- | --- | --- |
| Global menu controls | `config.yml` → `menu.*` | Auto-open, click mappings, cooldown, refresh, quantity menus |
| Global UI defaults | `config.yml` → `menu.ui.*` | UI mode, shared graphical layout, product presentation, renderer settings |
| Individual menu | `menus/<folder>/<id>.yml` → root | `title`, `size`, `layout`, `buttons`, conditions/actions |
| Individual menu UI | Menu root `ui.*` | Override global UI settings for that menu |
| Shop menu override | `shops/<shop>.yml` → `settings.menu-settings.*` | Override the chosen shop template's menu settings |
| Product/button | Entry root `ui.*` or `display-item.*` | Item-specific appearance, visibility, actions and placement |

Use `ui.mode.java` / `ui.mode.bedrock` to choose a presentation. Do **not** move Inventory `size` or `layout` under `ui.renderers.inventory`; that section is currently reserved.

## UI inheritance

Global defaults are defined in `config.yml`:

```yaml
menu:
  ui:
    mode:
      java: inventory
      bedrock: form
```

A menu overrides only what it needs:

```yaml
title: '&8VIP Shop'
ui:
  mode:
    java: arcmenu
    bedrock: inherit
```

`inherit` uses the global mode. Missing keys inside the menu's `ui` are filled recursively from `menu.ui` when the menu is initialized. **Explicit menu values win over global values**. This also means the shipped example files can override what appears to be a global default.

For a Shop Menu, `settings.menu-settings` is merged into the selected template **before** the global UI defaults are applied. See [Shop Menus](shop-menus.md).

## Product-specific UI values

Product settings such as `ui.actions` and `ui.price` are not read the same way as the entire menu-wide `ui` block. The relevant lookup is **product → owning shop menu → global `menu.ui.products`**. Product-level overrides must therefore be placed at the product root, not inside its `display-item`.

```yaml
items:
  A:
    ui:
      actions:
        primary: [buy, sell]
```

Custom actions still require valid click bindings; adding a label or listing an action in `ui.actions` does not create a click handler. Read [Custom Click Event](../features/custom-click-event-premium.md).

## Global controls outside menu.ui

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
