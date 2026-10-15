# 🔲General Menus

A menu defines what players see and how they navigate. A shop defines products, prices and trade rules. The same shop can use Inventory, Bedrock Form, native Dialog, Ore or ArcMenu without duplicating its products.

This chapter documents the current bundled configuration. Start from the examples shipped with your build when migrating: old root `bedrock`/`dialog` settings and old graphical layouts are not automatically converted into the unified `ui` format.

## Menu types and files

All paths below are relative to `plugins/UltimateShop/`. The file name without `.yml` is the menu ID. Keep IDs unique across the folders used by `/shop menu`.

| Type | Location | Purpose and entry point |
| --- | --- | --- |
| Common | `menus/common_menus/` | Navigation and custom action buttons; `/shop menu <id>` or `open_menu` |
| Shop | `menus/shop_menu_templates/` | Products from the shop that opens it; shop configuration `settings.menu`, `/shop menu <shop>` or `shop_menu` |
| Buy More | `menus/buy_more_menus/` | Quantity selection for an already selected product; `select-amount` or `buy_more_menu` action |
| Search (Premium) | `menus/search_menus/` | Search visible products; `/shop menu <id>` |
| Favourite (Premium) | `menus/favourite_menus/` | Saved products and editing; `/shop menu <id>` |
| Item Sell | `menus/item_sell_menus/` | Place physical items, preview value, confirm sale; `/shop menu <id>` |
| Sell All | `config.yml`: `menu.sell-all` | Deposit items and sell on close; `/shop sellall` |

Search, Favourite and Item Sell files in their dedicated folders are loaded as that type. Files in `common_menus` can instead declare `menu-type: search`, `favourite` (alias `favorite`) or `item-sell` (alias `item_sell`). The legacy `menu.search-gui.menu` and `menu.favourite-gui.menu` lists also classify common-folder files. Shop templates and Buy More files need a shop/product context; they are not standalone common menus.

## Choose the presentation

Defaults in `config.yml`:

```yaml
menu:
  ui:
    mode:
      java: inventory
      bedrock: form
```

Overrides in any menu file:

```yaml
ui:
  mode:
    java: inherit
    bedrock: inherit
```

| Client | Modes | Requirements |
| --- | --- | --- |
| Java | `inventory` | Standard chest interface |
| Java | `dialog` | Premium and supported Paper Dialog API; native multi-action or item-action-list |
| Java | `ore` | Premium, supported Paper Dialog API and generated Ore resource pack |
| Java | `arcmenu` | Premium, compatible ArcMenu plugin and client resources |
| Bedrock | `inventory`, `form` | Form requires Premium and working Bedrock/Floodgate integration |

`inherit` uses the global choice. Invalid mode values report an error and resolve to Inventory. Unsupported integrations/menu types also use their supported fallback. Item Sell and Sell All require Inventory because players deposit real items; choosing Form or ArcMenu does not turn them into graphical deposit screens.

Use [Bedrock](bedrock-menus-premium.md), [Dialog/Ore](dialog-menus-premium.md) and [ArcMenu/shared graphical layout](arcmenu-menus-premium.md) for renderer options. Shared settings live in `menu.ui` globally and root `ui` in menu files. Missing menu settings inherit global values recursively. Bundled menus contain many explicit overrides: changing a global value does not replace an explicit menu value.

## Create a common menu

Save as `menus/common_menus/main.yml`:

```yaml
title: '&8Categories'
content: 'Choose a category.'
size: 18
layout:
  - '000000000'
  - '000A0X000'
buttons:
  A:
    display-item:
      material: WHEAT
      name: '&aFarming'
      bedrock:
        icon: 'path;;textures/items/wheat.png'
        extra-line: '&7Open the farming shop'
    actions:
      1:
        type: shop_menu
        shop: farming
  X:
    display-item:
      material: BARRIER
      name: '&cClose'
    actions:
      1:
        type: close
ui:
  mode:
    java: inventory
    bedrock: form
```

Open it with `/shop menu main` (`ultimateshop.menu`). Console can use `/shop menu main <player>`. `/shop menu <id> -b` bypasses opening conditions for Premium administrators with `ultimateshop.menu.admin`; console can also append `-b`. If a shop and standalone menu share the same ID, this command resolves the shop first. `0` is empty here because no button has that ID. Define button `0` if you want a decorative filler. Form omits blank-name fillers. To use this menu in Ore/ArcMenu, add graphical placements as described on the graphical layout page.

## Common menu configuration

| Key | Meaning |
| --- | --- |
| `menu-type` | Optional type selector for files in the common folder; dedicated folders already determine their type |
| `title` | Menu title; supports language references and placeholders; shop templates support `{shop-name}` |
| `content` | Shared body/header text for Form, Dialog and graphical headers; not a chest inventory slot |
| `size` | Inventory slots: `9`, `18`, `27`, `36`, `45`, `54` |
| `layout` | Ordered list of slot IDs; nine slots per row, matching `size`; also identifies entries/result capacity for list presentations |
| `dynamic-layout` | Premium; resolve placeholders and conditional alternatives when obtaining the layout; defaults to false |
| `buttons.<id>` | Ordinary buttons; use their IDs in the layout |
| `conditions` | Requirements to open the menu; [Condition Format](../format/condition-format.md) |
| `open-actions` | [Actions](../format/action-format.md) executed on opening; individual actions can use `open-once` |
| `close-actions` | Actions executed when leaving the menu, including navigation to another menu |
| `custom-command.name` | Premium; register a command for this common menu |
| `custom-command.description` | Description of that command; shop commands belong in the shop configuration |
| `ui.mode` | Client presentation selection |
| `ui.renderers` | Settings specific to Inventory, Form, Dialog, Ore or ArcMenu |
| `ui.layout`, `ui.theme`, `ui.text` | Graphical canvas/areas, appearance and interface labels |
| `ui.products` | Shared product actions, price text, card style and placement |

Special menu types add the keys documented on their own pages; do not place product prices in common menu buttons.

## Button configuration

| Key inside `buttons.<id>` | Meaning |
| --- | --- |
| `display-item` | Icon, name, lore and item metadata; [Display Item Format](../format/display-item-format.md) |
| `display-conditions` | Premium; whether this entry is displayed |
| `conditions` | Requirements checked when the player clicks |
| `actions` | Actions run when click conditions pass |
| `fail-actions` | Actions run when click conditions fail |
| `ui.visible-in` | Presentations allowed to show this entry |
| `ui.placement` | Graphical placement for Ore/ArcMenu |
| `ui.appearance` | Graphical `button` or `card` |
| `ui.font-size`, `ui.text-align` | Graphical button font scale and `left`/`center`/`right` alignment |
| `ui.show-sprite` | Override the native Dialog sprite choice |
| `ui.renderers.dialog.layout` | Per-button native Dialog layout override, including `item-action-list` |
| `display-item.bedrock` | Form icon, extra line and hiding; see the Bedrock reference |

A click condition does not hide a button. Use a display condition for visibility. Product entries have their own trade and click settings; see [Products](../shops/products.md) and [custom click events](../features/custom-click-event-premium.md).

## Shop templates and inline overrides

A shop in `shops/farming.yml` can select a reusable template:

```yaml
settings:
  shop-name: '&aFarming'
  menu: example-shop-menu
  buy-more: true
items:
  A:
    # Define the product, prices and display here.
```

`menus/shop_menu_templates/example-shop-menu.yml` then uses product ID `A` in its layout. Ordinary navigation buttons can be defined in the template or shop. The template provides presentation, while the opening shop provides products. Use `shop: farming` with the `shop_menu` action; `open_menu` opens a standalone menu ID.

For a menu exclusive to one shop, put menu settings under `settings.menu-settings` in the shop file. This also overrides values of a selected template:

```yaml
settings:
  shop-name: 'Farming'
  menu: example-shop-menu
  menu-settings:
    title: '{shop-name}'
    content: 'Fresh supplies'
    ui:
      mode:
        java: arcmenu
```

## Layout option

Each ordinary character is one slot. Rows are read left to right, top to bottom. Slots use zero-based numbering where a setting requests a slot number. For an 18-slot menu, provide two nine-slot rows.

Multi-character IDs use backtick delimiters and still occupy one slot:

```yaml
layout:
  - '000000000'
  - '0`food_buy``food_sell`000000'
```

That second row contains nine slots. Define `food_buy` and `food_sell` under `buttons`, or use matching shop product/sub-button IDs. IDs are case-sensitive. An undefined ID leaves an empty slot.

[Conditional display](conditional-display-premium.md) supports alternatives such as `` `A||B||C` ``. Premium dynamic layouts also support placeholder values:

```yaml
dynamic-layout: true
layout:
  - '000000000'
  - '0`{random_daily;;1}`0000000'
```

Keep the resolved slot count consistent with `size`. Graphical presentations use `ui.layout` and placements for coordinates; root `layout` still identifies menu entries and special markers.

## Button visibility by menu presentation

```yaml
ui:
  visible-in: [inventory, form, dialog, ore, arcmenu]
```

Set this inside a button/product, not at menu root. `dialog` includes native multi-action and item-action-list; `ore` is separate. An empty list hides an entry everywhere. Removing the list restores default visibility. The removed `menu-visibility` fallback is not read. Display conditions still apply. No dynamic layout is needed for `ui.visible-in`.

`ui.placement.enabled: false` disables graphical placement only. Bedrock's display-item `bedrock.hide` only hides the Form button. Lore presentation conditions are separate; see [Display Item Add Lore](display-item-add-lore.md).

## Global menu controls in config.yml

These settings are under `menu`, outside `menu.ui`:

| Path | Meaning |
| --- | --- |
| `auto-open.enabled` | Allow bare `/shop` to open the configured target |
| `auto-open.menu` | Menu/shop ID opened by bare `/shop`, normally `main` |
| `anti-dupe-checker` | Additional Inventory GUI item checks; see the shipped configuration for the default |
| `prompt.cancel-keyword` | Keyword to cancel chat input; `{lang}` uses the language setting |
| `cooldown.click` | Menu click cooldown in ticks, also used by ArcMenu; `-1` disables it |
| `cooldown.reopen` | Menu reopen cooldown in ticks, also used by ArcMenu; `-1` disables it |
| `ignore-click-outside` | Ignore Inventory clicks outside the window |
| `title-update.enabled` | Premium; enable Inventory title updates using PacketEvents on supported Paper |
| `title-update.circle-update` | Refresh title periodically |
| `title-update.click-update` | Refresh title on clicks |
| `title-update.resend-items-pack` | Resend items after title changes to reduce visual flashing |
| `menu-update.circle-update` | Refresh Inventory button display periodically |
| `menu-update.click-update` | Refresh Inventory button display on clicks |
| `sell-all` | Deposit-and-close sale configuration; [Sell All](sell-all-menu.md) |
| `search-gui.menu`, `search-gui.prompt.clear-keyword` | Legacy search classification list and chat clear keyword |
| `favourite-gui.menu` | Legacy favourite classification list |
| `buy-more-menu` | Default quantity menus and maximum amount; [Buy More](buy-more-menus.md) |
| `click-event` | Product action ID to Bukkit click type mapping; `NEVER` disables a binding |
| `click-event-actions` | Custom product action definitions, labels and buy/sell restrictions |

A typical binding section is:

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

A binding may contain several Bukkit click names separated by `;;`, for example `LEFT;;SHIFT_LEFT`. Custom IDs under `click-event-actions.<id>` define `display-name`, optional `buy-only`/`sell-only`, and numbered Action Format entries. Product action lists only expose custom operations that have an available click binding. Products and sub-buttons may override click bindings. Shared `ui.products.actions` selects which available actions appear in Form/Dialog/graphical interfaces; it does not create an unbound action. See [custom click events](../features/custom-click-event-premium.md) for `click-event-actions` definitions.

<a id="all-buttons-update"></a>
<a id="title-update-premium"></a>
<a id="title-update-premium-require-paper"></a>

Periodic refresh resolves dynamic text repeatedly and costs more work with many open menus. Keep it disabled unless needed; `use-times.auto-reset-mode` can handle stock-reset refresh separately. Inventory title refresh can flash on the client. ArcMenu refresh has its own renderer interval. Only `menu.menu-update.circle-update` and `menu.menu-update.click-update` control button refresh; removed `menu.shop` refresh aliases are not read.

`ui.renderers.inventory` is currently reserved and should stay empty (`{}`). Configure Inventory `size`, `layout` and `title` at menu root. Inheritance fills missing values only inside `ui`; renderer values are not copied into root menu fields.

## Editing and troubleshooting

The Premium [in-game editor](../features/in-game-editor-premium.md) exposes menu type and UI presets. You can also edit YAML and run `/shop reload`. Regenerate/reload the Ore resource pack after changing graphical assets or layout.

For a missing entry, check the ID, layout marker, display condition, `ui.visible-in`, Form blank-name/hide rules and graphical placement. For an unexpected renderer, check both global and per-menu modes and the type's supported interfaces. For unchanged appearance, check explicit settings in the menu before changing global defaults again.
