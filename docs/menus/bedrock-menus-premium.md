# 🛏️ Bedrock Form UI - Premium

Form renders menus as Bedrock button lists and input screens. Products, prices, conditions and saved favourites come from the same configuration as other presentations. Form supports common, shop, search and favourite menus, plus product information and quantity input. Physical item deposit menus remain Inventory interfaces.

## Requirements and quick start

Set up Geyser and Floodgate for your server/proxy arrangement, using Floodgate authentication. UltimateShop must detect Bedrock players and send forms from the backend through Floodgate.

In `config.yml`:

```yaml
menu:
  bedrock:
    enabled: true
    check-method: FLOODGATE
    not-auto-close: true
    auto-add-icon:
      enabled: true
      format: 'https://raw.githubusercontent.com/InventivetalentDev/minecraft-assets/refs/heads/{version}/assets/minecraft/textures/{path}.png'
    price-extra-line:
      default: '{lang}'
      only-buy: '{lang}'
      only-sell: '{lang}'
```

In a menu:

```yaml
title: 'Farming'
bedrock:
  enabled: true
  content: '&7Choose a product.'
```

Both global and menu `bedrock.enabled` must allow Form. Inventory is used when Form is unavailable. Bedrock button icons and per-item supplemental text remain under `display-item.bedrock`.

## How a Form is built

The common/shop Form obtains entries from root `layout`, applies presentation visibility and display conditions, then turns each visible display item into a button. There are no chest-shaped empty spaces. Common buttons follow slot order; shop forms list products before ordinary navigation buttons. Ore/ArcMenu coordinates, card sizes and themes do not position Form buttons.

The button's first line is the display item name. The optional second line comes from `bedrock.extra-line` or the shared product price format. Inventory lore is not copied into every list button; product information uses the full product description/lore. Blank display names and `bedrock.hide: true` omit buttons, which is useful for decorative glass fillers.

Ordinary buttons execute their actions with LEFT click. A shop product normally opens its information Form; setting product `bedrock.click-event` requests a particular action directly.

## Explicit icons

Configure icons in the display item actually used by that button/product:

```yaml
buttons:
  A:
    display-item:
      material: WHEAT
      name: '&aFarming'
      bedrock:
        icon: 'path;;textures/items/wheat.png'
        extra-line: '&7Browse crops and food'
        hide: false
    actions:
      1:
        type: shop_menu
        shop: farming
```

| Display-item key | Meaning |
| --- | --- |
| `bedrock.icon` | Explicit Form icon: `path;;<texture path>` or `url;;<image URL>` |
| `bedrock.extra-line` | Text appended after the name on a new line; supports placeholders; products can use `{buy-price}` and `{sell-price}` |
| `bedrock.hide` | Hide this Form button, default false |

`path` addresses a texture available in the Bedrock client's resources, including a loaded Bedrock pack. It is not a server filesystem path or a Java resource-pack model ID. `url` loads an externally reachable image:

```yaml
display-item:
  material: DIAMOND
  name: '&bSpecial product'
  bedrock:
    icon: 'url;;https://example.com/shop/diamond.png'
```

Replace the example URL with your own image. Java custom-model-data does not automatically provide a Form icon. Use an explicit icon for custom items. Supply exactly one `;;` separator with a supported type; malformed icons are not a reliable way to disable automatic icons.

For a product with an explicit `display-item`, put `bedrock` inside that display item. If the display is inherited from a reward product, the active reward/display configuration supplies the icon. Conditional display-item variants need the icon in the variant that is selected.

## Automatic icons

With `menu.bedrock.auto-add-icon.enabled: true`, buttons without explicit icons try to use the display material's vanilla texture. Material resources are prepared on demand and cached under `minecraft-resources/<version>/`. While a texture mapping is unavailable, the button can appear without an image. A custom item using a vanilla material still resolves from that material, not its custom model.

The URL pattern supports `{version}` (Minecraft resource version) and `{path}` (resolved texture path). Set the pattern globally at `menu.bedrock.auto-add-icon.format`, or override it in a menu at `bedrock.auto-add-icon.format`. Explicit display-item icons take precedence.

Automatic-icon enabling also supports per-menu `bedrock.auto-add-icon.enabled`. Bedrock detection uses global `menu.bedrock.check-method`.

## Visibility

At the button or product root:

```yaml
menu-visibility:
  inventory: true
  form: true
  dialog: true
  item-action-list: true
  ore: true
  arcmenu: true
```

Set `form: false` to hide an entry from Bedrock Form. Omitted values default to true. `display-item.bedrock.hide: true` also hides that specific Form button, while `ui.placement.enabled` affects only graphical Ore/ArcMenu placement.

## Product information, actions and price text

The Form information screen uses the configured click bindings to offer available trade actions. Set its labels in `menu.bedrock.info.buttons` (or menu `bedrock.info.buttons`). The graphical `ui.products.actions` settings apply only to Ore and ArcMenu, not Form.

Default Form price text is configured by `menu.bedrock.price-extra-line.default`, `only-buy` and `only-sell`. The active `display-item.bedrock.extra-line` overrides the generated line; an empty string suppresses it. The display text supports `{buy-price}` and `{sell-price}`.

To bypass the product information Form:

```yaml
items:
  A:
    bedrock:
      click-event: buy
```

This product-root setting is distinct from display-item-specific `bedrock.icon`, `extra-line` and `hide`. The menu default is `bedrock.click-event`, global default `menu.bedrock.click-event`. Search and favourite product information still uses its own flow.

## Quantity input

`select-amount` opens a Custom Form with an amount input, then product information with that selected quantity. It requires Buy More to be enabled, a corresponding menu, and an available quantity action. The effective range is 1 through the product's `buy-more-menu.max-amount`; invalid input falls back to 1. Without a chosen quantity, buying and selling retain their separate `default-buy-amount` and `default-sell-amount`.

Form does not render Inventory Buy More `amount-items`, preview slots or confirmation buttons. Configure its input text using the renderer keys below. Closing quantity input closes that flow; cancelling Search input has separate return behaviour.

## Form renderer configuration reference

Configure global defaults under `menu.ui.renderers.form` and menu overrides under `ui.renderers.form`. Search/favourite text uses that menu; product information/quantity text and reopen behaviour use the owning shop menu. Bedrock detection uses global `check-method`. Product `bedrock.click-event` overrides the configured shop-list action.

| Relative key | Meaning |
| --- | --- |
| `check-method` | `FLOODGATE` checks Floodgate player records; other values use the Bedrock UUID-prefix heuristic. Prefer `FLOODGATE`; Form delivery still needs Floodgate |
| `not-auto-close` | After built-in information-form buy/sell handling, reopen the shop when true; not a universal reopen rule for custom actions |
| `click-event` | Default shop-product Form click action; menu settings override global defaults, and product `bedrock.click-event` takes precedence |
| `auto-add-icon.enabled` | Enable vanilla material icons when no explicit icon is supplied |
| `auto-add-icon.format` | Automatic URL pattern with `{version}` and `{path}`; supports menu overrides |
| `buy-or-sell.title` | Quantity input title; supports `{item-name}` |
| `buy-or-sell.buttons.amount.name` | Quantity input label |
| `buy-or-sell.buttons.amount.tip` | Quantity input placeholder text |
| `info.title` | Product information title; supports `{item-name}` and `{amount}` |
| `info.buttons.buy`, `sell`, `buy-more`, `sell-all` | Fallback operation labels; labels used when no custom action text is configured |
| `info.buttons.back` | Information Back button text |
| `search.input` | Search keyword field label |
| `search.buttons.search` | Search home button text |
| `favourite-edit.title`, `favourite-edit.content` | Favourite edit action screen title/body; supports `{item-name}` |
| `favourite-edit.buttons.forward`, `backward`, `remove`, `back` | Favourite editing controls |

`{lang}` uses the corresponding language override, and explicit `{lang:key}` references may be used in text settings. Keep language keys such as `menu.bedrock.info.title` in language files; unified configuration does not rename those language keys.

## Search and favourites

Search opens its home/list first, then a keyword input when Search is selected. The previous keyword is prefilled; cancelling returns to the home with that keyword preserved. Blank keywords show no results. Result capacity comes from root `result-item` slots. Result information Back restores the same search. Physical item filtering, Inventory `action-items`, `state-items` and the fixed no-result slot are not reproduced as special Form controls; ordinary configured buttons are included.

Favourite menus support normal and editing lists, using the same saved data as Inventory. Normal selection opens product information. Editing selection opens Forward, Backward, Remove and Back; closing that action form returns to editing. Configured empty slots can appear as buttons. See the [Search](search-menus-premium.md) and [Favourite](favourite-menus-premium.md) pages for their file settings.

## Troubleshooting

If a chest opens, check Premium, Bedrock detection, Floodgate availability, selected mode and menu type. If an image is missing, check the active display item, client texture path or URL, automatic resource preparation, and whether the button has a nonblank name. For information/quantity text or reopen behaviour, check the owning shop menu override. Renderer-specific settings belong under `ui.renderers.form` or global `menu.ui.renderers.form`.


## Related UI documentation

[UI Modes](ui-overview.md) · [Menu Configuration](menu-configuration.md) · [Troubleshooting](troubleshooting.md)
