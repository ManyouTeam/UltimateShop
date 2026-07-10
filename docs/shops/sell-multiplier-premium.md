# 💰Sell Multiplier - Premium

{% hint style="info" %}
Sell chest and sell stick provide multiplier feature, their multiplier and this feature are independent of each other, and the final price result will be superimposed.
{% endhint %}

`sell.multiplier` is a global sell bonus system. You can use it as tax for all players or bonus for VIP players. You can found it's config at `config.yml` file.

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

* enabled: To use this feature, you have to make sure this option being set to `true`.
* display-original-price: If set to `false`, we will display price that has modified by the multiplier in shop GUI.
*   mode: Support value: **MAX** and **STACK**.

    * MAX: Use the biggest value among all matched multiplier entries and `default`.
    * STACK: Start from `default`, then multiply every matched value together.

    Example:

    * `default = 1`
    * `rich = 0.9`
    * `vip = 1.1`
    * Player matches both `rich` and `vip`
    * MAX mode final result = `1.1`
    * STACK mode final result = `1 * 0.9 * 1.1 = 0.99`
* value and value-conditions: The multiplier id in `value` must match the id in `value-conditions` exactly. Should use [Condition Format](../format/condition-format.md) in `value-conditions` option.

