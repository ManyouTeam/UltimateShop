# 🥉Display Item Add Lore

{% hint style="info" %}
This page showing you the rematered display item add lore feature stating from plugin version 4.0.0.
{% endhint %}

## General Setting

You can set it at `config.yml` file.

Default example:

```yaml
  add-lore:
    - '@n '
    - '@a&ePurchase: {buy-price}'
    - '@b&eSell: {sell-price}'
    - '@c&#FF7777Player Buy Stock: {buy-times-player}/{buy-limit-player}'
    - '@d&#FF7777Server Buy Stock: {buy-times-server}/{buy-limit-server}'
    - '@e&#FF7777Player Sell Limit: {sell-times-player}/{sell-limit-player}'
    - '@f&#FF7777Server Sell Limit: {sell-times-server}/{sell-limit-server}'
    - '@g '
    - '@g&#ff3300cCan not buy more!'
    - '@g&8Refresh Time: {buy-refresh-player}'
    - '@i '
    - '@i&#ff3300Sold Out!'
    - '@i&8Refresh Time: {buy-refresh-server}'
    - '@h '
    - '@h&#ff3300Can not sell more!'
    - '@h&8Refresh Time: {sell-refresh-player}'
    - '@j'
    - '@j&#ff3300Can not sell more for server!'
    - '@j&8Refresh Time: {sell-refresh-server}'
    - '@n '
    - '@a@u@y{buy-click}'
    - '@b@v@y{sell-click}'
    - '@k@q@y&#FFFACDRight-Shift click to pick amount!'
    - '@m@v@y&#FFFACDDrop (Q key) to sell all!'
    - '(@n)&c&l:( Can not do this'
    - '(@a)@u@p&cThis item can not be purchased'
    - '(@b)@v@p&cThis item can not be sold'
```

## Per Product Setting

For products with `default-buy-amount` / `default-sell-amount`, `{buy-price}`, `{sell-price}`, `{buy-click}`, and `{sell-click}` use the corresponding default trade count. Sub buttons use their own override or inherit from the target product. Use `{buy-amount}` and `{sell-amount}` to show the counts separately:

```yaml
add-lore:
  - '@a&eBuy {buy-amount} trades: {buy-price}'
  - '@b&aSell {sell-amount} trades: {sell-price}'
```

In quantity selection menus, both placeholders use the selected amount. Explicit `display-item.amount` remains independent. When it is omitted and the default buy and sell counts match, the icon automatically uses that common count. See [Default Trade Amounts](../shops/products.md#default-trade-amounts).

You can set different add lore format for each product, add the `add-lore` option in the product config. Check [shops](../shops/shops.md) page product **B** to find the example.

```yaml
items:
  B:
    display-item:
      material: BREAD
      name: '&cSuper Bread'
    display-name: "Bread"
    add-lore: # <--- Custom Add Lore for this product
      - '@a&ePurchase: {buy-price}'
      - '@b&eSell: {sell-price}'
      - '&eDrop to buy, right to sell'
```

## Hide

You can use `display-item.modify-lore` option to hide the display item add lore.

```yaml
A:
  display-item:
    material: APPLE
    modify-lore: false # This line
  buy-prices:
    # The section of buy price
  sell-prices:
    # The section of sell price
```

## Prefix - Conditional Symbol

Each line start with `@+lower case` will be consider as conditional line. We will only display this line when this condition is meet. Starting from version **4.0.0**, multiple conditional symbols can be set for each line.

@a - This product has buy price. (Means has `buy-prices` section)

@b - This product has sell price. (Means has `sell-prices` section)

@c - This product has player buy limit. (Means has `buy-limits.player` option)

@d - This product has server buy limit. (Means has `buy-limits.global` option)

@e - This product has player sell limit. (Means has `sell-limits.player` option)

@f - This product has server sell limit. (Means has `sell-limits.global` option)

@g - This product has reached player buy limit.

@h - This product has reached player sell limit.

@i - This product has reached server buy limit.

@j - This product has reached server sell limit.

@k - This product has enabled buy more feature.

@l - This product can use favourite feature.

@m - This product can use sell all feature.

@n - Buy/sell price (corresponding to the Click type) is valid. For example, buy click type require buy price is valid.

@o - Player is opening favourite menu.

@p - Player is opening buy more menu.

@q - Player is **not** opening buy more menu.

@x - Player is from bedrock version. Require using [Bedrock Menus](bedrock-menus-premium.md) feature.

@y - Player is **not** from bedrock version. Require using [Bedrock Menus](bedrock-menus-premium.md) feature.

@u - This button can buy product.&#x20;

@v - This button can sell product.&#x20;

@w - This button exist in buy more menu and already set `click-type` option.

@t\[ore] - Only show in the specified menu presentation. Supported targets are `inventory`, `form`, `dialog`, `item-action-list`, `ore` and `ore-hover`. `ore` targets the Ore product card body; `ore-hover` targets its hover tooltip. Match several targets with commas, for example `@t[inventory,ore]`. Matching ignores case and spaces around each value. `dialog` matches ordinary Dialog menus; use `dialog,item-action-list,ore,ore-hover` to include all Dialog presentations. `(@t[ore])` hides the line from the Ore card body while retaining it in tooltips and other menus. With no active menu, a positive presentation condition does not match.

This condition reuses the presentation names from button `menu-visibility`, with `ore-hover` added for the separate Ore tooltip. It applies to both global `display-item.add-lore` and per-product `add-lore`, and can be combined with existing price/stock conditions. Lines without a presentation condition are shown in both the body and tooltip when their other conditions match. The card body uses auto-added lore; the tooltip also retains the item's original lore. No fixed set of fields is imposed:

```yaml
display-item:
  add-lore:
    - '@a@t[inventory,ore,ore-hover]&ePurchase: {buy-price}'
    - '@b@t[inventory,ore,ore-hover]&eSell: {sell-price}'
    - '@t[inventory]&7Left click to buy'
    - '@t[ore]&7View details to buy or sell'
    - '@t[ore-hover]&7Additional tooltip information'
    - '(@t[ore])&7This line is hidden in Ore'
```

@z\[vip] - Only show when [multiplier](../shops/sell-multiplier-premium.md) id `vip` is active.

(@z\[vip]) - Only show when [multiplier](../shops/sell-multiplier-premium.md) id `vip` is not active.

## Negation

You can reverse the conditional symbol by adding English parentheses before and after it. For example: `(@a)` represents the reversal of `@a`. (means this product does not has buy price)

{% hint style="danger" %}
Suffix has been removed in display item add lore from 4.0.0.
{% endhint %}

## Multi Line Price&#x20;

Use `;;` symbol if you want to display price in multi lines.

```yaml
placeholder:
  price:
    split-symbol-any: ';;' # <--- Changed this in config.yml
    split-symbol-all: ';;' # <--- Changed this in config.yml
    unknown: "Unknown"
```

You need also update your `add-lore` settings:

```yaml
  add-lore:
    - '@n '
    - '@a&ePurchase: '
    - '@a   &7[-] &f{buy-price}'
    - '@b&eSell:'
    - '@a   &7[-] &f {sell-price}'
```
