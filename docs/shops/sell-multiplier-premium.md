# 💰Sell Multiplier - Premium

{% hint style="info" %}
Sell chest and sell stick provide multiplier feature, their multiplier and this feature are independent of each other, and the final price result will be superimposed.
{% endhint %}

`sell.multiplier` is a global sell bonus system. You can use it as tax for all players or bonus for VIP players. You can find its configuration in `config.yml`.

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

* `enabled`: Must be `true` to enable the global multiplier.
* `display-original-price`: When `false`, the shop GUI displays the price after applying the multiplier.
* `mode`: Supports `MAX` and `STACK`.
  * `MAX`: Uses the largest value among `default` and all matched entries.
  * `STACK`: Starts with `default`, then multiplies all matched entries together.
* `value` and `value-conditions`: IDs must match exactly. Conditions use [Condition Format](../format/condition-format.md).

For example, if `default` is `1`, `rich` is `0.9`, `vip` is `1.1`, and the player matches both conditions, `MAX` produces `1.1` while `STACK` produces `0.99`.

## Item-aware price modifiers

`sell.price-modifier` is separate from this global multiplier. For products with `price-modifier: true`, item-aware modifiers are applied to the actual sold stacks and then combined with `sell.multiplier` for the final sell reward.

See [Price Modifier](price-modifier.md) for its configuration and behavior.
