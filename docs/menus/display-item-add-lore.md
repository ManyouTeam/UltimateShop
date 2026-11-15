# 🥉 Display Item Add Lore

Generated lore adds prices, limits and operation instructions to a product display. Original `display-item.lore` remains separate. Inventory, product information and graphical tooltips can show the generated text, with conditions selecting lines for each presentation.

Compact prices are configured separately: Form uses `bedrock.price-extra-line`; graphical cards use `ui.products.price`.

## Global and per-product lore

Global defaults belong in `config.yml`:

```yaml
display-item:
  add-lore:
    - '@a&eBuy {buy-amount} trades: {buy-price}'
    - '@b&aSell {sell-amount} trades: {sell-price}'
    - '@c&7Player purchases: {buy-times-player}/{buy-limit-player}'
    - '@t[inventory]&7Use the configured product click controls'
    - '@t[ore]&7Choose a card operation or view details'
    - '@t[arcmenu-hover]&7Choose an operation on the card'
```

The bundled list `['{lang}']` loads the corresponding language lore format. A product-root `add-lore` replaces that format:

```yaml
items:
  A:
    display-item:
      material: APPLE
      name: '&aApple'
      modify-lore: true
    add-lore:
      - '@a&eBuy: {buy-price}'
      - '@b&aSell: {sell-price}'
```

This fragment extends an existing product. Set `display-item.modify-lore: false` to suppress generated additions for that display variant. Buy More confirmation buttons have a separate control-root `modify-lore` switch, default `true`.

## Conditional symbols

Put condition symbols before a line's text for readability. All conditions on that line must pass; condition syntax is removed from the displayed result. Lines without conditions always appear.

| Symbol | Show when |
| --- | --- |
| `@a` | Buy prices exist |
| `@b` | Display sell prices exist |
| `@c` / `@d` | Player / server buy limit is configured |
| `@e` / `@f` | Player / server sell limit is configured |
| `@g` / `@h` | Positive player buy / sell limit has been reached |
| `@i` / `@j` | Positive server buy / sell limit has been reached |
| `@k` | Product enables Buy More |
| `@l` | Product allows favourites |
| `@m` | Display sell prices exist and sell-all is enabled |
| `@n` | The current operation supports a direction with a price |
| `@o` | The opening menu is Favourite |
| `@p` / `@q` | Inside / outside Buy More lore context |
| `@u` / `@v` | The current operation supports buying / selling |
| `@w` | A specific click-action context is supplied |
| `@x` / `@y` | Player is / is not detected as Bedrock |
| `@t[presentation]` | Active presentation matches a listed name |
| `@z[multiplier-id]` | The named sell multiplier is active |

Use lower-case symbols. Parentheses negate a condition, for example `(@a)` or `(@z[vip])`. Combine symbols for AND logic:

```yaml
add-lore:
  - '@a@t[inventory]&eBuy: {buy-price}'
  - '@g&cPlayer purchase limit reached'
  - '(@a)&7This product cannot be purchased'
```

A condition affects only its own line. Include conditions on separator/blank lines too when they should disappear with a section.

## Presentation conditions

| `@t[...]` value | Rendering context |
| --- | --- |
| `inventory` | Chest-menu lore |
| `form` | Bedrock product information |
| `dialog` | Native Dialog information |
| `item-action-list` | Native item-action-list when that context is supplied |
| `ore` | Ore card body/details context |
| `ore-hover` | Ore hover tooltip |
| `arcmenu` | ArcMenu body/details context |
| `arcmenu-hover` | ArcMenu hover tooltip |

List multiple targets with commas, for example `@t[inventory,ore]`. Matching ignores case and surrounding spaces. A missing presentation does not satisfy a positive presentation condition. `dialog` does not automatically include Ore or every Dialog-derived context.

Use `@t[inventory]` for Inventory mouse/key instructions. `@y` matches Java players in ArcMenu and Ore too. These conditions filter generated lore, rather than the item's original static lore.

Ore body lore is limited by `ui.products.card.lore-lines`; full applicable lore remains available to tooltips. ArcMenu compact card price text uses `ui.products.price` instead of displaying every generated line in the card body.

## Product placeholders

| Placeholder | Meaning |
| --- | --- |
| `{buy-price}`, `{sell-price}` | Price for the corresponding operation's quantity |
| `{buy-amount}`, `{sell-amount}` | Independent buy/sell trade quantities |
| `{amount}` | Display amount, affected by `display-item.calculate-amount` |
| `{item-name}` | Product display name |
| `{buy-click}`, `{sell-click}` | Generated operation availability/click text |
| `{buy-limit-player}`, `{sell-limit-player}` | Player limits |
| `{buy-limit-server}`, `{sell-limit-server}` | Server limits |
| `{buy-times-player}`, `{sell-times-player}` | Player counters for the current period |
| `{buy-times-server}`, `{sell-times-server}` | Server counters for the current period |
| `{buy-total-player}`, `{sell-total-player}` | Player lifetime totals |
| `{buy-total-server}`, `{sell-total-server}` | Server lifetime totals |
| `{buy-refresh-player}`, `{sell-refresh-player}` | Player reset-time display |
| `{buy-refresh-server}`, `{sell-refresh-server}` | Server reset-time display |
| `{buy-next-player}`, `{sell-next-player}` | Next player reset |
| `{buy-next-server}`, `{sell-next-server}` | Next server reset |
| `{last-buy-player}`, `{last-sell-player}` | Player last trade time |
| `{last-buy-server}`, `{last-sell-server}` | Server last trade time |
| `{last-reset-buy-player}`, `{last-reset-sell-player}` | Player last reset |
| `{last-reset-buy-server}`, `{last-reset-sell-server}` | Server last reset |

Default buy/sell amounts are independent until quantity selection supplies a common count. An explicit display-item stack amount is separate from trade quantity. See [Default Trade Amounts](../shops/products.md#default-trade-amounts).

`{buy-click}` and `{sell-click}` use global `placeholder.click`. With `placeholder.click.enabled: true`, the plugin previews operation availability and selects condition/limit/funds text from that section. The actual transaction still runs its own validation.

## Multi-line prices

Use `;;` price separators in `config.yml` to split formatted price components into lore lines:

```yaml
placeholder:
  price:
    split-symbol-any: ';;'
    split-symbol-all: ';;'
    unknown: 'Unknown'
display-item:
  add-lore:
    - '@a&ePurchase:'
    - '@a&7  {buy-price}'
    - '@b&aSell:'
    - '@b&7  {sell-price}'
```

Keep the corresponding buy/sell condition on every relevant heading and price line.

## Related display defaults

| Global `display-item` key | Purpose |
| --- | --- |
| `auto-set-first-product` | Use the first applicable product reward when no explicit display item is supplied |
| `calculate-amount` | Include reward amount when calculating displayed `{amount}` |
| `auto-translate-item-name` | Automatic vanilla display-name translation on supported Paper 1.17.1+ |
| `auto-use-sprite-item-name` | Java sprite item names on compatible Paper runtimes |

Explicit custom names take precedence. See [Localized Item Name](../features/localized-item-name-premium.md) for the shared name/resource system.

[Shop Menus](shop-menus.md) · [Bedrock Form](bedrock-menus-premium.md) · [Graphical UI Layout](../menus-getting-started/ui-layout.md)
