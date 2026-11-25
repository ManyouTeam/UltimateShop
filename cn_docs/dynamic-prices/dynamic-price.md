# 🔄动态价格

动态价格允许商品的购买/出售价格（或物品数量）根据全服或玩家的买卖次数实时浮动，用来模拟供需变化、抑制刷钱或实现阶梯定价。

## 启用数学运算

在 `config.yml` 中开启数学运算，并允许在价格/数量公式中使用买卖次数占位符：

```yaml
math:
  enabled: true

placeholder:
  data:
    can-used-in-amount: true
```

{% hint style="info" %}
如果后续公式中使用了 `sqrt()`、`min()`、`max()`、`floor()`、`ceil()`、`log()` 等数学函数，还需要在 `config.yml` 中开启 `math.enable-function: true`。
{% endhint %}

## 为商品配置动态数值

打开商店配置文件，找到需要启用动态价格的商品。例如：

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

配置要点：

1. 将 `price-mode` 设为 `ALL` 或 `ANY`（`CLASSIC_*` 模式不会在单笔批量交易中逐次重新计算公式）。
2. 在 `amount` 中填写动态公式。以上方公式为例：
   * `5` 为基础价格（初始价格）。
   * `0.2` 为每成交一笔时的涨跌幅度：买得越多价格越高，卖得越多价格越低。
   * `max-amount` 与 `min-amount` 用于限制价格上下限，防止价格失控。

<figure><img src="../.gitbook/assets/屏幕截图 2023-10-08 201124.png" alt=""><figcaption></figcaption></figure>

{% hint style="warning" %}
**设计公式时注意防范「低买高卖」**：不能只比较单件的初始买卖差价，而要核算**整批先买后卖的总收支**。

例如，若购买价格设为 `2.8 + {buy-times-server} * 0.1 - {sell-times-server} * 0.06`，出售价格设为 `2.38 + {buy-times-server} * 0.1 - {sell-times-server} * 0.06`：从买卖计数都为 0 开始（无倍率、无上下限且每笔计数 +1），批量买入 18 件共花费 `65.70`；由于买入过程抬高了 `{buy-times-server}` 且卖价跌幅（`0.06`）小于买价涨幅（`0.1`），此时再将这 18 件全部卖回，反而能收到 `66.06`。
{% endhint %}

## Sell All 与 `max-sell-amount`

在动态数量或动态价格下，玩家使用 **Sell All（全部出售）** 时，插件需要计算当前最多能卖出多少笔。通过 `max-sell-amount` 直接给出最大可售笔数的数学公式，可以避免插件逐笔穷举试算，显著提升性能（需同时开启 `math.enabled` 与 `math.enable-function`；函数说明见[数学计算格式](../format/math-calculate-format.md)，字段详情见 [单项内容 - `max-sell-amount`](../shops/products-config-single-thing/README.md#max-sell-amount)）。

### `products` 的动态扣除数量

将 `product-mode` 设为 `ALL`，可以让同一批出售交易中每笔扣除的物品数量逐次递增。在下面的示例里，服务器出售计数从 0 开始时，前几笔会依次扣除 1、2、3 个海晶灯，每笔固定获得 5 金币：

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

**公式说明**：当服务器出售计数从 `s` 开始时，连续卖出 `n` 笔共需扣除 `n * (1 + s) + n * (n - 1) / 2` 个物品。上述 `max-sell-amount` 公式反解了 `{stock}` 足够支撑的最大笔数 `n`（例如背包有 10 个海晶灯时，`s = 0` 最多卖 4 笔，`s = 2` 最多卖 2 笔）。

### `sell-prices` 的动态卖价

在卖价项中使用 `shared-price: vault-sell` 并配合动态公式时，需注意以下几点：

* 卖价项单独配置的 `max-sell-amount` 会覆盖 `vault-sell` 模板自带的公式。
* 模板内置公式中的 `{sell-unit-income}` 仅适用于 `CLASSIC_*` 模式；在 `ALL` 模式下使用动态价格时，公式必须按整批回收的累计收入来反推笔数。
* 模板继承的 `require-conditions` 仍会在成交前校验 5 小时、每日和每周三项限额。
* 以下两个带限额的示例均按「保留两位小数、每笔卖出计数 +1、售价倍率为 1、且仅此一个卖价条目」推导；公式末尾的极小补偿（`+ 0.0000000001`）用于消除浮点/对数舍入误差，避免把本可成交的最后一整笔舍掉。

#### 等差跌价的完整配置

回收单价从 `23.80` 金币起算。服务器每买入一笔，单价加 `1`。每卖出一笔，单价减 `0.60`，最低为 `10` 金币。

**不使用 Vault 回收限额：**

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

**使用 Vault 回收限额**

公式读取 [Vault 回收限额模板](../features/sell-income-limits-example.md)，比较 5 小时、每日和每周的剩余额度，取最小值。先按逐笔跌价的累计收入反推可回收笔数，达到保底价后，剩余额度每满 `10` 金币可再回收一笔。

**应用到已有商品**

1. 打开服务器的 `shops` 目录中对应的商店配置，在 `items` 下找到要修改的商品。将该商品的 `price-mode` 设为 `ALL`，再用下方示例替换它的 `sell-prices`。`price-mode` 和 `sell-prices` 写在商品下的同一级。
2. 确认 `sell-times-increment` 为 `1` 或未设置，售价倍率为 `1`，并在 `config.yml` 中开启 `math.enabled`、`math.enable-function` 和 `placeholder.data.can-used-in-amount`。
3. 执行 `/ultimateshop reload`，重新打开商店，按 Q 测试全部回收。测试前确认背包库存足够，且商品的 `sell-limits` 和全局 `sell.max-amount` 允许卖出预期数量。

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

买卖计数都为 `0`、5 小时剩余额度为 `2000` 金币、其余两个周期的剩余额度不少于 `2000` 金币，且库存与出售数量限额充足时，一次全部回收可成交 `183` 笔，到账 `1995.60` 金币。剩余 `4.40` 金币的额度不足以再成交一笔。

**调整额度或价格**

* 调整收入限额时，修改 `shared_prices/vault-sell.yml` 中 `require-conditions` 的对应 `value`。编号 `1` 为 5 小时，`2` 为每日，`3` 为每周。公式会读取这些值。
* 如果要把原始 `2.38` / `+0.10` / `−0.06` / `1` 这组价格整体改为 `m` 倍，将 `amount` 的三项和 `min-amount` 一起乘以 `m`，并将公式中的 `120` 改为 `(1200 / m)`、将三项剩余额度最小值后的 `/ 10` 改为 `/ m`。在 YAML 中用实际倍数替换 `m`。只调整某一项价格或改动出售计数增量时，需要重新推导公式。

#### 按比例跌价的完整配置

每卖出一笔，单价除以 `1.5`，保底为 `100`。

**不使用 Vault 回收限额：**

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

**使用 Vault 回收限额（同时读取 5 小时、每日、每周三项额度）：**

公式直接读取 [Vault 回收限额模板](../features/sell-income-limits-example.md)中的三个周期上限，先用对数计算单价降至 `100` 之前在当前最紧额度内能卖出的笔数，再将剩余额度除以保底价 `100`。

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

## 可用占位符

在商店配置中，`buy-prices` / `sell-prices` 的 `amount` 以及 `buy-limits` / `sell-limits` 的数值均支持使用占位符（含 PlaceholderAPI）与[数学计算格式](../format/math-calculate-format.md)。

常用的内置计数与时间占位符如下（完整说明见[内置占位符](../placeholders/built-in-placeholders.md)）：

* `{buy-times-player}` / `{buy-times-server}`
* `{sell-times-player}` / `{sell-times-server}`
* `{buy-total-player}` / `{buy-total-server}` <mark style="color:red;">**- 付费版，3.9.0+**</mark>
* `{sell-total-player}` / `{sell-total-server}` <mark style="color:red;">**- 付费版，3.9.0+**</mark>
* `{last-buy-player}` / `{last-buy-server}` <mark style="color:red;">**- 付费版**</mark>
* `{last-sell-player}` / `{last-sell-server}` <mark style="color:red;">**- 付费版**</mark>
* `{last-buy-reset-player}` / `{last-buy-reset-server}` <mark style="color:red;">**- 付费版，3.9.0+**</mark>
* `{last-sell-reset-player}` / `{last-sell-reset-server}` <mark style="color:red;">**- 付费版，3.9.0+**</mark>

{% hint style="info" %}
**`times` 与 `total` 占位符的区别：**

* **`times`**：每次次数重置后，会恢复为配置中指定的初始值（通常为 0）。
* **`total`**：重置后仍保留并持续累计历史次数；通过命令或其他方式修改买卖次数时也会同步影响 `total`。当数值达到 `int` 类型上限时会自动重置。
{% endhint %}

如果需要跨商品联动（例如商品 A 的价格受商品 B 的买卖次数影响），请使用 UltimateShop 的新版 PlaceholderAPI 占位符格式（不含 `{}`）：

```yaml
    buy-prices:
      1:
        economy-plugin: Vault
        amount: '15 - {sell-times-player} * 0.1 + %ultimateshop_farming_B_sell-times-player% * 0.1'
        # 使用不含 { 和 } 的新版占位符格式
        placeholder: '{amount}$'
        start-apply: 0
```

{% hint style="tip" %}
如果商品 B 与商品 A 位于同一个打开的菜单中，请将 `config.yml` 中的 `menu.shop.click-update` 设为 `true`，否则玩家出售商品 B 后，商品 A 显示的价格不会立即刷新。
{% endhint %}

## 每位玩家独立的动态价格

动态价格是否按玩家独立计算，取决于公式中使用的占位符类型。前文示例使用的是 `{buy-times-server}` 等**全服**占位符；只需将占位符中的 `server` 替换为 `player`（如 `{buy-times-player}`），即可让每位玩家拥有各自独立的动态价格。详见[内置占位符](../placeholders/built-in-placeholders.md)。

## 设置商品购买/出售上限

关于 `buy-limits` 与 `sell-limits` 的具体配置与示例，请参见[商品配置](../shops/products.md)。

## 重置动态价格

由于动态价格是由公式实时计算得出的，因此无法直接「重置价格数值」，而是通过**重置公式中使用的次数占位符**来恢复初始价格。

如果公式使用了 `{buy-times-server}`、`{sell-times-player}` 等计数占位符，可以通过以下两种方式重置：

* 配置自动定时重置，详见[购买/出售次数重置](../shops/product-config-buy-sell-times-reset.md)。
* 在游戏内或控制台手动执行 `/shop setbuytimes` / `/shop setselltimes`。
