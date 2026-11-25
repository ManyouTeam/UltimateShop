# 🛏️Bedrock Form UI - Premium

Bedrock Form UI converts menu entries into native Bedrock list buttons and provides dedicated product info and quantity-input forms. It shares products, prices, and trade rules with Java screens without using graphical `ui.layout` coordinates.

## Enable Forms

Requires a supported Geyser / Floodgate setup that detects Bedrock players. Configure `config.yml`:

```yaml
menu:
  bedrock:
    enabled: true
    check-method: FLOODGATE
    not-auto-close: true
    auto-add-icon:
      enabled: true
```

Enable on a Common menu (e.g. `menus/common_menus/categories.yml`):

```yaml
title: '&8Categories'
size: 9
layout:
  - '0000A0000'
bedrock:
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
```

Make sure the `farming` shop already exists. After reloading, join with a Bedrock client and run `/shop menu categories` to see the form. Under normal selection, both global and menu `bedrock.enabled` must be `true` (global `menu.force-ui` overrides switches while keeping platform and integration requirements).

## Entry List and Visibility

Forms collect entries from root `layout` and ignore chest spacer gaps; shop products appear before common navigation buttons. Clicking a common button runs `LEFT` click, while clicking a product opens its info form by default.

Hide filler panes with `menu-visibility.form: false`; entries whose active display item has `bedrock.hide: true` or an empty button name are also omitted.

## Icons and Extra Text

Configure Bedrock Form icons and extra lines inside the active `display-item`:

```yaml
buttons:
  A:
    display-item:
      material: WHEAT
      name: '&aFarming'
      bedrock:
        icon: 'path;;textures/items/wheat.png'
        extra-line: '&7Seeds, crops and food'
        hide: false
```

| `display-item.bedrock` Key | Meaning |
| --- | --- |
| `icon` | `path;;<client texture path>` or `url;;<public image URL>` |
| `extra-line` | Extra line below the button title (products support price placeholders) |
| `hide` | Hides this display variant in Forms |

When `icon` is omitted, `bedrock.auto-add-icon` attempts to use vanilla textures (Java custom-model-data does not provide Bedrock textures automatically). For conditional display items, place `bedrock` options inside each variant.

For products, explicit `display-item.bedrock.extra-line` wins (`''` intentionally leaves it blank); otherwise the plugin reads menu `bedrock.price-extra-line` then global `menu.bedrock.price-extra-line`:

```yaml
bedrock:
  price-extra-line:
    default: '&7Buy: {buy-price} | Sell: {sell-price}'
    only-buy: '&7Buy: {buy-price}'
    only-sell: '&7Sell: {sell-price}'
```

This is independent of graphical `ui.products.price`.

## Direct Product Actions

To execute a trade directly instead of opening the info form, add `bedrock.click-event` at the product root in the shop file:

```yaml
items:
  A:
    bedrock:
      click-event: buy
```

Lookup order: product `bedrock.click-event` → owning shop menu `bedrock.click-event` → global `menu.bedrock.click-event` (applies to `LEFT` clicks only; trade conditions and item-inspection sell routing still apply).

## Form Text & Option Reference

Options read menu `bedrock.*` first, then global `menu.bedrock.*` (`check-method` is global-only):

| Relative Key | Purpose |
| --- | --- |
| `enabled` | Allows Bedrock Form |
| `content` | Screen description (falls back to root `content`) |
| `check-method` | Global detection method: `FLOODGATE` or `UUID` |
| `not-auto-close` | Reopens supported info forms after a trade |
| `auto-add-icon.enabled` / `format` | Vanilla icon generation (supports `{version}`, `{path}`) |
| `price-extra-line.default` / `only-buy` / `only-sell` | Product price extra lines |
| `click-event` | Default direct product action |
| `info.title`, `info.buttons.buy/sell/buy-more/sell-all/back` | Product info form text |
| `buy-or-sell.title`, `buy-or-sell.buttons.amount.name/tip` | Quantity input form text |
| `search.input`, `search.buttons.search` | Search form input and button |
| `favourite-edit.title/content/buttons.*` | Favourite edit form text |

`{lang}` resolves from the active language file. Form product actions come from available click actions—graphical `ui.products.actions` and Inventory Buy More confirm labels do not define native Form controls.

Search and Favourite menus use dedicated Form flows, Item Sell and Sell All keep physical Inventory, and graphical `ui.style: bedrock` is an Ore/ArcMenu visual preset unrelated to Bedrock Forms.

[UI Modes](ui-overview.md) · [Buy More Menus](buy-more-menus.md) · [Search Menus](search-menus-premium.md) · [Favourite Menus](favourite-menus-premium.md)
