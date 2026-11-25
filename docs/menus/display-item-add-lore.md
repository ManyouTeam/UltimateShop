# 🥉Display Item Add Lore

Auto-generated lore (`add-lore`) appends prices, limits, and click hints below a product's own `display-item.lore`. Inventory menus, native product info, and graphical tooltips can display these lines and filter them per presentation or product state.

Compact prices are configured separately: Bedrock Form lists use `bedrock.price-extra-line` and graphical card faces use `ui.products.price`.

## Global vs. Per-Product Lore

Global defaults live in `config.yml`:

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

Built-in `['{lang}']` loads the active language's lines; a product's root `add-lore` replaces them for that product:

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

Set `display-item.modify-lore: false` to disable auto-lore on a specific display variant. Buy More confirm buttons use their own `modify-lore` toggle at the control root (default `true`).

## Condition Symbols

Place condition symbols at the start of a line for readability. Every condition on a line must pass for that line to appear (the symbols are stripped on render; lines without symbols always show).

| Symbol | Shown When |
| --- | --- |
| `@a` | Buy price exists |
| `@b` | Display sell price exists |
| `@c` / `@d` | Player / server buy limit is configured |
| `@e` / `@f` | Player / server sell limit is configured |
| `@g` / `@h` | Non-zero player buy / sell limit is reached |
| `@i` / `@j` | Non-zero server buy / sell limit is reached |
| `@k` | Buy More is enabled for this product |
| `@l` | Favouriting is allowed for this product |
| `@m` | Display sell price exists and Sell All is enabled |
| `@n` | Active action supports at least one priced direction |
| `@o` | Opened inside a Favourite menu |
| `@p` / `@q` | Inside / outside a Buy More lore context |
| `@r[shared-price-id]` | Product price uses the specified Shared Price template (empty matches any; comma-separated for multiple) |
| `@u` / `@v` | Active action supports buying / selling |
| `@w` | Explicit click-action context is provided |
| `@x` / `@y` | Player is / is not detected as Bedrock |
| `@t[presentation]` | Active presentation matches any listed name |
| `@z[multiplier-id]` | Specified sell multiplier is active |

Symbols are lowercase. Wrap in parentheses to negate (e.g. `(@a)` or `(@z[vip])`), and chain multiple symbols for logical AND:

```yaml
add-lore:
  - '@a@t[inventory]&eBuy: {buy-price}'
  - '@g&cPlayer purchase limit reached'
  - '(@a)&7This product cannot be purchased'
```

Conditions apply per line—if a divider or blank line should hide with a section, give it the same condition.

<a id="presentation-conditions"></a>

## Presentation Conditions (`@t[...]`)

| `@t[...]` Value | Render Context |
| --- | --- |
| `inventory` | Chest menu lore |
| `form` | Bedrock product info |
| `dialog` | Native Dialog info |
| `item-action-list` | Native item-action list (when provided) |
| `ore` | Ore card body / details |
| `ore-hover` | Ore hover tooltip |
| `arcmenu` | ArcMenu body / details |
| `arcmenu-hover` | ArcMenu hover tooltip |

Separate multiple targets with commas (e.g. `@t[inventory,ore]`; matching is case-insensitive and trimmed, and `dialog` does not automatically include `ore` or other dialog-based contexts).

* Guard Inventory click hints with `@t[inventory]`; `@y` also matches Java players in ArcMenu and Ore.
* These conditions filter auto-generated `add-lore` lines only, never static `display-item.lore`.
* Ore body lore is capped by `ui.products.card.lore-lines`, while hover tooltips still show all eligible lines; ArcMenu card faces use `ui.products.price` for compact pricing.

## Product Placeholders

| Placeholder | Meaning |
| --- | --- |
| `{buy-price}`, `{sell-price}` | Prices calculated for each direction's trade count |
| `{buy-amount}`, `{sell-amount}` | Independent buy / sell trade counts |
| `{amount}` | Display amount (affected by `display-item.calculate-amount`) |
| `{item-name}` | Product display name |
| `{buy-click}`, `{sell-click}` | Generated action availability / click hints |
| `{buy-limit-player}`, `{sell-limit-player}` | Player limits |
| `{buy-limit-server}`, `{sell-limit-server}` | Server limits |
| `{buy-times-player}`, `{sell-times-player}` | Player current-period counts |
| `{buy-times-server}`, `{sell-times-server}` | Server current-period counts |
| `{buy-total-player}`, `{sell-total-player}` | Player cumulative totals |
| `{buy-total-server}`, `{sell-total-server}` | Server cumulative totals |
| `{buy-refresh-player}`, `{sell-refresh-player}` | Player reset time display |
| `{buy-refresh-server}`, `{sell-refresh-server}` | Server reset time display |
| `{buy-next-player}`, `{sell-next-player}` | Player next reset time |
| `{buy-next-server}`, `{sell-next-server}` | Server next reset time |
| `{last-buy-player}`, `{last-sell-player}` | Player last trade time |
| `{last-buy-server}`, `{last-sell-server}` | Server last trade time |
| `{last-reset-buy-player}`, `{last-reset-sell-player}` | Player last reset time |
| `{last-reset-buy-server}`, `{last-reset-sell-server}` | Server last reset time |

Before selecting a quantity, default buy and sell amounts stay independent; once chosen, both use the selected count (explicit display-item stack sizes stay separate; see [Default Trade Amounts](../shops/products.md#default-trade-amounts)).

`{buy-click}` and `{sell-click}` read global `placeholder.click`. When `placeholder.click.enabled: true`, they preview action availability and pick condition, limit, or balance hints (trades still validate independently).

## Multi-Line Prices

Set `;;` price splitters in `config.yml` to break formatted multi-part prices onto separate lines:

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

Keep `@a` / `@b` on both the header line and the price line.

## Related Display Defaults

| Global `display-item` Key | Purpose |
| --- | --- |
| `auto-set-first-product` | Uses the first eligible product reward when no display item is defined |
| `calculate-amount` | Scales `{amount}` by product reward amount |
| `auto-translate-item-name` | Auto-translates vanilla display names on Paper 1.17.1+ |
| `auto-use-sprite-item-name` | Uses Java sprite item names on compatible Paper servers |

Explicit custom names always take priority. See [Localized Item Name](../features/localized-item-name-premium.md).

[Shop Menus](shop-menus.md) · [Bedrock Form](bedrock-menus-premium.md) · [Graphical UI Layout](../menus-getting-started/ui-layout.md)
