# ✨ ArcMenu UI - Premium

ArcMenu presents Common, Shop, Search and Favourite menus as interactive graphical screens. It shares `ui.layout`, `ui.products`, `ui.placement` and `ui.theme` with Ore, but uses its own client integration and interaction model.

## Enable ArcMenu

In `config.yml`:

```yaml
menu:
  arcmenu:
    enabled: true
```

In your menu:

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

This uses the default global `menu.ui.layout.areas.sidebar` graphical area. Install a compatible ArcMenu integration and required client resources. If both ArcMenu and Dialog are enabled, Java players use **ArcMenu first** when available. If ArcMenu cannot open, the menu-opening flow can attempt Dialog/Ore, then Inventory. Bedrock players use their separate `bedrock.enabled` selection.

## Product interaction

* Graphical product cards use `ui.products.actions.primary` for up to two primary operation buttons and `ui.products.actions.extra` for an extra-action menu.
* Listing an action does not create a trade binding. Available actions still come from `menu.click-event` and product overrides.
* `select-amount` normally opens ArcMenu product details; quantity adjustment requires Buy More to be enabled for the product and a suitable quantity binding.
* Product right-click defaults to `select-amount`. Change it with `ui.renderers.arcmenu.right-click-action`.
* A shop's default buy/sell quantities apply until the player selects another quantity.
* With ArcMenu 3.4.3 or newer, click the quantity between `-` and `+` to open its native input editor. UltimateShop draws a focused input box with the live draft even when ArcMenu conceals the native anvil GUI. Finish editing (Esc in mouse mode) to apply a valid whole number immediately, clamped to `1` through the Buy More menu's `max-amount`. Invalid input keeps the previous quantity. The player then clicks Buy or Sell to trade; finishing input does not execute a transaction.

Item icons show the displayed stack quantity in white with a dark shadow at the icon's bottom-right when the quantity is greater than one. Product cards no longer display a separate cyan `×10` quantity label. This icon count describes the displayed item stack; the product's buy/sell quantity and the detail panel's quantity selector still follow their own settings.

ArcMenu product details are **not** the Inventory Buy More menu. The latter's `amount-items` and `layout` do not control ArcMenu's detail panel.

## ArcMenu graphical settings

Defaults live at `menu.ui.renderers.arcmenu`; most renderer settings support per-menu overrides at `ui.renderers.arcmenu`. **`update-interval` is currently read only from the global `menu.ui.renderers.arcmenu.update-interval` path.**

### Bedrock style

Styles are stored in `plugins/UltimateShop/ui/`. Both `classic.yml` (the original appearance) and `bedrock.yml` are created at startup if missing. Set `menu.ui.style: bedrock` in `config.yml` globally, or select it for one menu:

```yaml
ui:
  style: bedrock
```

The Bedrock preset uses charcoal square panels, beveled buttons, green hover and buy buttons, pale sell buttons, a navigation bar across the top with room for five buttons, and a three-column product grid. Navigation stays visible across product pages. Product details, quantity input and tooltips use the same palette. It uses Minecraft's existing font and item icons and does not require extra image assets. `classic` keeps the original appearance.

Bevel highlight and shadow colors are composited against the current panel color before rendering, so opaque cards and buttons retain opaque edges even when the configured bevel color has alpha. The backdrop uses a low-transparency neutral gray to reduce world color bleed. Price labels are white with green values. Existing style files are preserved on upgrade; replace `ui/bedrock.yml` with the bundled revision to apply these updated palette defaults, then reload UltimateShop.

Mouse layout fitting uses the active ArcMenu surface, cursor sensitivity, per-player sensitivity preference and cursor margin. It also respects Minecraft's vertical pitch limit, keeping footer buttons within the physically reachable cursor area. Touch and Vision use the surface bounds directly.

Each style file contains the UI fields directly (`layout`, `products`, `theme`, `text` and `renderers`), without a wrapping `ui` section. To add a custom style, copy either file to `ui/my-style.yml` and select `ui.style: my-style`. Subfolders are supported, for example `ui.style: custom/dark` loads `ui/custom/dark.yml`. Reload UltimateShop after editing styles.

Menu-local UI settings take precedence over the selected style. Selecting a different style overrides the global UI defaults, and missing fields fall back to those defaults. Existing `menu.ui` settings remain supported as overrides of the global style. Styles provide the shared graphical layout used by ArcMenu and Ore. ArcMenu reads rendering parameters such as `theme.bevel.enabled`, button colors and text alignment, area backgrounds and header `bold` directly from the selected file; Java does not branch on style file names.

To force one presentation and style across menus, use the existing `force-ui` section in `config.yml`:

```yaml
menu:
  force-ui:
    enabled: true
    type: arcmenu
    style: bedrock
```

An enabled `force-ui.style` overrides menu style selections and local UI settings for fields provided by that style. Empty `style` forces only the presentation. When `force-ui.enabled` is false, the forced style is ignored.

Menus with explicit grid cells, custom areas or free coordinates should adapt their placements to the chosen style (the Bedrock preset uses the existing `sidebar` area for up to five navigation buttons and `content` for products). For example, a menu can select the Bedrock style and override its accent:

```yaml
ui:
  style: bedrock
  theme:
    accent: '#6CC349'
```

### Other settings

```yaml
ui:
  renderers:
    arcmenu:
      right-click-action: select-amount
      screen-scale: 1.35
      update-interval: 20
      animation:
        enabled: true
        duration: 8
      more-menu:
        columns: 1
      tooltip:
        enabled: true
        width: 260
        font-size: 0.9
        padding: 8
```

| Key relative to `ui.renderers.arcmenu`    | Effect                                                            |
| ----------------------------------------- | ----------------------------------------------------------------- |
| `right-click-action`                      | Direct action triggered by product right-click                    |
| `screen-scale`                            | Overall graphical scale                                           |
| `update-interval`                         | **Global only:** graphical refresh interval, in ticks (minimum 5) |
| `animation.enabled/duration`              | Opening animation                                                 |
| `more-menu.columns`                       | Extra-action popup layout                                         |
| `tooltip.enabled/width/font-size/padding` | Hover-tooltip visibility and appearance                           |

The renderer constrains numeric settings to supported ranges. Very large fonts can truncate text; use sensible area and card sizes rather than relying on scale alone.

## Layout and theme are shared with Ore

Define a canvas, named `areas` and optional `pagination` under menu `ui.layout` or global `menu.ui.layout`. Ordinary buttons require `ui.placement`; products use `ui.products.placement` unless overridden. In Search/Favourite screens the root result-slot count still limits source results, regardless of how many cards a graphical area could hold.

See [📐 Graphical layout - Premium](../menus-getting-started/ui-layout.md) for the complete schema and correct scopes. ArcMenu does not require Ore's generated pack merely to display its own controls; follow the ArcMenu integration's client setup. The `/shop dialogpack` command concerns **Ore assets**.

## Troubleshooting

If Inventory opens, check the two ArcMenu switches, plugin integration, resource setup and supported menu type. Item Sell and Sell All require physical inventories; Buy More uses ArcMenu product details instead of a separate Inventory-style ArcMenu screen. Use [Menu troubleshooting](troubleshooting.md) for symptoms.

[🎨 Presentation selection](ui-overview.md) · [📐 Layout - Premium](../menus-getting-started/ui-layout.md) · [🪨 Ore - Premium](ore-menus-premium.md).

## Independent title backgrounds and decorations

ArcMenu reads these settings from menu `ui.theme` or global `menu.ui.theme`. They use the same names as SpinToWin's Wheel theme:

```yaml
ui:
  theme:
    background-enabled: true
    title:
      background-enabled: false
      background: '#EF0C1223'
      decoration-enabled: false
    decorations:
      enabled: true
      style: none
      color: '#65E8EF'
      corner-color: '#65E8EF'
      thickness: 2
      corner-thickness: 2
      corner-length: 20
      inset: 10
      dividers-enabled: true
```

The title background can be enabled while the window background is disabled. `title.decoration-enabled` adds a frame/corners around the configured title header bounds. `decorations.style` accepts `frame`, `corners`, `both` or `none`; `none` preserves the default header accent without adding a window frame. `enabled: false` disables all theme decoration. `dividers-enabled` controls the header accent. Thickness ranges are `0.5–6` and `0.5–8`, corner length `0–80`, inset `0–30`. Colors accept `#RRGGBB` or `#AARRGGBB`.

Header entries under `ui.layout.header` accept `height` (default `27`). Text is vertically centered within that region, and title backgrounds/decorations follow the same bounds.

Tooltip text is pre-wrapped with formatting preserved, displayed as a single left-aligned block and never ellipsized a second time. Oversized tooltips shrink to fit rather than silently dropping Lore. The renderer also skips the opening animation when reopening the same active menu; ordinary content updates already reuse the current session. First opens retain the configured animation.


## Force one presentation globally

```yaml
menu:
  force-ui:
    enabled: false
    type: inventory
```

Enable `menu.force-ui.enabled` in `config.yml` to override individual menu and global presentation enable switches. Supported types are `inventory`, `arcmenu`, `dialog`, `ore`, `item-action-list` and `form`. Disabling the switch restores normal presentation selection. Invalid types report a configuration error and use normal selection.

`inventory` applies to both Java and Bedrock. `arcmenu`, `dialog`, `ore` and `item-action-list` target Java; incompatible Bedrock clients use Inventory. `form` targets Bedrock, with Inventory for Java. Existing platform, edition and integration requirements still apply. Unsupported menu types retain their supported fallback: Item Sell and Sell All use physical inventories; Buy More supports Inventory/Dialog and ArcMenu product details; Search/Favourite use native Dialog when Ore is unavailable for that type. Missing ArcMenu falls back to Inventory. Forcing native `dialog` also replaces an Ore layout with `multi-action`.

Reload the plugin after changing the setting so loaded menu definitions are rebuilt.
