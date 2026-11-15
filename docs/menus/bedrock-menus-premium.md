# 🛏️ Bedrock Form UI — Premium

Bedrock Form presents menu entries as a list, with separate product information and quantity input forms. It shares shop products, prices and trade rules with Java interfaces. It uses native Bedrock controls rather than graphical `ui.layout` coordinates.

## Enable Form

A supported Geyser/Floodgate setup and Bedrock player detection are required. In `config.yml`:

```yaml
menu:
  bedrock:
    enabled: true
    check-method: FLOODGATE
    not-auto-close: true
    auto-add-icon:
      enabled: true
```

In a Common menu, for example `menus/common_menus/categories.yml`:

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

Create `farming`, reload, then open `/shop menu categories` as a Bedrock player. Normal selection requires both global and menu switches. Global `menu.force-ui` can override those switches; platform and integration requirements remain.

## Entry list and visibility

Form collects root-layout entries and omits empty chest spacing. Shop product entries are listed before ordinary navigation controls. Buttons execute a LEFT click; a product normally opens its information Form.

Hide decorative entries with button/product `menu-visibility.form: false`. The active display item's `bedrock.hide: true` or an empty button name can also omit a Form button.

## Icons and supplemental text

Place these fields inside the active `display-item`:

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

| `display-item.bedrock` key | Meaning |
| --- | --- |
| `icon` | `path;;<client texture path>` or `url;;<public image URL>` |
| `extra-line` | Supplemental button text; products support price placeholders |
| `hide` | Hide this display variant from Form |

`path;;` points into the Bedrock client's resources. `url;;` points to an image the client can load. Without an explicit icon, automatic icons attempt the vanilla material texture using `bedrock.auto-add-icon`. Java custom model data does not automatically supply a Bedrock texture. Put variant-specific icon options inside each conditional display item.

For products, an explicit `display-item.bedrock.extra-line` wins; `''` intentionally leaves it blank. Otherwise, menu `bedrock.price-extra-line` falls back to global `menu.bedrock.price-extra-line`:

```yaml
bedrock:
  price-extra-line:
    default: '&7Buy: {buy-price} | Sell: {sell-price}'
    only-buy: '&7Buy: {buy-price}'
    only-sell: '&7Sell: {sell-price}'
```

This text is independent of graphical `ui.products.price`.

## Direct product actions

To run a product operation instead of opening information, add a product-root shortcut in the shop file:

```yaml
items:
  A:
    bedrock:
      click-event: buy
```

Lookup order is product `bedrock.click-event`, owning shop menu `bedrock.click-event`, then global `menu.bedrock.click-event`. Only the LEFT product interaction uses this shortcut. Available bindings, trade conditions and item-aware selling routes remain in effect.

## Form text reference

Supported options use menu `bedrock.*`, then global `menu.bedrock.*`. Player detection is global.

| Relative key | Purpose |
| --- | --- |
| `enabled` | Allow Form |
| `content` | Menu description, with local root `content` fallback |
| `check-method` | Global detection method: `FLOODGATE` or `UUID` |
| `not-auto-close` | Reopen after supported information-form trades |
| `auto-add-icon.enabled/format` | Vanilla icon generation; format supports `{version}` and `{path}` |
| `price-extra-line.default/only-buy/only-sell` | Supplemental product price text |
| `click-event` | Default direct product operation |
| `info.title`, `info.buttons.buy/sell/buy-more/sell-all/back` | Product information text |
| `buy-or-sell.title`, `buy-or-sell.buttons.amount.name/tip` | Quantity input text |
| `search.input`, `search.buttons.search` | Search input and button |
| `favourite-edit.title/content/buttons.*` | Favourite editing text |

`{lang}` uses the matching language override. Form product actions come from available click operations. Graphical `ui.products.actions` and Inventory Buy More confirmation labels do not define native Form controls.

Search and Favourite use dedicated Form flows. Item Sell and Sell All retain their physical inventories. The graphical `ui.style: bedrock` preset is a separate appearance setting for Ore/ArcMenu.

[UI Modes](ui-overview.md) · [Buy More](buy-more-menus.md) · [Search](search-menus-premium.md) · [Favourite](favourite-menus-premium.md)
