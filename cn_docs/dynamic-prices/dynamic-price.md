# 🔄动态价格

## 启用数学运算

修改 `config.yml`：

* 将

```yaml
math:
  # Enabled base math calculate?
  # Will support + - * / only.
  enabled: false
```

改为

```yaml
math:
  # Enabled base math calculate?
  # Will support + - * / only.
  enabled: true
```

* 将

```yaml
placeholder:
  data:
    can-used-in-amount: false
```

改为

```yaml
placeholder:
  data:
    can-used-in-amount: true
```

## 为商品配置动态数值

打开商店配置，找到要启用动态价格的商品。例如：

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

首先将 `price-mode` 设为 `ALL` 或 `ANY`。

然后在 `amount` 中填写需要的数值或公式。上面的常见动态价格公式可以这样理解：

* `5` 是基础价格，即初始价格。
* `0.2` 表示每当一位玩家购买或出售一个商品时，价格上涨或下跌的幅度。
* 通过 `max-amount` 和 `min-amount` 限制价格，避免过高或过低。

<figure><img src="../.gitbook/assets/屏幕截图 2023-10-08 201124.png" alt=""><figcaption></figcaption></figure>

公式可以自由设置，但必须仔细检查玩家是否能通过直接买入再卖出来获利。因此，`max-amount` 和 `min-amount` 十分重要。例如：

* 购买价格公式：`2.8+{buy-times-server}*0.1-{sell-times-server}*0.06`
* 出售价格公式：`2.38+{buy-times-server}*0.1-{sell-times-server}*0.06`

此时，`max-amount` 应低于第 <mark style="color:red;">**n**</mark> 次购买或出售时的价格。

如何确定 <mark style="color:red;">**n**</mark>？它需要满足：

\[(**购买基础价格** - **出售基础价格**)/(**购买涨价幅度** - **出售降价幅度**)] >= 从 1 累加到 n 的总和。

在此示例中：`(2.8-2.38)/(0.1-0.06) >= 1+2+3+4`；累加到 5 时便不再满足，所以 <mark style="color:red;">n</mark> 最大为 4。

请记住：<mark style="color:red;">不同公式需要合理设置各自的 max-amount 与 min-amount。最稳妥的方式是让每次购买和出售的价格变化幅度相同。</mark>

另一种常见公式按比例改变价格，例如 `100 * (1.5 ^ ({buy-times-server}-{sell-times-server}))`：

* **100** 为基础价格。
* **1.5** 为每次购买或出售后的价格变化倍率。&#x20;
* 在没有出售记录时，首次购买价格为 **100**，然后为 **150（+50%）**，再然后为 **225（150 + 150 \* 50%）**。
* 别忘了把 `min-amount` 设为接近 **100** 的数值，防止价格过低。

## 可用占位符

商店配置中，`buy-prices`、`sell-prices` 的 `amount`，以及 `buy-limits`、`sell-limits` 的数值均可使用占位符（包括 PlaceholderAPI）和[数学计算格式](../format/math-calculate-format.md)。

可用的内置占位符如下，详细说明见[内置占位符](../placeholders/built-in-placeholders.md)。

* {buy-times-player}
* {buy-times-server}
* {buy-total-player} <mark style="color:red;">**- 付费版，3.9.0+**</mark>
* {buy-total-server} <mark style="color:red;">**- 付费版，3.9.0+**</mark>

{% hint style="info" %}
**times** 与 **total** 占位符的区别：

* **times** 会在每次重置后变为配置中指定的数值。
* **total** 会在重置后保留原数据并累计之前的次数。使用命令或其他方式修改购买/出售次数，也会影响 total。达到 int 类型上限时，该占位符会自动重置。
{% endhint %}

* {sell-times-player}
* {sell-times-server}
* {sell-total-player} <mark style="color:red;">**- 付费版，3.9.0+**</mark>
* {sell-total-server} <mark style="color:red;">**- 付费版，3.9.0+**</mark>
* {last-buy-player} <mark style="color:red;">**- 付费版**</mark>
* {last-buy-server} <mark style="color:red;">**- 付费版**</mark>
* {last-sell-player} <mark style="color:red;">**- 付费版**</mark>
* {last-sell-server} <mark style="color:red;">**- 付费版**</mark>
* {last-buy-reset-player} <mark style="color:red;">**- 付费版，3.9.0+**</mark>
* {last-buy-reset-server} <mark style="color:red;">**- 付费版，3.9.0+**</mark>
* {last-sell-reset-player} <mark style="color:red;">**- 付费版，3.9.0+**</mark>
* {last-sell-reset-server} <mark style="color:red;">**- 付费版，3.9.0+**</mark>

`buy-prices` 和 `sell-prices` 还支持两个选项：

* `max-amount`：价格上限，适用于动态价格。**可选。**
* `min-amount`：价格下限，适用于动态价格。**可选。**

如果使用 UltimateShop 的 PlaceholderAPI 扩展占位符，务必采用新的格式，例如：

```yaml
    buy-prices:
      1:
        economy-plugin: Vault
        amount: '15 - {sell-times-player} * 0.1 + %ultimateshop_farming_B_sell-times-player% * 0.1'
        # We use the new format without { and } symbol.
        placeholder: '{amount}$'
        start-apply: 0
```

此外，如果相关商品也出现在当前打开的菜单中，需要将 `menu.shop.click-update` 设为 `true`，否则出售商品 B 后，该价格不会自动更新。

## 每位玩家独立的动态价格

只要公式中的占位符按玩家独立返回数值，计算出的价格自然也就按玩家独立。上例使用 `{buy-times-server}` 等全服占位符，将其中的 `server` 换成 `player`，即可使用玩家自己的购买次数。详细说明见[占位符](../placeholders/built-in-placeholders.md)。

## 设置商品购买/出售上限

说明与示例见[商品](../shops/products.md)。

## 重置动态价格

动态价格由公式计算，因此无法直接重置价格本身，而应重置公式中使用的占位符。如果按照本节示例使用 `{buy-times-server}` 等占位符，就可以通过重置对应次数来恢复价格。

可以使用自动重置功能，详见[购买/出售次数重置](../shops/product-config-buy-sell-times-reset.md)，也可以执行 `/shop setbuytimes/setselltimes`。
