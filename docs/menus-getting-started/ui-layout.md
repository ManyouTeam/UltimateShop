# 📐 Graphical UI Layout — Premium

Ore and ArcMenu share graphical styles, a logical canvas, named areas and entry placements. These settings position graphical controls; Inventory uses root `layout`, and native Form/Dialog use their own controls.

Common/Shop support both graphical renderers. Search/Favourite support ArcMenu; Buy More and physical selling screens do not use this canvas. See [UI Modes](../menus/ui-overview.md#supported-menu-types).

## Start with a shared style

Select global `menu.ui.style` in `config.yml`, or menu-root `ui.style`. Bundled `classic` uses left navigation; `bedrock` uses a top navigation bar. Both provide named `sidebar`, `content` and `footer` areas. Style files live under `plugins/UltimateShop/ui/` and contain UI fields directly, without a wrapping `ui` section.

This complete Common menu uses the inherited canvas:

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
ui:
  style: classic
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
      appearance: button
      placement:
        area: sidebar
        order: 0
```

Create the target shop. For Ore, enable global Dialog, reload and [generate/distribute its pack](../menus/ore-menus-premium.md#generate-the-resource-pack). For ArcMenu, enable the global/menu ArcMenu switches with a compatible integration. The menu's root Inventory layout does not select graphical coordinates.

For shop product cards, the inherited placement is normally:

```yaml
ui:
  products:
    placement:
      area: content
```

## Configuration scopes

| Scope | Configuration |
| --- | --- |
| Shared style file | `ui/<style>.yml`: `layout`, `products`, `theme`, `text`, `renderers` |
| Global graphical defaults | `config.yml` → `menu.ui` |
| Per-menu overrides | Menu root `ui`; for a shop, also `settings.menu-settings.ui` |
| Individual product actions/price | Product root `ui.actions`, `ui.price` |
| Individual entry placement | Product/button root `ui.placement` |
| Ordinary entry appearance | Entry `ui.appearance`, `ui.font-size`, `ui.text-align` |

In normal selection, explicit menu fields win over selected defaults. A different per-menu style overlays the global defaults; missing fields are filled recursively. Forced style selection can replace local defaults. See [Configuration & Inheritance](../menus/menu-configuration.md).

Graphical product action/price lookup is product → owning shop menu → global defaults. Card appearance is menu-wide. Native Form price lines use `bedrock.price-extra-line`, independently of graphical prices.

## Canvas and areas

This menu fragment shows a complete classic-style canvas definition:

```yaml
ui:
  layout:
    canvas:
      width: 648
      height: 405
    header:
      title:
        enabled: true
        text: '{title}'
        x: 18
        y: 9
        width: 570
        height: 27
        align: left
        font-size: 1.3
      content:
        enabled: true
        text: '{content}'
        x: 18
        y: 36
        width: 570
        height: 27
        align: left
        font-size: 0.8
    areas:
      sidebar:
        type: stack
        direction: vertical
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

Style sections merge recursively. Editing `header.title` does not remove other named headers inherited from a style; set unwanted header entries to `enabled: false`.

| Path relative to `ui.layout` | Purpose |
| --- | --- |
| `canvas.width`, `canvas.height` | Logical dimensions: width `144–900`, height `144–540` |
| `header.<id>.enabled/text` | Named header visibility and text |
| `header.<id>.x/y/width/height` | Header bounds; default height `27` |
| `header.<id>.align` | `left`, `center` or `right` |
| `header.<id>.font-size`, `bold` | ArcMenu text scale and bold styling |
| `areas.<id>.type` | `grid`, `stack` or `free` |
| `areas.<id>.x/y/width/height` | Area bounds on the canvas |
| `areas.<id>.columns/rows` | Grid dimensions: columns `1–36`, rows `1–60` |
| `areas.<id>.cell-height` | Grid cell height; width derives from area width/columns/gaps |
| `areas.<id>.direction` | Stack direction: `vertical` (default) or `horizontal` |
| `areas.<id>.item-width` | Horizontal stack entry width; default `108` |
| `areas.<id>.item-height` | Stack entry height; default `36` |
| `areas.<id>.gap` | Fallback for horizontal and vertical gaps |
| `areas.<id>.gap-x/gap-y` | Individual gap overrides |
| `areas.<id>.background-enabled/background-padding` | ArcMenu area backdrop and padding |
| `pagination.enabled` | Graphical page controls |
| `pagination.previous/indicator/next` | Each supports `enabled`, `x`, `y`, `width`, `height` |

Headers can replace `{title}` and `{content}` and use normal language/PlaceholderAPI text. Ore description comes from `dialog.content`, ArcMenu from `arcmenu.content`, then local root `content`.

## Area behavior and pagination

**Grid** fills cells in order, reserving explicitly assigned row/column cells first. A `3 × 2` area holds six entries per page. Additional grid entries produce further pages; row/column indices start at zero, and rows beyond the first page identify later pages. Keep columns within the configured grid width.

**Stack** lays out entries in `placement.order`, vertically or horizontally. It does not create extra pages; all entries must fit. The bundled Bedrock style's horizontal `sidebar` holds five navigation buttons and remains visible while grid pages change.

**Free** uses offsets inside the area's bounds. Supply both `placement.x` and `placement.y`. Default free-entry size is `108 × 27`; larger entries still need to fit the area.

Canvas height, all element Y coordinates/heights, and vertical gaps must be multiples of 9. Bounds must stay inside their parent. Rectangles require width at least 24 and height at least 9; non-free cells require height at least 27. Every `appearance: card` requires height at least 99. Visible entries must not overlap each other, headers or active pagination controls.

Search/Favourite root result-slot counts limit source entries before graphical arrangement. A larger grid or more graphical pages does not expand that source capacity.

## Entry placements

```yaml
buttons:
  search:
    display-item:
      material: HOPPER
      name: '&eSearch'
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
      name: '&cClose'
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

The target Search menu must exist and requires Premium. The Close button is positioned relative to the footer's origin. Both controls can appear graphically without root layout markers.

| Entry key | Purpose |
| --- | --- |
| `ui.placement.enabled` | Default `true` when placement exists; `false` disables graphical placement |
| `ui.placement.area` | Named area ID |
| `ui.placement.order` | Sort priority; defaults to source slot |
| `ui.placement.row/column` | Explicit grid cell; a configured row enables explicit-cell placement |
| `ui.placement.x/y` | Free-area offsets |
| `ui.placement.width/height` | Entry dimension overrides |
| `ui.appearance` | `button` or `card`; products default to cards |
| `ui.font-size` | Ordinary ArcMenu button font scale |
| `ui.text-align` | Ordinary graphical button text alignment |
| `menu-visibility.ore/arcmenu` | Presentation visibility at entry root |

Ordinary entries need their own placement; products inherit `ui.products.placement` and merge their own overrides over it. `appearance: card` can also create category/navigation cards. Neither an empty sidebar nor an action automatically generates a navigation control.

## Product cards, prices and actions

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

| Path relative to `ui.products` | Purpose |
| --- | --- |
| `card.icon-size`, `padding` | Item icon size and content padding |
| `card.name-size/price-size` | ArcMenu product name/price font scales |
| `card.button-font-size/button-height` | ArcMenu operation button text and height |
| `card.buttons.inset-x/inset-bottom/gap` | ArcMenu operation button placement |
| `card.lore-lines` | Maximum Ore product body lore lines |
| `card.description-lines` | Maximum Ore category description lines; ArcMenu fits descriptions to available space |
| `price.enabled` | Compact product price line |
| `price.formats.both/only-buy/only-sell` | Format for the product's available trade directions |
| `actions.primary` | Ordered preferred IDs; at most two available primary operations |
| `actions.extra` | `auto` for remaining operations, an explicit list, or `[]` for none |
| `actions.labels.<id>` | Operation label; supports `{item-name}` and normal text placeholders |

Operation IDs come from product click bindings and custom `menu.click-event-actions`. Listing `buy` does not create a missing binding. Prices, product options and custom buy-only/sell-only restrictions filter available actions.

Products can override `ui.price` and `ui.actions` at product root; card styling remains menu-wide. Compact price text represents one trade unit and uses shared dynamic price/multiplier calculation. Information/details use their applicable quantities. Original lore and applicable generated lore remain available to information/tooltips. Use [lore presentation conditions](../menus/display-item-add-lore.md#presentation-conditions) for different instructions.

## Theme and text

Quote colors in YAML. ArcMenu accepts `#RRGGBB` or `#AARRGGBB`; Ore's generated RGB textures discard alpha.

| Path relative to `ui.theme` | Purpose |
| --- | --- |
| `background-enabled`, `background` | ArcMenu window backdrop |
| `sidebar`, `card`, `border`, `accent`, `text` | Shared panel/card/accent colors and ArcMenu base text |
| `button.background/hover/border/text-align` | Ordinary graphical controls |
| `buy.background/hover`, `sell.background/hover` | Trade controls |
| `title.background-enabled/background/decoration-enabled` | Independent ArcMenu title background and decoration |
| `decorations.enabled/style/color/corner-color` | ArcMenu frame/corner styling |
| `decorations.thickness/corner-thickness/corner-length/inset` | Decoration dimensions |
| `decorations.dividers-enabled` | ArcMenu header accent |
| `bevel.enabled/shadow/top/left/right/bottom` | ArcMenu beveled control defaults |
| `tooltip.border/background` | ArcMenu hover panel colors |
| `details.background`, `more-menu.background` | ArcMenu details and popup colors |

ArcMenu control-specific theme sections such as `buy`, `sell`, `quantity`, `pagination` and `cancel` can supply their own `text` and bevel colors. Bevel colors are composited against their control's base color.

A title background can be enabled separately from the window backdrop:

```yaml
ui:
  theme:
    background-enabled: false
    title:
      background-enabled: true
      background: '#EF0C1223'
      decoration-enabled: true
    decorations:
      enabled: true
      style: corners
      color: '#65E8EF'
      corner-color: '#65E8EF'
      thickness: 2
      corner-thickness: 2
      corner-length: 20
      inset: 10
      dividers-enabled: true
```

Decoration `style` accepts `frame`, `corners`, `both` or `none`. `none` adds no outer frame while allowing the header accent. Supported dimensions are thickness `0.5–6`, corner thickness `0.5–8`, corner length `0–80`, inset `0–30`. Title backgrounds/decorations follow the configured title-header bounds.

Graphical labels live in `ui.text`: `cancel`, `no-extra`, `amount-input`, `select-product` and `favourite-edit.forward/backward/remove`. Language references can localize them.

## Renderer-specific settings

Ore texture templates belong in `ui.renderers.ore.templates`; ArcMenu interaction, animation and tooltip options belong in `ui.renderers.arcmenu`. ArcMenu refresh `update-interval` is global only. Shared geometry does not promise identical font rendering or card bodies across the two renderers.

[Ore](../menus/ore-menus-premium.md) · [ArcMenu](../menus/arcmenu-menus-premium.md) · [Configuration Compatibility](../menus/migration-guide.md)
