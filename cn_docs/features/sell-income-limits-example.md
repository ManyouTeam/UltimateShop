# 🧪示例:回收限额示例

本示例基于[简易价格模板](easy-prices.md)中的 `vault-sell.yml`，让所有引用该模板的回收商品共用同一套玩家收入上限（默认每 5 小时 2,000、每日 10,000、每周 50,000）。

{% hint style="info" %}
自定义占位符在免费版中即可使用（最多 5 个），因此本示例在**免费版与付费版**中均可直接运行。
{% endhint %}

## 使用方法

在需要共用 Vault 回收限额的商品 `sell-prices` 中引用 `vault-sell` 模板：

```yaml
sell-prices:
  1:
    shared-price: vault-sell
    amount: 25
```

## 默认限额与重置周期

| 周期 | 默认上限 | 计数字段 | 重置方式 |
| --- | --- | --- | --- |
| 每 5 小时 | 2,000 | `{custom_sell_5h}` | 从该周期首次记账起算 5 小时 |
| 每日 | 10,000 | `{custom_sell_daily}` | 每天 00:00 |
| 每周 | 50,000 | `{custom_sell_weekly}` | 每周一 00:00 |

## 工作原理与界面显示

* **条件校验时机**：`require-conditions` 中的 `type: placeholder` 支持直接比较算式，并会在应用出售倍率与金额舍入之后执行；同一次回收中若有多条价格引用同一模板，条件会按合计金额一起校验。
* **累计记账方式**：`give-actions` 通过 `type: add_custom_placeholder` 将 `{amount}` 累加到数字型自定义占位符中，并配合 `multi-once: true` 确保每条价格项按本次奖励总额仅累加一次。
* **重置与倒计时**：数字型自定义占位符支持 `TIMER`、`TIMED` 和 `CUSTOM` 重置模式；如需在菜单中显示下次重置时间，可使用 `{custom-times_sell_daily}` 等占位符。
* **商品描述显示**：默认语言文件在 `display-item.add-lore` 中通过 `@r[vault-sell]` 条件符号，会在卖价下方自动显示三个周期的已用额度，详见[展示物品的附加描述](../menus/display-item-add-lore.md)。

## Sell All 怎么算最多能卖几次

默认的 `vault-sell.yml` 内置了 `max-sell-amount` 公式，会先计算三个周期各自还剩多少可用额度，再按**最紧的剩余额度 ÷ 单次实际到账金额**得出最大可售次数。例如最紧的额度只剩 80 金币、每卖一次实际到账 5 金币时，最多可卖 16 次；若背包库存只够卖 10 次，则按 10 次成交。

* 平时调整限额时，只需修改 `require-conditions` 中对应条目的 `value`，公式会自动读取该数值；只有修改了条件使用的收入计数器或比较规则时，才需要同步修改公式。
* 默认公式用到了 `min()`，需在 `config.yml` 中开启 `math.enable-function`。自定义公式的写法见[商品配置：单项内容中的 `max-sell-amount` 说明](../shops/products-config-single-thing/README.md#max-sell-amount)。

## 调整或关闭额度限制

三条上限全部由 `vault-sell.yml` 的 `require-conditions` 控制，插件没有单独的开关，直接修改该段即可：

```yaml
require-conditions:
  1:
    type: placeholder
    placeholder: '{custom_sell_5h} + {amount}'
    rule: '<='
    value: '2000'
```

常见调整方式：

* **完全取消限制**：删除整个 `require-conditions` 段。模板将只保留计数与显示功能，不再拦截回收。
* **只保留部分周期**：删掉不需要的条目，例如只保留 5 小时那一条。
* **只放宽限额数值**：将对应条目的 `value` 改大，例如 `'999999999'`。
* **仅对单个商品取消限制**：在该商品引用模板的价格项中加上 `require-conditions: false`，清空从模板继承的条件。

{% hint style="info" %}
取消限制条件后，`give-actions` 仍会继续累加 `sell_5h`、`sell_daily` 与 `sell_weekly`，商品描述中的已用额度也会照常增长，只是不再拦截交易。如果连进度显示也不需要，删掉语言文件 `display-item.add-lore` 中带有 `@r[vault-sell]` 的行即可。
{% endhint %}
