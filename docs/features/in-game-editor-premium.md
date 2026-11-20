# ✏️In-game Editor - Premium

Run `/shop editor` to open the editor. To enable it for the first time, set `editor.enabled` to `true` in `plugins/UltimateShop/config.yml` and run `/shop reload`. Players need the `ultimateshop.editor` permission.

## Open the file you want to change

To edit a product, choose **Edit Shops**, open the shop file, then open `items` and choose the product. To edit a menu title, slot layout or button, choose the matching menu category on the home screen.

Choose **Configuration** at the bottom of the home screen to open `config.yml`, shared UI styles, saved items, language files, placeholders, shared use times, sell sticks or sell chests.

You can also open a file by command. For example, `/shop editor shop example` opens `shops/example.yml`, and `/shop editor config config` opens `config.yml`. Leave off the `.yml` extension and use Tab to complete category and file names.

| Category | Folder |
| --- | --- |
| `shop` | `shops/` |
| `menu` | `menus/common_menus/` |
| `shop_menu_template` | `menus/shop_menu_templates/` |
| `buy_more_menu` | `menus/buy_more_menus/` |
| `favourite_menu` | `menus/favourite_menus/` |
| `search_menu` | `menus/search_menus/` |
| `item_sell_menu` | `menus/item_sell_menus/` |

To create a new menu, copy a bundled example into the matching folder and open the copy. See [Configuration files](../info/configuration-files.md) for other folders and migration from older versions.

## Change a value

Hover over a field to see what each click does. Click a toggle to turn it on or off. Click a field with fixed choices, such as a product's price mode or a menu's shared UI style, to cycle through its options. To set the default purchase amount to 32, hover over the field, press your Drop key (**Q** by default), then type `32` in chat.

Open a list to add, edit or remove individual entries. For a reset time, choose the mode first. For example, use `TIMER` with `00:15:00` to reset 15 minutes after a trade. Use `TIMED` with `19:00:00` for a daily reset at 7 p.m.

Common fields have their own buttons. If you can't find a setting, open **Raw Section** to browse or add keys in the same structure as the YAML file.

## Find layout and button settings

- **Inventory menus.** Use `size` and `layout` in the menu file to change its size and slots. In a Buy More menu, edit quantity buttons under `amount-items` and confirmation buttons under `confirm-items`.
- **Dialog, Bedrock forms and ArcMenu.** Enable these in the menu file with `dialog.enabled`, `bedrock.enabled` and `arcmenu.enabled`. Choose the Dialog layout with `dialog.layout`. Open `ui` to edit Ore and ArcMenu layouts, styles and colors.
- **One product or button.** Use `menu-visibility` to choose where it appears and `dialog.show-sprite` to show its item icon in Dialog. To change its ArcMenu position, font size or alignment, open its own `ui` section.
- **Right-clicking a product in ArcMenu.** Choose the action in `ui.renderers.arcmenu.right-click-action`. The default, `select-amount`, opens the product details. The inventory quantity buttons are edited in the Buy More menu file.

To give several menus the same look, open the shared UI style from **Configuration**. Global defaults live under `menu.ui` in `config.yml`, and individual menus can override them. See [Unified UI and ArcMenu](../menus/arcmenu-menus-premium.md) for layout examples.

## Apply your changes

The editor writes each successful change to the file. When you're finished, click **Reload Plugin** or run `/shop reload`, then reopen the menu to see the result.

If a save fails because the file changed while you were editing, go back to the file list, reopen the file and enter your change again.
