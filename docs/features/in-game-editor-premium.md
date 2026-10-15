# ✏️In-game Editor - Premium

The editor is built into UltimateShop Premium. Open it with `/shop editor` using the `ultimateshop.editor` permission; no separate editor addon is required.

## Enable the editor

Read `plugins/UltimateShop/editor-enable-confirm.yml`, back up the data folder, and set all five confirmations to `true` after completing them. Reload the plugin to apply the confirmation file. The editor writes changes directly to YAML files; use its Reload Plugin button or `/shop reload` to rebuild live menus after editing.

## Open a file

The home screen lists shops and each menu folder separately. Commands omit the `.yml` extension:

| Command | Folder relative to the plugin data folder |
| --- | --- |
| `/shop editor shop <id>` | `shops/` |
| `/shop editor menu <id>` | `menus/common_menus/` |
| `/shop editor shop_menu_template <id>` | `menus/shop_menu_templates/` |
| `/shop editor buy_more_menu <id>` | `menus/buy_more_menus/` |
| `/shop editor favourite_menu <id>` | `menus/favourite_menus/` |
| `/shop editor search_menu <id>` | `menus/search_menus/` |
| `/shop editor item_sell_menu <id>` | `menus/item_sell_menus/` |

All seven scopes support command completion. The editor opens existing files. To create a menu, copy the current bundled example into its matching folder, then open it in the editor. Legacy folders are moved during startup; see [Configuration Files](../info/configuration-files.md).

## Menu and UI presets

The menu preset edits root `title`, `content`, `size`, Inventory `layout`, conditions, actions and buttons. Open **UI** for the current presentation settings:

* `mode.java`: `inherit`, `inventory`, `dialog`, `ore`, `arcmenu`.
* `mode.bedrock`: `inherit`, `inventory`, `form`.
* `layout.canvas`, `layout.header`, `layout.areas`, `layout.pagination`: shared Ore/ArcMenu layout.
* `products`: default product placement, card style, price formats and operation lists.
* `renderers.dialog`: native Dialog layout, button width and columns.
* `renderers.form` and `renderers.ore`: presentation settings.
* `renderers.arcmenu`: right-click action, screen scale, update interval, animation and tooltips.

Paths here are relative to the menu's `ui` section. The global defaults live under `menu.ui` in `config.yml` and are edited in that file. A shop's `settings.menu-settings.ui` uses the same menu UI preset.

ArcMenu right-click defaults to `select-amount`, which opens product details. The choice field includes standard and configured custom product actions. ArcMenu does not render Inventory Buy More slots; changing `amount-items` or `confirm-items` affects the original quantity menu, not the ArcMenu details panel.

Search presets also expose input/result identifiers, action buttons, state buttons, empty results and result lore. Favourite presets expose result identifiers, the edit-mode button, empty state and normal/editing lore. Quantity and item-sell presets expose their own controls.

## Product and button UI

Open `ui` from a product or ordinary button preset to edit `visible-in`, `show-sprite`, `appearance`, placement, font size, text alignment, price and actions. The menu `ui.products` preset only exposes shared product defaults. Ordinary button actions remain in the button's root `actions` section; `ui.actions` controls product operation display and does not contain numbered action definitions.

For `visible-in`, enter a string list such as `inventory;;dialog;;ore;;arcmenu`; Form uses `form`. An empty list hides the entry everywhere. Removing the list restores the default visibility rules. `dialog` includes the native item-action-list layout.

## Saving and advanced sections

Follow the click hints on each field: choice fields cycle values, boolean fields toggle, and number/string fields open chat prompts. List prompts separate entries with `;;`. Right-click deletion is available where shown. Raw Section opens the YAML structure for fields outside a preset, including named layout areas, headers, price formats and extra actions. `ui.products`, `ui.actions` and areas named `content` are treated as UI settings, not trade items or executable actions.

Menu-level overrides take precedence over global defaults; product/button overrides take precedence where supported. Removing a local value restores inheritance after reload. See [Unified UI and ArcMenu](../menus/arcmenu-menus-premium.md) for placement dimensions, precedence and a layout example.
