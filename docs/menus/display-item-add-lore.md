# 🥉 Display Item Add Lore

Generated lore adds product prices, stock, limits and operation instructions to the display item. It is shared by Inventory and product information/tooltips in other presentations. Compact Form/ArcMenu list price text is configured separately in `ui.products.price`.

## Quick start: global and per-product configuration

In `config.yml`:

```yaml
display-item:
  add-lore:
    - '@a&eBuy {buy-amount} trades: {buy-price}'
    - '@b&aSell {sell-amount} trades: {sell-price}'
    - '@c&7Player purchases: {buy-times-player}/{buy-limit-player}'
    - '@t[inventory]&7Left click to buy'
    - '@t[ore]&7Use the card actions or View Detail'
    - '@t[arcmenu-hover]&7Choose an operation on the card'
```

The bundled `['{lang}']` list loads the corresponding language format. A product's root `add-lore` overrides the global format:

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

Set product `display-item.modify-lore: false` to suppress generated additions for that display. Original display-item `lore` is separate. Buy More confirmation buttons also have their own root `modify-lore` switch, default true.

## Related global display settings

| Key under `display-item`    | Meaning                                                                                          |
| --------------------------- | ------------------------------------------------------------------------------------------------ |
| `add-lore`                  | Default generated lore list                                                                      |
| `auto-set-first-product`    | Use the first applicable product reward as the display when no explicit display item is supplied |
| `calculate-amount`          | Include the product reward amount in the displayed `{amount}` calculation                        |
| `auto-translate-item-name`  | Paper 1.17.1+ automatic vanilla display-name translation support                                 |
| `auto-use-sprite-item-name` | Java sprite item-name support on compatible Paper runtimes                                       |

Explicit custom names take precedence. Material/name resources are handled through the shared resource manager; see [Localized Item Name - Premium](../features/localized-item-name-premium.md).

## Conditional symbols

Each condition on a line must pass. Conditions are removed from the final displayed text. Lines without conditions always appear. Use lower-case symbols and place them before the text for readability.

| Symbol              | Show when                                                                     |
| ------------------- | ----------------------------------------------------------------------------- |
| `@a`                | Product has buy prices                                                        |
| `@b`                | Product has display sell prices                                               |
| `@c`                | Player buy limit is configured                                                |
| `@d`                | Server buy limit is configured                                                |
| `@e`                | Player sell limit is configured                                               |
| `@f`                | Server sell limit is configured                                               |
| `@g`                | Positive player buy limit has been reached                                    |
| `@h`                | Positive player sell limit has been reached                                   |
| `@i`                | Positive server buy limit has been reached                                    |
| `@j`                | Positive server sell limit has been reached                                   |
| `@k`                | Product enables Buy More                                                      |
| `@l`                | Product allows favourites                                                     |
| `@m`                | Product has display sell prices and enables sell-all                          |
| `@n`                | The current operation supports a direction with a price                       |
| `@o`                | Opening menu is a Favourite menu                                              |
| `@p`                | Rendering in Buy More context                                                 |
| `@q`                | Rendering outside Buy More context                                            |
| `@u`                | Current operation supports buying                                             |
| `@v`                | Current operation supports selling                                            |
| `@w`                | A specific click-action context is supplied, rather than general product lore |
| `@x`                | Player is detected as Bedrock                                                 |
| `@y`                | Player is not detected as Bedrock                                             |
| `@t[presentation]`  | Rendering in one of the listed presentations                                  |
| `@z[multiplier-id]` | The specified sell multiplier is active                                       |

Negate a condition with parentheses, for example `(@a)` (no buy price), `(@z[vip])` (VIP multiplier inactive), or `(@t[ore])` (outside the Ore card body). Combine conditions for AND logic:

```yaml
add-lore:
  - '@a@t[inventory]&eBuy: {buy-price}'
  - '@g&cPlayer purchase limit reached'
  - '(@a)&7This product cannot be purchased'
```

Use the condition prefixes described above; suffix-style conditions are not supported.

## Presentation conditions

Supported `@t[...]` names:

| Name               | Content                                                                 |
| ------------------ | ----------------------------------------------------------------------- |
| `inventory`        | Chest menu lore                                                         |
| `form`             | Bedrock product information                                             |
| `dialog`           | Native Dialog information                                               |
| `item-action-list` | Native item-action-list content when that rendering context is supplied |
| `ore`              | Ore product card body / details context                                 |
| `ore-hover`        | Ore hover tooltip                                                       |
| `arcmenu`          | ArcMenu body/details context                                            |
| `arcmenu-hover`    | ArcMenu hover tooltip                                                   |

List several targets with commas: `@t[inventory,ore]`. Matching ignores case and surrounding spaces. `dialog` does not automatically include Ore or every Dialog-derived context. Without an active presentation, a positive presentation condition does not match.

Apply the condition to every relevant line, including blank separator lines. `@y` only distinguishes Java from Bedrock; Java ArcMenu/Ore players still match it. Use `@t[inventory]` for mouse/keyboard inventory instructions. The condition works in global and per-product generated lore. It does not filter the item's original static lore; use the appropriate display-item configuration for that.

Ore card bodies are limited by `ui.products.card.lore-lines`. Graphical tooltips retain full original lore plus applicable generated lore. ArcMenu compact card price text uses `ui.products.price`, rather than displaying every generated lore line on the card.

## Product information placeholders

| Placeholder family                                    | Meaning                                                     |
| ----------------------------------------------------- | ----------------------------------------------------------- |
| `{buy-price}`, `{sell-price}`                         | Price for the corresponding operation's trade count         |
| `{buy-amount}`, `{sell-amount}`                       | Independent buy/sell trade counts                           |
| `{amount}`                                            | Display amount, affected by `display-item.calculate-amount` |
| `{item-name}`                                         | Product display name                                        |
| `{buy-click}`, `{sell-click}`                         | Generated operation availability/click message              |
| `{buy-limit-player}`, `{sell-limit-player}`           | Player limits                                               |
| `{buy-limit-server}`, `{sell-limit-server}`           | Server limits                                               |
| `{buy-times-player}`, `{sell-times-player}`           | Player use counters for the current period                  |
| `{buy-times-server}`, `{sell-times-server}`           | Server use counters for the current period                  |
| `{buy-total-player}`, `{sell-total-player}`           | Player lifetime totals                                      |
| `{buy-total-server}`, `{sell-total-server}`           | Server lifetime totals                                      |
| `{buy-refresh-player}`, `{sell-refresh-player}`       | Player reset-time display                                   |
| `{buy-refresh-server}`, `{sell-refresh-server}`       | Server reset-time display                                   |
| `{buy-next-player}`, `{sell-next-player}`             | Next player reset display                                   |
| `{buy-next-server}`, `{sell-next-server}`             | Next server reset display                                   |
| `{last-buy-player}`, `{last-sell-player}`             | Player last trade time                                      |
| `{last-buy-server}`, `{last-sell-server}`             | Server last trade time                                      |
| `{last-reset-buy-player}`, `{last-reset-sell-player}` | Player last reset time                                      |
| `{last-reset-buy-server}`, `{last-reset-sell-server}` | Server last reset time                                      |

Default product/sub-button buy and sell quantities are used independently until quantity selection supplies a common count. Explicit display-item stack `amount` is independent. See [Default Trade Amounts](../shops/products.md#default-trade-amounts).

`{buy-click}` and `{sell-click}` use `placeholder.click` in `config.yml`. When `placeholder.click.enabled` is true, operation availability is previewed; its error/permission/limit/funds messages come from that section. This is display text and does not bypass the actual transaction validation.

## Multi-line price formatting

Set the price separators in `config.yml`:

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

`;;` splits formatted price components into multiple lore lines. Keep buy/sell conditions on the corresponding price lines. Ordinary extra lines without a price placeholder do not gain an automatic condition from the preceding line.

## Related documentation

[Shop Menus](shop-menus.md) · [Bedrock Form - Premium](bedrock-menus-premium.md) · [Graphical Layout - Premium](../menus-getting-started/ui-layout.md)
