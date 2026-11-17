# 💰出售倍率 - 付费版

{% hint style="info" %}
出售箱（sell chest）和出售棒（sell stick）自带的倍率与这里讲的倍率互相独立，最终价格会叠加计算。
{% endhint %}

`sell.multiplier` 是一套全局的出售加成，既可以当作面向所有玩家的「税」，也可以作为 VIP 的额外加成。配置位于 `config.yml`。

```yaml
sell:
  # Premium version only
  multiplier:
    enabled: false
    display-original-price: true
    # Support value: MAX, STACK
    # MAX mode: will use the maximum value as the result
    # STACK mode: As long as the player meets the conditions, it will be stacked and multiplied.
    mode: STACK
    value:
      default: 1
      rich: 0.9
      vip: 1.1
    value-conditions:
      # Tax
      rich:
        1:
          type: placeholder
          placeholder: '%vault_eco_balance%'
          rule: '>='
          value: 50000
      # Bonus for VIP
      vip:
        1:
          type: permission
          permission: 'group.vip'
```

* `enabled`：必须设为 `true` 才会启用全局倍率。
* `display-original-price`：设为 `false` 时，商店界面显示的就是乘过倍率之后的价格。
* `mode`：支持`MAX`和`STACK`。
  * `MAX`：在 `default` 与所有命中条目里取最大的那个值。
  * `STACK`：以 `default` 为起点，把所有命中的条目依次乘上去。
* `value` 和 `value-conditions`：ID 必须完全匹配。条件使用[条件格式](../format/condition-format.md)。

例如 `default` 为 `1`、`rich` 为 `0.9`、`vip` 为 `1.1`，玩家同时满足两个条件时，`MAX` 得到 `1.1`，`STACK` 得到 `0.99`。

## 按物品计的价格修正

`sell.price-modifier` 与这套全局倍率是两回事。对启用了 `price-modifier: true` 的商品，会先按实际卖出的每一堆物品逐一应用修正，再与 `sell.multiplier` 相乘，得出最终的出售收益。

具体配置与行为见[价格修改器](price-modifier.md)。
