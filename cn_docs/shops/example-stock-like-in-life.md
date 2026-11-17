# 🌱示例：像现实生活那样的库存

{% hint style="info" %}
这里说的是现实意义上的「有货 / 没货」。若要设置购买、出售次数上限，请看[商品](products.md)页面。
{% endhint %}

UltimateShop 并不单独存一份仓库库存，而是用全服交易次数来替代：卖出了多少，购买上限就是多少。

```
玩家 A 卖出 5 件物品
        ↓
全服出售次数变为 5
        ↓
商店的购买上限变为 5
        ↓
玩家 B 最多只能买走这 5 件
```

## 在商品配置里设置动态值

插件本身并不保存库存数据，但库存的道理很简单：玩家 A 把物品卖给商店，商店就能把 A 卖出的东西再卖给玩家 B。你只要设一个购买上限，让这个上限等于该商品在全服的卖出次数，也就是占位符 `{server-times-server}`。读本页前请先看[动态价格](../dynamic-prices/dynamic-price.md)。和动态价格一样，做库存系统要写在 `buy-limits` 里，并放入 `{server-times-server}`，例如：

```yaml
  A:
    price-mode: ALL
    product-mode: CLASSIC_ALL
    products:
      1:
        material: echo_shard
        amount: 1
    buy-prices:
      1:
        economy-plugin: EcoBits
        economy-type: quest_points
        amount: 5
        placeholder: '&b{amount} Quest Points'
        start-apply: 0
    sell-prices:
      1:
        economy-plugin: EcoBits
        economy-type: quest_points
        amount: 5
        placeholder: '&b{amount} Quest Points'
        start-apply: 0
    buy-limits:
      global: '{sell-times-server}' 
    buy-times-reset-mode: 'NEVER'
    buy-times-reset-time: '00:00:00' 
```

改动点：

* 把 `price-mode` 设为 `ANY` 或 `ALL`。
* 把 `buy-limits` 设为 `{sell-times-server}`。若要用在出售上限上，这里应写 `{buy-times-server}`。换成 `{buy-times-player}` 和 `{sell-times-player}`，库存就变成每个玩家各算各的。
* 把 `buy-limits-reset-mode` 设为 `'NEVER'`。

这样一来，玩家能买到的数量就不会超过卖出次数——也就是靠别人卖货来补库存。

## 变体

* 想做每人独立的库存，就把全服占位符换成玩家占位符。
* 在出售上限里用 `{buy-times-server}`，可以模拟「先有需求、后有供给」。
* 在数学表达式里加一个固定值，当作初始库存。
* 和动态价格配合使用，稀缺商品就会越卖越贵。

## 手动补货

补货就是改动交易次数，或者改动决定上限的公式。

* 自动重置购买/出售次数的说明见[本页](product-config-buy-sell-times-reset.md)。
* 用命令 `/shop setselltimes <shop> <product> global <newStockPlusNowSellTimes>`。想补 50 个库存、而当前已卖出 75 个时，这里要填 125。
