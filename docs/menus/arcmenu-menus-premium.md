# ✨Unified UI and ArcMenu - Premium

ArcMenu and Ore share a graphical layout model. Common, shop, search and favourite menus keep their existing buttons, products, conditions and actions; `ui.layout` and placements determine their graphical positions. Inventory slots and Bedrock Form lists keep their own presentation rules.

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

## Configuration scopes

| Scope | Location |
| --- | --- |
| Global UI defaults | `config.yml`: `menu.ui` |
| Menu overrides | Menu root `ui`, including shop `settings.menu-settings.ui` |
| Product action/price overrides | Product root `ui.actions`, `ui.price` |
| Product/button graphical position | Product/button root `ui.placement` |
| Ordinary button appearance | Button root `ui.appearance`, `ui.font-size`, `ui.text-align` |

Menu values inherit missing global values recursively. Product action/price lookup uses product > owning shop menu > global defaults. Card styling is menu-wide. A product shown in search/favourites keeps its product settings and trade rules. Bundled menus contain explicit UI values, so inspect the menu when a global edit appears ineffective.

Root `title` and `content` provide shared text. Headers can reference `{title}` and `{content}`, language references and PlaceholderAPI. Start from the current bundled main/shop/search/favourite example; removed graphical configuration formats are not automatically migrated.

## Shared layout

```yaml
ui:
  layout:
    canvas:
      width: 648
      height: 414
    header:
      title:
        enabled: true
        text: '{title}'
        x: 18
        y: 9
        width: 570
        align: left
        font-size: 1.3
      content:
        enabled: true
        text: '{content}'
        x: 18
        y: 36
        width: 570
        align: left
        font-size: 0.8
    areas:
      sidebar:
        type: stack
        x: 18
        y: 90
        width: 108
        height: 270
        item-height: 36
        gap: 9
      content:
        type: grid
        x: 144
        y: 90
        width: 486
        height: 270
        columns: 3
        rows: 2
        cell-height: 126
        gap-x: 9
        gap-y: 9
      footer:
        type: free
        x: 18
        y: 369
        width: 612
        height: 27
    pagination:
      enabled: true
      previous:
        x: 297
        y: 369
        width: 36
        height: 27
      indicator:
        x: 342
        y: 369
        width: 90
        height: 27
      next:
        x: 441
        y: 369
        width: 36
        height: 27
```

### Canvas, headers and areas

| Path relative to `ui.layout` | Meaning |
| --- | --- |
| `canvas.width`, `canvas.height` | Logical canvas dimensions; width 144–900, height 144–540; height is a multiple of 9 |
| `header.<id>` | Named text header; any descriptive ID may be used |
| `header.<id>.enabled` | Whether to render this header |
| `header.<id>.text` | Text, with `{title}`/`{content}` replacement |
| `header.<id>.x`, `y`, `width` | Canvas position and text width; header layout occupies a 27-high area |
| `header.<id>.align` | `left`, `center`, `right` |
| `header.<id>.font-size` | ArcMenu text scale; Ore uses native font rendering |
| `areas.<id>.type` | `grid`, `stack` or `free` |
| `areas.<id>.x`, `y`, `width`, `height` | Named area's bounds within the canvas |
| `areas.<id>.columns`, `rows` | Grid dimensions; columns 1–36, rows 1–60 |
| `areas.<id>.cell-height` | Grid cell height; width is calculated from area width, columns and gaps |
| `areas.<id>.item-height` | Stack entry height; default 36 |
| `areas.<id>.gap` | Fallback for both horizontal and vertical gaps |
| `areas.<id>.gap-x`, `gap-y` | Override the respective gap |
| `pagination.enabled` | Enable graphical pagination controls |
| `pagination.previous`, `indicator`, `next` | Each supports `enabled`, `x`, `y`, `width`, `height` |

Grid entries fill cells and paginate by area capacity. Explicit row/column cells are reserved before automatic placement. Stack entries sort by `placement.order` and stack vertically; free entries use coordinates within the area. Row/column indices start at 0.

Use multiples of 9 for vertical coordinates, heights and vertical gaps. Non-free cells must be at least 24 wide and 27 high; all entries with `appearance: card` must be at least 99 high, including category cards. Keep headers, pagination and actual entries within bounds and free of overlap. Errors are reported through ErrorManager. Increasing canvas size does not automatically redesign ArcMenu's product details panel, whose internal coordinates are fixed by the renderer.

### Entry placements

```yaml
buttons:
  search:
    display-item:
      material: HOPPER
      name: 'Search'
    actions:
      1:
        type: open_menu
        menu: search
    ui:
      appearance: button
      placement:
        area: sidebar
        order: 0
      font-size: 1.0
      text-align: left
  close:
    display-item:
      material: BARRIER
      name: 'Close'
    actions:
      1:
        type: close
    ui:
      placement:
        area: footer
        x: 504
        y: 0
        width: 108
        height: 27
```

| Entry key | Meaning |
| --- | --- |
| `ui.placement.enabled` | Default true when placement exists; false disables this graphical entry |
| `ui.placement.area` | Named area from `ui.layout.areas` |
| `ui.placement.order` | Sort order for stack/automatic placement; defaults to source slot |
| `ui.placement.row`, `column` | Optional explicit grid cell; zero-based |
| `ui.placement.x`, `y` | Offset relative to a free area's origin |
| `ui.placement.width`, `height` | Override entry dimensions; free entries otherwise default to 108 × 27 |
| `ui.appearance` | `button` or `card`; products use cards by default |
| `ui.font-size` | Ordinary ArcMenu button font scale |
| `ui.text-align` | Ordinary graphical button alignment |
| `ui.visible-in` | Presentation filter; use `ore` and/or `arcmenu` |

Ordinary buttons without a graphical placement are not displayed. The renderer does not generate Back/Close or infer a sidebar from actions. Root `layout` identifies Inventory/list entries. Graphical menu collection can also include explicitly placed ordinary buttons/products that are absent from the root slot layout. Product placement defaults to `ui.products.placement`; a product's own `ui.placement` overrides it. `appearance: card` also supports category/navigation cards.

## Shared product settings

```yaml
ui:
  products:
    placement:
      area: content
    card:
      icon-size: 32
      padding: 9
      name-size: 1.0
      price-size: 0.85
      button-font-size: 1.0
      button-height: 27
      buttons:
        inset-x: 6
        inset-bottom: 6
        gap: 3
      price-lines: 1
      lore-lines: 6
      description-lines: 2
    price:
      enabled: true
      formats:
        both: '&6Buy: {buy-price} | Sell: {sell-price}'
        only-buy: '&6Buy: {buy-price}'
        only-sell: '&6Sell: {sell-price}'
    actions:
      primary: [buy, sell]
      extra: auto
      labels:
        buy: 'Buy'
        sell: 'Sell'
```

| Path relative to `ui.products` | Meaning |
| --- | --- |
| `placement` | Default product area/placement, using the entry keys above |
| `card.icon-size` | Material/item icon size |
| `card.padding` | Card content padding |
| `card.name-size`, `price-size` | ArcMenu name and compact price font scales |
| `card.button-font-size`, `button-height` | ArcMenu operation button font and height |
| `card.buttons.inset-x`, `inset-bottom`, `gap` | ArcMenu operation button positioning |
| `card.price-lines` | Present in bundled defaults; current ArcMenu card code renders the compact price directly and does not read this limit |
| `card.lore-lines` | Maximum product lore lines in Ore card bodies |
| `card.description-lines` | Maximum Ore category-card description lines; ArcMenu calculates fitting lines from card space |
| `price.enabled` | Enable the compact product price line |
| `price.formats.both`, `only-buy`, `only-sell` | Price format by available trade direction; `{buy-price}` and `{sell-price}` |
| `actions.primary` | Ordered action IDs, at most two available primary actions |
| `actions.extra` | `auto` for remaining actions, explicit list to filter, or `[]` for none |
| `actions.labels.<action-id>` | Label override; supports `{item-name}` and normal text placeholders |

Actions come from `menu.click-event` or product click overrides, with custom definitions in `menu.click-event-actions`. Listing an ID in `actions.primary` does not create an action without a binding. Buy/sell, quantity, sell-all and favourite availability is filtered by the product. Custom `buy-only`/`sell-only` restrictions also apply.

Price text reuses dynamic price calculation and sell multiplier display. Compact prices represent one trade unit; information/detail content uses the applicable quantities. Original item lore and generated `add-lore` are retained in product information/tooltips. Presentation conditions can target `arcmenu`, `arcmenu-hover`, `ore` and `ore-hover`; see [Add Lore](display-item-add-lore.md). If there is no lore, tooltip content can fall back to the compact price line.

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
| `update-interval` | 20 ticks; periodic refresh interval, currently read globally with a minimum of 5 ticks |
| `animation.enabled` | true; menu opening animation |
| `animation.duration` | 8; animation steps, clamped to 1–40 |
| `more-menu.columns` | 1; extra-action popup columns, clamped to 1–2 |
| `tooltip.enabled` | true; hover tooltip |
| `tooltip.width` | 260; clamped to 100–450 |
| `tooltip.font-size` | 0.9; clamped to 0.5–1.2 |
| `tooltip.padding` | 8; clamped to 4–20 |

Some old bundled menu copies contain `details.enabled`. The current renderer does not read it; it does not disable details. Do not rely on unused keys simply because they appear in an older file.

ArcMenu card numeric settings are clamped: icon size 12–38, padding 3–16, name scale 0.5–1.2, price scale 0.5–1.0, operation font 0.5–1.5, button height 18–36, horizontal inset 2–16, bottom inset 3–16, button gap 2–12. Enlarged fonts still need sufficient width; long text can be truncated.

## Theme and interface text

`ui.theme` accepts `#RRGGBB` or ArcMenu `#AARRGGBB` strings; quote values in YAML. Ore's generated templates use RGB and drop the alpha channel.

| Theme key | Element |
| --- | --- |
| `background` | Overall ArcMenu background |
| `sidebar` | Sidebar background |
| `card` | Card background |
| `border` | Card border |
| `accent` | Selection/highlight accent |
| `text` | ArcMenu base text colour |
| `button.background`, `button.hover`, `button.border` | Ordinary controls |
| `buy.background`, `buy.hover` | Buy controls |
| `sell.background`, `sell.hover` | Sell controls |

`ui.text.cancel` and `ui.text.no-extra` label the extra-action popup. `ui.text.favourite-edit.forward`, `backward`, `remove` override favourite card editing labels. `ui.text.select-product` is the renderer's unselected-product hint. Use language references for translated labels.

## Search and favourite menus

ArcMenu uses the corresponding file's title, content, conditions, ordinary buttons, result lore and result capacity. Configure `ui.layout`, `ui.products.placement` and special-button placements in that file. Graphical pagination lays out the entries selected for the view; root result slots still limit its result list.

Search supports name keywords through chat input, clear-search, action/state items and a no-result item. It preserves the keyword when returning from a result. Physical Inventory input slots are not an ArcMenu item filter. Favourite normal mode uses product actions/details. Edit mode offers forward/backward/remove buttons per card, and retains left/right/middle edit clicks. Data and ordering are shared with other presentations.

## Names, tooltips and Ore resources

Custom item names take precedence. The shared item-name path can return vanilla translation components. ArcMenu resolves available Minecraft translations for the player's locale through the resource manager; Ore retains native translatable components for the client. Ore's tooltip adds the item name once. The old separate item-name download flags are not part of this configuration.

Ore uses special positive/negative-width characters to place text. Translatable components are atomic; their server-side width estimate is not the final translated width in every client language/font. Keep adequate text width when choosing a layout. The full Ore renderer and template configuration is on [Dialog and Ore](dialog-menus-premium.md).

After changing Ore layout/theme/templates, run `/shop dialogpack` and load the new pack. ArcMenu mouse mode reads ArcMenu's `mouse.cursor.z` to keep menu layers and tooltips behind its cursor without changing that cursor setting.
