# 📐Graphical UI Layout — Premium

Ore and ArcMenu share the same graphical styles, logical canvas, named areas, and entry placement system. These settings only affect graphical renderers; Inventory uses root `layout`, while Bedrock Form and native Dialog use their own controls.

Common and Shop menus support both Ore and ArcMenu, Search and Favourite support ArcMenu, and Buy More / Item Sell do not use this canvas; see [UI Modes](../menus/ui-overview.md#supported-menu-types).

## Start from a Shared Style

Select a global `menu.ui.style` in `config.yml`, or `ui.style` at a menu's root. Built-in `classic` uses a left sidebar and `bedrock` uses a top bar; both provide `sidebar`, `content`, and `footer` areas. Style files live in `plugins/UltimateShop/ui/` and contain UI fields directly at the root (no outer `ui:` key).

Complete Common menu example using an inherited canvas:

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

Make sure the target shop exists. For Ore, enable global dialog, reload, and [generate/distribute the resource pack](../menus/ore-menus-premium.md#generate-the-resource-pack). For ArcMenu, install a compatible integration and enable global/menu switches. Note that root Inventory `layout` does not determine graphical coordinates.

Shop product cards usually inherit a shared area placement:

```yaml
ui:
  products:
    placement:
      area: content
```

## Configuration Scope

| Scope | Configuration |
| --- | --- |
| Shared style file | `ui/<style>.yml`: `layout`, `products`, `theme`, `text`, `renderers` |
| Global graphical defaults | `config.yml` → `menu.ui` |
| Menu overrides | Menu root `ui`; shops can also use `settings.menu-settings.ui` |
| Per-product actions/price | Product root `ui.actions`, `ui.price` |
| Per-entry placement | Product or button root `ui.placement` |
| Common entry appearance | Entry `ui.appearance`, `ui.font-size`, `ui.text-align` |

Under normal selection, explicit menu fields override style defaults, and switching styles overrides global defaults before filling missing fields recursively. Forced styles can override local defaults; see [Menu Configuration & Inheritance](../menus/menu-configuration.md).

Graphical product actions/prices resolve in order: product → owning shop menu → global defaults. Card styling applies menu-wide. Bedrock Form price lines use `bedrock.price-extra-line`, independent of graphical prices.

## Canvas and Areas

Full `classic`-style canvas snippet:

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

Because sections merge recursively, editing `header.title` does not remove other inherited headers; set `enabled: false` to hide unwanted headers.

| Path relative to `ui.layout` | Purpose |
| --- | --- |
| `canvas.width`, `canvas.height` | Logical dimensions: width `144–900`, height `144–540` |
| `header.<id>.enabled` / `text` | Visibility and text of a named header |
| `header.<id>.x` / `y` / `width` / `height` | Header bounds (default height `27`) |
| `header.<id>.align` | `left`, `center`, or `right` |
| `header.<id>.font-size`, `bold` | ArcMenu text scale and bold |
| `areas.<id>.type` | `grid`, `stack`, or `free` |
| `areas.<id>.x` / `y` / `width` / `height` | Area bounds within the canvas |
| `areas.<id>.columns` / `rows` | Grid columns `1–36`, rows `1–60` |
| `areas.<id>.cell-height` | Grid cell height (width is derived from area width, columns, and gap) |
| `areas.<id>.direction` | Stack direction: `vertical` (default) or `horizontal` |
| `areas.<id>.item-width` / `item-height` | Stack item size (defaults `108` / `36`) |
| `areas.<id>.gap`, `gap-x` / `gap-y` | Item spacing (`gap` is the fallback for both axes) |
| `areas.<id>.background-enabled` / `background-padding` | ArcMenu area background and padding |
| `pagination.enabled` | Enables graphical pagination controls |
| `pagination.previous` / `indicator` / `next` | Each supports `enabled`, `x`, `y`, `width`, `height` |

Headers support `{title}`, `{content}`, language strings, and PlaceholderAPI. `{content}` reads `dialog.content` in Ore, `arcmenu.content` in ArcMenu, and falls back to root `content`.

## Area Behaviour and Pagination

* **Grid**: places entries with explicit `row`/`column` first, then fills remaining cells sequentially. A `3 × 2` grid holds 6 entries per page and paginates overflow; row/column indices are zero-based (rows beyond page 1 spill onto later pages; columns must stay within `columns`).
* **Stack**: arranges entries by `placement.order` vertically or horizontally without pagination—all entries must fit. In the built-in `bedrock` preset, the horizontal `sidebar` holds 5 navigation buttons and stays visible across grid pages.
* **Free**: positions entries using offsets relative to the area origin; requires both `placement.x` and `placement.y` (default size `108 × 27`).

{% hint style="warning" %}
**Geometry rules**: canvas height, all Y coordinates/heights, and vertical gaps must be **multiples of 9**; bounds cannot exceed their parent area; minimum box size is `24 × 9`; non-free cells require height ≥ `27`; `appearance: card` requires height ≥ `99`. Visible entries must not overlap each other, headers, or enabled pagination controls.
{% endhint %}

Search and Favourite menus cap source entries by their root result-slot count before graphical layout runs; enlarging the grid or adding graphical pages does not increase source capacity.

## Entry Placement

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

Here `search` must exist (Premium) and `close` is positioned relative to `footer`. Both appear in graphical UIs even if omitted from root `layout`.

| Entry Key | Purpose |
| --- | --- |
| `ui.placement.enabled` | Defaults to `true` when placement is present; set `false` to disable |
| `ui.placement.area` | Target named area ID |
| `ui.placement.order` | Sort priority (defaults to source slot order) |
| `ui.placement.row` / `column` | Explicit grid cell (setting `row` enables explicit placement) |
| `ui.placement.x` / `y` | Free-area internal offset |
| `ui.placement.width` / `height` | Overrides entry size |
| `ui.appearance` | `button` or `card` (products default to `card`) |
| `ui.font-size` | ArcMenu common-button text scale |
| `ui.text-align` | Common graphical button text alignment |
| `menu-visibility.ore` / `arcmenu` | Per-entry graphical visibility |

Common entries need their own placement; products inherit `ui.products.placement` and merge their own `ui.placement`. `appearance: card` also works on category/navigation buttons.

## Product Cards, Price, and Actions

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
| `card.icon-size`, `padding` | Item icon size and card padding |
| `card.name-size` / `price-size` | ArcMenu product name and price text scale |
| `card.button-font-size` / `button-height` | ArcMenu action button text scale and height |
| `card.buttons.inset-x` / `inset-bottom` / `gap` | ArcMenu action button insets and gap |
| `card.lore-lines` | Maximum body lore lines on Ore cards |
| `card.description-lines` | Maximum Ore category description lines (ArcMenu adapts to space) |
| `price.enabled` | Enables the compact price line |
| `price.formats.both` / `only-buy` / `only-sell` | Price format chosen by available trade directions |
| `actions.primary` | Priority-ordered IDs (up to 2 available primary actions shown) |
| `actions.extra` | `auto` for remaining actions, a specific list, or `[]` for none |
| `actions.labels.<id>` | Button label (supports `{item-name}` and standard placeholders) |

Action IDs come from product click bindings and custom `menu.click-event-actions` (listing `buy` does not create a missing binding, and actions are filtered by prices, product options, and buy-only/sell-only rules).

Products can override `ui.price` and `ui.actions` at their root, while card styling applies menu-wide. The compact price represents one trade unit (with shared dynamic pricing and multipliers); full lore and auto-lore still appear in details/tooltips and can be filtered with [presentation conditions](../menus/display-item-add-lore.md#presentation-conditions).

<a id="theme-and-text"></a>

## Theme and Text

Quote hex colors in YAML. ArcMenu supports `#RRGGBB` and `#AARRGGBB`, while Ore's generated RGB textures ignore alpha.

| Path relative to `ui.theme` | Purpose |
| --- | --- |
| `background-enabled`, `background` | ArcMenu window background |
| `sidebar`, `card`, `border`, `accent`, `text` | Shared panel/card/border/accent colors and ArcMenu base text color |
| `button.background` / `hover` / `border` / `text-align` | Common graphical button styling |
| `buy.background` / `hover`, `sell.background` / `hover` | Trade button styling |
| `title.background-enabled` / `background` / `decoration-enabled` | Standalone ArcMenu title background and decorations |
| `decorations.enabled` / `style` / `color` / `corner-color` | ArcMenu frame and corner accents |
| `decorations.thickness` / `corner-thickness` / `corner-length` / `inset` | Decoration sizing |
| `decorations.dividers-enabled` | ArcMenu header dividers |
| `bevel.enabled` / `shadow` / `top` / `left` / `right` / `bottom` | ArcMenu control bevel defaults |
| `tooltip.border` / `background` | ArcMenu hover tooltip colors |
| `details.background`, `more-menu.background` | ArcMenu details panel and extra-actions popup background |

ArcMenu's `buy`, `sell`, `quantity`, `pagination`, and `cancel` sections also accept per-control `text` and bevel colors (blended with the control's base color).

Title backgrounds can be enabled independently of the window background:

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

`decorations.style` accepts `frame`, `corners`, `both`, or `none` (`none` omits the outer frame while still allowing header dividers). Ranges: thickness `0.5–6`, corner thickness `0.5–8`, corner length `0–80`, inset `0–30`.

Graphical UI labels live under `ui.text` (`cancel`, `no-extra`, `amount-input`, `select-product`, `favourite-edit.forward/backward/remove`) and support language references.

## Renderer-Specific Settings

Ore texture templates live under `ui.renderers.ore.templates`, while ArcMenu interaction, animation, and tooltip settings live under `ui.renderers.arcmenu` (`update-interval` is global-only). Sharing geometry does not guarantee identical font sizing or card body wrapping between the two renderers.

[Ore UI](../menus/ore-menus-premium.md) · [ArcMenu UI](../menus/arcmenu-menus-premium.md) · [Compatibility](../menus/migration-guide.md)
