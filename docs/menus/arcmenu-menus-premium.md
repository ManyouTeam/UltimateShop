# ✨ ArcMenu UI — Premium

ArcMenu displays Common, Shop, Search and Favourite menus as interactive graphical screens for Java players. It shares styles, areas, placements and product card settings with Ore, while using its own ArcMenu integration and client setup.

## Enable ArcMenu

Install a compatible ArcMenu plugin and complete its client resource setup. This project integrates with the ArcMenu 3.4.3 API, including its native text input service.

In `config.yml`:

```yaml
menu:
  arcmenu:
    enabled: true
  ui:
    style: classic
```

Save as `menus/common_menus/categories.yml`:

```yaml
title: '&8Categories'
size: 9
layout:
  - '0000A0000'
arcmenu:
  enabled: true
  content: '&7Choose a category'
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

Create `farming`, reload and open `/shop menu categories`. The selected style provides the canvas and `sidebar` area. In normal selection, Java tries ArcMenu before an enabled Dialog/Ore route and Inventory. Bedrock uses its own presentation selection. See [UI Modes](ui-overview.md) for global forced selection.

## Styles and layout

Use menu `ui.style: classic` or `ui.style: bedrock`, or select the global default with `menu.ui.style`. Style files live in `plugins/UltimateShop/ui/`, contain graphical fields directly, and can be copied for custom presets. Existing files are preserved when the plugin creates missing bundled presets.

| Preset | Appearance and placement |
| --- | --- |
| `classic` | Vertical navigation at the left, product grid at the right |
| `bedrock` | Charcoal panels, beveled controls, green buying/hover states, pale selling controls, five-button top navigation and a three-column product grid |

The `bedrock` preset uses the named `sidebar` area for its horizontal navigation. The name identifies an area, not a fixed screen position. Both presets use existing item icons and fonts. Adapt explicit row/column positions and custom areas when changing presets.

Menu-local fields override normal style defaults. A forced style can override local fields through `menu.force-ui.style`; see [Configuration & Inheritance](menu-configuration.md). Geometry and shared card keys are documented in [Graphical UI Layout](../menus-getting-started/ui-layout.md).

## Product operations

Cards expose up to two available operations from `ui.products.actions.primary`, plus remaining or selected extra actions from `ui.products.actions.extra`. Operations must already exist in the configured product click bindings. Prices, conditions, quantity permissions and custom buy-only/sell-only restrictions still filter them.

Right-click defaults to `select-amount`. Change menu `ui.renderers.arcmenu.right-click-action` to select another available operation. `select-amount` opens graphical product details rather than rendering a Buy More file.

Default buy/sell quantities apply independently until the player selects a quantity. Plus/minus and scrolling adjust the selected quantity when Buy More and a quantity operation are available. Inventory `amount-items`, previews and confirmation slots do not define the graphical detail panel.

Item icons show the displayed stack count at their bottom-right when greater than one. This count describes the displayed stack, independently of the selected number of trade units.

## Quantity input

With a compatible ArcMenu text input service, click the quantity between `-` and `+` to edit it directly. UltimateShop shows the live draft in the focused graphical field even when ArcMenu hides the native anvil screen.

Finish editing, using Esc in mouse mode, to apply a valid whole number. The value is clamped from 1 to the selected Buy More maximum; invalid input keeps the previous quantity. Finishing input only selects the amount. The player then chooses Buy or Sell to trade.

The input label is `ui.text.amount-input`. The source product still needs Buy More, a valid quantity menu and an available `select-amount` operation.

## ArcMenu renderer settings

Menu-local renderer settings belong under `ui.renderers.arcmenu`:

```yaml
ui:
  renderers:
    arcmenu:
      right-click-action: select-amount
      screen-scale: 1.5
      animation:
        enabled: true
        duration: 8
      more-menu:
        columns: 1
      tooltip:
        enabled: true
        width: 260
        font-size: 1.1
        padding: 8
```

| Relative key | Purpose |
| --- | --- |
| `right-click-action` | Product right-click operation |
| `screen-scale` | Overall graphical scale |
| `animation.enabled/duration` | Opening animation; same-menu reopens skip it |
| `more-menu.columns` | Extra-action popup columns |
| `tooltip.enabled/width/font-size/padding` | Hover tooltip appearance |

Refresh interval is read **globally**, even when other settings are local:

```yaml
menu:
  ui:
    renderers:
      arcmenu:
        update-interval: 20
```

The interval is in ticks, with a minimum of 5. It can also be supplied by the selected global style. Per-menu `update-interval` does not set the refresh schedule.

## Backgrounds, decorations and text

ArcMenu reads graphical colors from resolved `ui.theme`. Window and title backgrounds have independent enable switches. `theme.decorations` supports frames, corners and header accents; bevels and control-specific colors let a style define its appearance without depending on its file name. See the [theme reference](../menus-getting-started/ui-layout.md#theme-and-text).

Tooltips wrap formatted lore in a left-aligned block and shrink oversized content to fit. Mouse layout fitting accounts for the active surface, cursor sensitivity, player preferences, cursor margin and the vertical pitch limit. Touch/Vision use the surface bounds.

## Menu-type behavior

Search/Favourite use ArcMenu layouts and their own controls, while root result-slot capacity still limits source results. Favourite editing exposes reordering/removal controls. Item Sell/Sell All remain physical inventories. Buy More quantity selection stays within product details.

ArcMenu does not use `/shop dialogpack` for its own controls; that command builds Ore assets.

[Search](search-menus-premium.md) · [Favourite](favourite-menus-premium.md) · [Buy More](buy-more-menus.md)
