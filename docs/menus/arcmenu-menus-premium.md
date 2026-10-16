# ✨ ArcMenu UI - Premium

ArcMenu renders Common, Shop, Search and Favourite menus as graphical Java-client menus, using existing products, conditions and actions. Ore uses the same [graphical layout](ui-layout.md) with a different renderer.

## Enable ArcMenu

This integration targets ArcMenu. Install the compatible ArcMenu server plugin and its required client resources. Without the integration, the menu falls back to Inventory.

```yaml
# config.yml
menu:
  ui:
    mode:
      java: arcmenu
      bedrock: form
```

A menu can override `ui.mode.java: arcmenu` or use `inherit`. Use `ore` instead for the graphical Dialog presentation; Ore additionally needs supported Paper and a generated resource pack. Item Sell/Sell All are Inventory deposit interfaces. ArcMenu does not render the Inventory Buy More menu as a separate screen.

<a id="shared-layout"></a>

<a id="configuration-scopes"></a>
<a id="canvas-headers-and-areas"></a>
<a id="entry-placements"></a>
<a id="shared-product-settings"></a>
<a id="theme-and-interface-text"></a>

## Shared layout and product styling

The complete `ui.layout`, `ui.placement`, `ui.products` and `ui.theme` reference has moved to [Graphical UI Layout](ui-layout.md). It covers canvas, headers, areas, pagination, buttons, cards, prices and actions without duplicating the same settings here.

## ArcMenu clicks, details and extra actions

Product card operation buttons execute their selected action. Clicking `…` opens an extra-action popup with pagination when needed; Cancel closes it. While the popup is open, background clicks and scrolling do not perform menu actions.

Right-click on a shop product uses `ui.renderers.arcmenu.right-click-action`, default `select-amount`. It may be `buy`, `sell`, `sell-all`, `none` or a configured custom action. Other card clicks use the normal click bindings, while favourite editing retains its edit controls.

`select-amount` opens ArcMenu product details directly. Opening details requires the product's Buy More feature and a corresponding menu. Quantity adjustment additionally requires an available `select-amount` binding. Plus/minus and scrolling adjust the amount within 1 through `buy-more-menu.max-amount`. Until it is adjusted, buy and sell retain their separate default amounts. Card buttons outside the details panel use product defaults.

ArcMenu does not read Buy More `layout`, `amount-items`, `add-amount`, `set-amount` or confirmation-button positions for this panel. Ore and native Dialog use their quantity input/information flow instead.

## ArcMenu renderer reference

Global prefix: `menu.ui.renderers.arcmenu`. Menu prefix: `ui.renderers.arcmenu`.

| Relative key | Default / effect |
| --- | --- |
| `right-click-action` | `select-amount`; product right-click action |
| `screen-scale` | Bundled default 1.35; overall menu scale |
| `update-interval` | 20 ticks; periodic refresh interval, read globally with a minimum of 5 ticks |
| `animation.enabled` | true; menu opening animation |
| `animation.duration` | 8; animation steps, clamped to 1–40 |
| `more-menu.columns` | 1; extra-action popup columns, clamped to 1–2 |
| `tooltip.enabled` | true; hover tooltip |
| `tooltip.width` | 260; clamped to 100–450 |
| `tooltip.font-size` | 0.9; clamped to 0.5–1.2 |
| `tooltip.padding` | 8; clamped to 4–20 |

`details.enabled` does not control the ArcMenu details panel. Use the documented renderer settings rather than relying on unrelated keys copied from example files.

ArcMenu card numeric settings are clamped: icon size 12–38, padding 3–16, name scale 0.5–1.2, price scale 0.5–1.0, operation font 0.5–1.5, button height 18–36, horizontal inset 2–16, bottom inset 3–16, button gap 2–12. Enlarged fonts still need sufficient width; long text can be truncated.

## Search and favourite menus

ArcMenu uses the corresponding file's title, content, conditions, ordinary buttons, result lore and result capacity. Configure `ui.layout`, `ui.products.placement` and special-button placements in that file. Graphical pagination lays out the entries selected for the view; root result slots still limit its result list.

Search supports name keywords through chat input, clear-search, action/state items and a no-result item. It preserves the keyword when returning from a result. Physical Inventory input slots are not an ArcMenu item filter. Favourite normal mode uses product actions/details. Edit mode offers forward/backward/remove buttons per card, and retains left/right/middle edit clicks. Data and ordering are shared with other presentations.

## Names, tooltips and Ore resources

Custom item names take precedence. The shared item-name path can return vanilla translation components. ArcMenu resolves available Minecraft translations for the player's locale through the resource manager; Ore retains native translatable components for the client. Ore's tooltip adds the item name once. Separate item-name download flags are not ArcMenu configuration options.

Ore uses special positive/negative-width characters to place text. Translatable components are atomic; their server-side width estimate is not the final translated width in every client language/font. Keep adequate text width when choosing a layout. Read [Ore UI](ore-menus-premium.md) for Ore-specific templates and resources.

After changing Ore layout/theme/templates, run `/shop dialogpack` and load the new pack. ArcMenu mouse mode reads ArcMenu's `mouse.cursor.z` to keep menu layers and tooltips behind its cursor without changing that cursor setting.

## Troubleshooting

If it falls back to Inventory, check Premium, a compatible ArcMenu integration/client setup, the selected `ui.mode.java` and supported menu type. Item Sell and Sell All are physical inventories; ArcMenu quantity selection uses its details panel, not the Inventory Buy More layout. See [Troubleshooting](troubleshooting.md).
