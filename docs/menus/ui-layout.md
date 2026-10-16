# 📐 Graphical UI Layout - Premium

Ore and ArcMenu share the `ui.layout` canvas/area model, `ui.products`, entry `ui.placement`, `ui.appearance` and `ui.theme`. Inventory uses the root slot `layout` instead, while Form and native Dialog use list rendering. Use [Ore - Premium](ore-menus-premium.md) or [ArcMenu - Premium](arcmenu-menus-premium.md) for renderer-specific behavior.

## First graphical menu

Global graphical defaults live under `config.yml` → `menu.ui`, including the named areas `sidebar` and `content`. The following menu uses those default areas and works as the starting point for **either** Ore or ArcMenu after enabling the chosen renderer:

```yaml
title: '&8Categories'
size: 9
layout:
  - '0000A0000'
dialog:
  enabled: true
  layout: ore
arcmenu:
  enabled: false
buttons:
  A:
    display-item:
      material: WHEAT
      name: '&aFarming'
    actions:
      1:
        type: shop_menu
        shop: farming
    ui:
      placement:
        area: sidebar
        order: 0
```

To select Ore, enable global `menu.dialog.enabled` and generate its resource pack. To select ArcMenu, enable both global and menu `arcmenu.enabled` with a compatible integration. The `farming` shop must exist. See [UI modes](ui-overview.md). For a shop's product cards, use:

```yaml
ui:
  products:
    placement:
      area: content
```

This inherits the rest of the graphical canvas/grid/card defaults rather than duplicating them. Override the full canvas only when you need custom sizes or areas.

## Configuration scopes

| Scope | Location |
| --- | --- |
| Global UI defaults | `config.yml`: `menu.ui` |
| Menu overrides | Menu root `ui`, including shop `settings.menu-settings.ui` |
| Product action/price overrides | Product root `ui.actions`, `ui.price` |
| Product/button graphical position | Product/button root `ui.placement` |
| Ordinary button appearance | Button root `ui.appearance`, `ui.font-size`, `ui.text-align` |

Menu values inherit missing global values recursively. Graphical product action/price lookup uses product > owning shop menu > global defaults. Form and native Dialog do not read these graphical presentation options. Card styling is menu-wide. A product shown in search/favourites keeps its product settings and trade rules. Bundled menus contain explicit UI values, so inspect the menu when a global edit appears ineffective.

Root `title` supplies the title. Ore uses `dialog.content` (then root `content`) for its header; ArcMenu uses `arcmenu.content` (then root `content`). Headers can reference `{title}` and `{content}`, language references and PlaceholderAPI. Start from the included main/shop/search/favourite menu examples. Other graphical layout formats are not automatically converted.

## Canvas, areas and pagination

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

Use multiples of 9 for vertical coordinates, heights and vertical gaps. Non-free cells must be at least 24 wide and 27 high; all entries with `appearance: card` must be at least 99 high, including category cards. Keep headers, pagination and actual entries within bounds and free of overlap. Errors are reported through ErrorManager. Increasing canvas size does not redesign ArcMenu's product details panel, whose internal coordinates are renderer-specific.

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
| `menu-visibility.ore`, `menu-visibility.arcmenu` | Boolean presentation filters; omitted values default to true |

Ordinary buttons without a graphical placement are not displayed. The renderer does not generate Back/Close or infer a sidebar from actions. Root `layout` identifies Inventory/list entries. Graphical menu collection can also include explicitly placed ordinary buttons/products that are absent from the root slot layout. Product placement defaults to `ui.products.placement`; a product's own `ui.placement` overrides it. `appearance: card` also supports category/navigation cards.

## Graphical product cards and actions

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
| `card.price-lines` | Available in the example configuration; ArcMenu renders compact prices without applying this limit |
| `card.lore-lines` | Maximum product lore lines in Ore card bodies |
| `card.description-lines` | Maximum Ore category-card description lines; ArcMenu calculates fitting lines from card space |
| `price.enabled` | Enable the compact product price line |
| `price.formats.both`, `only-buy`, `only-sell` | Price format by available trade direction; `{buy-price}` and `{sell-price}` |
| `actions.primary` | Ordered action IDs, at most two available primary actions |
| `actions.extra` | `auto` for remaining actions, explicit list to filter, or `[]` for none |
| `actions.labels.<action-id>` | Label override; supports `{item-name}` and normal text placeholders |

Actions come from `menu.click-event` or product click overrides, with custom definitions in `menu.click-event-actions`. Listing an ID in `actions.primary` does not create an action without a binding. Buy/sell, quantity, sell-all and favourite availability is filtered by the product. Custom `buy-only`/`sell-only` restrictions also apply.

Price text reuses dynamic price calculation and sell multiplier display. Compact prices represent one trade unit; information/detail content uses the applicable quantities. Original item lore and generated `add-lore` are retained in product information/tooltips. Presentation conditions can target `arcmenu`, `arcmenu-hover`, `ore` and `ore-hover`; see [Add Lore](display-item-add-lore.md). If there is no lore, tooltip content can fall back to the compact price line.

## Colors and graphical text

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

## Ore vs ArcMenu

ArcMenu uses compact product price text and adjustable font scales; Ore uses generated assets, native text rendering and card body lore. Shared keys do not imply identical visual behavior. ArcMenu does not apply `ui.products.card.price-lines` to its compact price text.

For entry-specific overrides, product `ui.actions` and `ui.price` take precedence over the owning shop menu and global `menu.ui.products`. Ordinary navigation entries need their own placements; a sidebar/back button is not automatically created.

[UI Modes](ui-overview.md) · [Ore - Premium](ore-menus-premium.md) · [ArcMenu - Premium](arcmenu-menus-premium.md) · [Layout and Buttons](layout-and-buttons.md)
