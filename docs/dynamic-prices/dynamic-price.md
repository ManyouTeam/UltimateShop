# 🔄Dynamic Price

## Enable Math Feature

Change your `config.yml` file:

* Change

```yaml
math:
  # Enabled base math calculate?
  # Will support + - * / only.
  enabled: false
```

to

```yaml
math:
  # Enabled base math calculate?
  # Will support + - * / only.
  enabled: true
```

* Change&#x20;

```yaml
placeholder:
  data:
    can-used-in-amount: false
```

to

```yaml
placeholder:
  data:
    can-used-in-amount: true
```

## Set dynamic value for your product configs

Open one of your shop configs, find the product you want to enable dynamic price.

Like I want to enable for this product:

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

First, you need set `price-mode` to `ALL` or `ANY`.

Set amount option to the value you want here, like put a common dynamic formula here, if your math is not good, let me explain it:

* 5 is base price, which means the start price.
* 0.2 is each time one player buy or sell one this product, then the price will up or down.
* We also added `max-amount` and `min-amount` option, to avoid price is too high or too low.

<figure><img src="../.gitbook/assets/屏幕截图 2023-10-08 201124.png" alt=""><figcaption></figcaption></figure>

The formula you set here is not limited, but you need carefully check whether player can earn money by purchase them then just sell them without do anything. Because of this, the `max-amount` and `min-amount` option is very important. For example, you set:

* Buy Price Formula: `2.8+{buy-times-server}*0.1-{sell-times-server}*0.06`
* Sell Price Formula: `2.38+{buy-times-server}*0.1-{sell-times-server}*0.06`

In this way, your `max-amount` option should be lower than the price at the <mark style="color:red;">**n**</mark>(th) purchase or sellling time.

How to get the number called <mark style="color:red;">**n**</mark>? This number must meet:

\[(**Buy Price Base Price** - **Sell Price Base Price**)/(**Buy Up Price** - **Sell Down Price**)] >= Accumulation from 1 to n.

In this example: `(2.8-2.38)/(0.1-0.06) >= 1+2+3+4` (if up to 5, the formula will not meet), so <mark style="color:red;">n</mark> max number is 4 in this example.

Remerber: <mark style="color:red;">Your different formulas require reasonable setting of different values in max-amount and min-amount. The safest approach is to set the price of each purchase or sellling change to the same value.</mark>

Another common dynamic price formula is price changed based on the percentage, like: `100 * (1.5 ^ ({buy-times-server}-{sell-times-server}))`. In this example:

* **100** is base price.
* **1.5** is the multiplier of the price after each purchase or sellling.&#x20;
* In this example, first time buy with no sell is **100**, then is **150 (+50%)**, then is **225 (150 + 150 \* 50%)**.
* Don't forget set `min-amount` option to a number near **100** to avoid the price become too low!

## Available Placeholders

You can set placeholders (including PlaceholderAPI) and [Math Calculate Format](../format/math-calculate-format.md) in `buy-prices`, `sell-prices` section's `amount` option and `buy-limits`, `sell-limits` section's value in shop configs.

Available built-in placeholder, for more info about them, please view [Built-In Placeholders](../placeholders/built-in-placeholders.md) page.

* {buy-times-player}
* {buy-times-server}
* {buy-total-player} <mark style="color:red;">**- PREMIUM, 3.9.0+**</mark>
* {buy-total-server} <mark style="color:red;">**- PREMIUM, 3.9.0+**</mark>

{% hint style="info" %}
Difference between **times** placeholder and **total** placeholder:

* **times** placeholder will be reset to the value you set after each reset.
* **total** placeholder will keep data after reset, and will accumulate previous times. Use command to set buy/sell times or other way will still effect total placeholder. This placeholder will auto reset when reaching the upper limit of int type data.
{% endhint %}

* {sell-times-player}
* {sell-times-server}
* {sell-total-player} <mark style="color:red;">**- PREMIUM, 3.9.0+**</mark>
* {sell-total-server} <mark style="color:red;">**- PREMIUM, 3.9.0+**</mark>
* {last-buy-player} <mark style="color:red;">**- PREMIUM**</mark>
* {last-buy-server} <mark style="color:red;">**- PREMIUM**</mark>
* {last-sell-player} <mark style="color:red;">**- PREMIUM**</mark>
* {last-sell-server} <mark style="color:red;">**- PREMIUM**</mark>
* {last-buy-reset-player} <mark style="color:red;">**- PREMIUM, 3.9.0+**</mark>
* {last-buy-reset-server} <mark style="color:red;">**- PREMIUM, 3.9.0+**</mark>
* {last-sell-reset-player} <mark style="color:red;">**- PREMIUM, 3.9.0+**</mark>
* {last-sell-reset-server} <mark style="color:red;">**- PREMIUM, 3.9.0+**</mark>

Also in `buy-prices` and `sell-prices` section, you can set new 2 options:

* max-amount: Price max amount, useful for dynamic prices. **Optional.**
* min-amount: Price min amount, useful for dynamic prices. **Optional.**

Please carefully note that if you want to use our PlaceholderAPI extansion's placeholder, you have to use our new format, for example:

```yaml
    buy-prices:
      1:
        economy-plugin: Vault
        amount: '15 - {sell-times-player} * 0.1 + %ultimateshop_farming_B_sell-times-player% * 0.1'
        # We use the new format without { and } symbol.
        placeholder: '{amount}$'
        start-apply: 0
```

Additionally, you need to set `menu.shop.click-update` to `true` if the related to product is also in the menu you opened. Otherwise this price won't auto update after you sell B product.

## Dynamic Price per player

As long as you can ensure that the placeholder used in the formula is per player output value, the calculated price result will naturally be per player. In the above example, we used global placeholders such as `{buy-times-server}`, and you only need to replace the `server` with the `player` to display the player's own buy times value. The relevant content is explained in detail in the [Placeholders](../placeholders/built-in-placeholders.md) page.

## Set buy / sell limits for your products

Please view [Products](../shops/products.md) page for info and example.

## Reset dynamic price

Many people ask this question, and I feel that the person asking this question simply does not understand the essence of UltimateShop. The dynamic price is determined by a formula, so you cannot reset the price directly. To reset the price, the placeholders used in your formula must be reset. If you use placeholders such as `{buy-times-server}` exactly as described in this section, they can be reset.

You can reset the buy times or sell times by our auto reset feature, you can find more info at [Buy/Sell Times Reset](../shops/product-config-buy-sell-times-reset.md) page or use `/shop setbuytimes/setselltimes` command
