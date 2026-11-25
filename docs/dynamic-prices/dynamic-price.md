# 🔄Dynamic Price

Dynamic pricing lets product prices (or item amounts) adjust automatically based on server-wide or player-specific buy/sell counts, helping you simulate supply and demand, prevent farming, or build tiered pricing.

## Enable Math Calculation

Enable math evaluation in `config.yml` and allow trade counters to be used in price/amount formulas:

```yaml
math:
  enabled: true

placeholder:
  data:
    can-used-in-amount: true
```

{% hint style="info" %}
If your formulas use math functions such as `sqrt()`, `min()`, `max()`, `floor()`, `ceil()`, or `log()`, also enable `math.enable-function: true` in `config.yml`.
{% endhint %}

## Set Dynamic Values for Products

Open your shop configuration and locate the product you want to make dynamic. For example:

```yaml
  A:
    display-name: 'Custom Name!'
    price-mode: ALL
    product-mode: CLASSIC_ALL
    products:
      1:
        material: sea_lantern
    buy-prices:
      1:
        economy-type: exp
        amount: '5+({buy-times-server}-{sell-times-server})*0.2'
        max-amount: 15
        min-amount: 1
        start-apply: 0
        placeholder: '{amount} Exp'
    sell-prices:
      1:
        economy-type: exp
        amount: '5+({buy-times-server}-{sell-times-server})*0.2'
        max-amount: 15
        min-amount: 1
        start-apply: 0
        placeholder: '{amount} Exp'
```

Key configuration points:

1. Set `price-mode` to `ALL` or `ANY` (`CLASSIC_*` modes do not recalculate formulas per unit within a single bulk trade).
2. Enter your dynamic formula in `amount`. In the example above:
   * `5` is the base (initial) price.
   * `0.2` is the step change per trade: prices rise as players buy more and fall as players sell more.
   * `max-amount` and `min-amount` clamp the final price so it never goes too high or too low.

<figure><img src="../.gitbook/assets/屏幕截图 2023-10-08 201124.png" alt=""><figcaption></figcaption></figure>

{% hint style="warning" %}
**Watch out for buy-low/sell-high arbitrage**: never compare only the initial single-item spread—always check the **total cost of a bulk buy against the total payout of immediately selling that batch back**.

For example, with a buy price of `2.8 + {buy-times-server} * 0.1 - {sell-times-server} * 0.06` and a sell price of `2.38 + {buy-times-server} * 0.1 - {sell-times-server} * 0.06`: starting from 0 counts (no multiplier, no min/max clamp, +1 count per trade), buying 18 items in one batch costs `65.70`. Because the buy raises `{buy-times-server}` to 18 and the sell-side drop (`0.06`) is smaller than the buy-side increase (`0.1`), selling those 18 items straight back pays out `66.06`.
{% endhint %}

## Sell All and `max-sell-amount`

When using dynamic amounts or dynamic prices, **Sell All** needs to know how many trades the player can make right now. Providing a closed-form `max-sell-amount` formula lets the plugin compute the cap directly instead of brute-forcing trade by trade, significantly improving performance (requires `math.enabled` and `math.enable-function`; see [Math Calculate Format](../format/math-calculate-format.md) and [Single Entry - `max-sell-amount`](../shops/products-config-single-thing/README.md#max-sell-amount)).

### Dynamic `products` take amounts

Setting `product-mode: ALL` lets the item amount taken per trade change within a single Sell All batch. Starting from server sell count `0`, the first trades take 1, 2, 3, ... sea lanterns while paying a flat 5 coins each:

```yaml
product-mode: ALL
price-mode: CLASSIC_ALL
sell-times-increment: 1
products:
  1:
    material: SEA_LANTERN
    amount: '1 + {sell-times-server}'
    max-sell-amount: >-
      4 * {stock} / (
        sqrt((2 * {sell-times-server} + 1) ^ 2 + 8 * {stock})
        + 2 * {sell-times-server} + 1
      ) + 0.0000000001
sell-prices:
  1:
    economy-plugin: Vault
    amount: 5
    placeholder: '{amount} Coins'
```

**Formula note**: starting from server sell count `s`, selling `n` times takes `n * (1 + s) + n * (n - 1) / 2` items in total. The `max-sell-amount` formula solves for the largest `n` that `{stock}` can cover (for 10 sea lanterns, that is 4 trades at `s = 0` and 2 trades at `s = 2`).

### Dynamic `sell-prices` payouts

When combining `shared-price: vault-sell` with dynamic formulas:

* A `max-sell-amount` set directly on the sell-price entry overrides the template's built-in formula.
* `{sell-unit-income}` in the default template only applies to `CLASSIC_*` modes; in `ALL` mode with dynamic prices, calculate against the cumulative batch payout instead.
* The template's inherited `require-conditions` still enforce the 5-hour, daily, and weekly Vault caps before any trade completes.
* Both limited examples below assume 2-decimal rounding, `+1` sell count per trade, a sell multiplier of `1`, and a single sell-price entry; the tiny `+ 0.0000000001` offset prevents floating-point or logarithm rounding from dropping an exact whole trade.

#### Full configuration for a price that falls by a fixed step

The initial sell price is `23.80` coins. Each server purchase raises it by `1`, and each server sale lowers it by `0.60`, down to a minimum of `10` coins.

**Without Vault sell limits:**

```yaml
price-mode: ALL
product-mode: CLASSIC_ALL
products:
  1:
    material: SEA_LANTERN
    amount: 1
sell-prices:
  1:
    economy-plugin: Vault
    amount: '23.8 + {buy-times-server} - {sell-times-server} * 0.6'
    min-amount: 10
    placeholder: '{amount} Coins'
```

**With Vault sell limits**

The formula reads the [Vault sell-limit template](../features/sell-income-limits-example.md) and uses the smallest remaining allowance across the 5-hour, daily, and weekly caps. It first calculates how many trades fit at the declining prices. Once the price reaches the floor, each additional `10` coins of allowance covers one more trade.

**Apply this to an existing product**

1. Open the shop configuration in your server's `shops` directory and find the product under `items`. Set its `price-mode` to `ALL`, then replace its `sell-prices` with the example below. Put `price-mode` and `sell-prices` at the same indentation under the product.
2. Make sure `sell-times-increment` is `1` or omitted, the sell multiplier is `1`, and `math.enabled`, `math.enable-function`, and `placeholder.data.can-used-in-amount` are enabled in `config.yml`.
3. Run `/ultimateshop reload`, reopen the shop, and press Q to test Sell All. Make sure your inventory, the product's `sell-limits`, and the global `sell.max-amount` allow the expected quantity.

```yaml
price-mode: ALL
sell-prices:
  1:
    shared-price: vault-sell
    amount: '23.8 + {buy-times-server} - {sell-times-server} * 0.6'
    min-amount: 10
    max-sell-amount: >-
      min(
        max(0, ceil((138 + 10 * {buy-times-server} - 6 * {sell-times-server}) / 6)),
        floor((
          max(100, 238 + 10 * {buy-times-server} - 6 * {sell-times-server}) + 3
          - sqrt(max(
            0,
            (max(100, 238 + 10 * {buy-times-server} - 6 * {sell-times-server}) + 3) ^ 2
            - 120 * max(0, min(
              {require-conditions.1.value} - {custom_sell_5h},
              {require-conditions.2.value} - {custom_sell_daily},
              {require-conditions.3.value} - {custom_sell_weekly}
            ))
          ))
        ) / 6 + 0.0000000001)
      )
      + floor(max(
        0,
        min(
          {require-conditions.1.value} - {custom_sell_5h},
          {require-conditions.2.value} - {custom_sell_daily},
          {require-conditions.3.value} - {custom_sell_weekly}
        ) / 10
        - max(0, ceil((138 + 10 * {buy-times-server} - 6 * {sell-times-server}) / 6))
          * (241 + 10 * {buy-times-server} - 6 * {sell-times-server}
            - 3 * max(0, ceil((138 + 10 * {buy-times-server} - 6 * {sell-times-server}) / 6))) / 100
      ))
```

To check the result, start with both trade counters at `0`, `2000` coins left under the 5-hour cap, at least `2000` coins left under each other cap, and enough stock and sell-count allowance. One Sell All should complete `183` trades and pay `1995.60` coins. The remaining `4.40` coins of allowance cannot cover another trade.

**Adjust the caps or prices**

* To change a revenue cap, edit the corresponding `value` under `require-conditions` in `shared_prices/vault-sell.yml`. Entry `1` is the 5-hour cap, `2` is the daily cap, and `3` is the weekly cap. The formula reads these values.
* To scale the original `2.38` / `+0.10` / `−0.06` / `1` prices by a factor of `m`, multiply all three terms of `amount` and `min-amount` by `m`. In the formula, change `120` to `(1200 / m)` and the `/ 10` after the minimum of the three remaining allowances to `/ m`. Substitute the numeric factor for `m` in YAML. Changing an individual price term or the sell-counter increment requires a new formula.

#### Full configuration for a price that falls by a fixed ratio

Each sale divides the price by `1.5`, down to a floor of `100`.

**Without Vault sell limits:**

```yaml
price-mode: ALL
product-mode: CLASSIC_ALL
products:
  1:
    material: SEA_LANTERN
    amount: 1
sell-prices:
  1:
    economy-plugin: Vault
    amount: '100 * (1.5 ^ ({buy-times-server} - {sell-times-server}))'
    min-amount: 100
    placeholder: '{amount} Coins'
```

**With Vault sell limits (reading 5-hour, daily, and weekly caps):**

Reads all three caps from the [Vault template](../features/sell-income-limits-example.md), uses a logarithm to find how many trades fit under the tightest cap before reaching `100`, then divides the remaining allowance by `100`.

```yaml
price-mode: ALL
sell-prices:
  1:
    shared-price: vault-sell
    amount: '100 * (1.5 ^ ({buy-times-server} - {sell-times-server}))'
    min-amount: 100
    max-sell-amount: >-
      floor(
        max(0, {buy-times-server} - {sell-times-server})
        - log(max(
          1,
          1.5 ^ max(0, {buy-times-server} - {sell-times-server})
          - max(0, min(
            {require-conditions.1.value} - {custom_sell_5h},
            {require-conditions.2.value} - {custom_sell_daily},
            {require-conditions.3.value} - {custom_sell_weekly}
          )) / 300
        )) / log(1.5) + 0.0000000001
      )
      + floor(
        max(0, min(
          {require-conditions.1.value} - {custom_sell_5h},
          {require-conditions.2.value} - {custom_sell_daily},
          {require-conditions.3.value} - {custom_sell_weekly}
        ) - 300 * (
          1.5 ^ max(0, {buy-times-server} - {sell-times-server}) - 1
        )) / 100
      )
```

## Available Placeholders

In shop configs, `amount` under `buy-prices` / `sell-prices` as well as `buy-limits` / `sell-limits` all support placeholders (including PlaceholderAPI) and [Math Calculate Format](../format/math-calculate-format.md).

Common built-in counter and timestamp placeholders (see [Built-in Placeholders](../placeholders/built-in-placeholders.md)):

* `{buy-times-player}` / `{buy-times-server}`
* `{sell-times-player}` / `{sell-times-server}`
* `{buy-total-player}` / `{buy-total-server}` <mark style="color:red;">**- Premium, 3.9.0+**</mark>
* `{sell-total-player}` / `{sell-total-server}` <mark style="color:red;">**- Premium, 3.9.0+**</mark>
* `{last-buy-player}` / `{last-buy-server}` <mark style="color:red;">**- Premium**</mark>
* `{last-sell-player}` / `{last-sell-server}` <mark style="color:red;">**- Premium**</mark>
* `{last-buy-reset-player}` / `{last-buy-reset-server}` <mark style="color:red;">**- Premium, 3.9.0+**</mark>
* `{last-sell-reset-player}` / `{last-sell-reset-server}` <mark style="color:red;">**- Premium, 3.9.0+**</mark>

{% hint style="info" %}
**Difference between `times` and `total` placeholders:**

* **`times`**: resets to the configured starting value (usually `0`) whenever a reset triggers.
* **`total`**: keeps accumulating across resets; modifying buy/sell counts via commands or other methods also affects `total`. It resets automatically if it reaches the `int` limit.
{% endhint %}

To reference another product's trade counts (cross-product pricing), use the new UltimateShop PlaceholderAPI format without `{}`:

```yaml
    buy-prices:
      1:
        economy-plugin: Vault
        amount: '15 - {sell-times-player} * 0.1 + %ultimateshop_farming_B_sell-times-player% * 0.1'
        # Use the new format without { and } symbols.
        placeholder: '{amount}$'
        start-apply: 0
```

{% hint style="tip" %}
If the referenced product is in the same open menu, set `menu.shop.click-update` to `true` in `config.yml`, otherwise its displayed price will not refresh immediately after selling product B.
{% endhint %}

## Per-Player Dynamic Pricing

Whether dynamic prices are shared or per-player depends on the placeholders in your formula. The examples above use `{buy-times-server}` (server-wide). Replace `server` with `player` (for example `{buy-times-player}`) to give each player their own independent dynamic price. See [Built-in Placeholders](../placeholders/built-in-placeholders.md).

## Set Buy/Sell Limits

For `buy-limits` and `sell-limits` examples, see [Products](../shops/products.md).

## Reset Dynamic Prices

Because dynamic prices are computed by formulas, you reset them by resetting the counter placeholders they read. If your formula uses `{buy-times-server}` or `{sell-times-player}`, you can reset them by:

* Configuring automatic scheduled resets in [Product Config: Buy/Sell Times Reset](../shops/product-config-buy-sell-times-reset.md).
* Running `/shop setbuytimes` or `/shop setselltimes` manually.
