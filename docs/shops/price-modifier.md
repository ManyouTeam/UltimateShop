# 💹Price Modifier

{% hint style="danger" %}
This feature is offered to users as an early experimental feature, and will be available after version 5.0.0. This feature is still under testing, so much so that it's not mentioned in the plugin's changelog. If you happen to stumble upon this page someday, then you're in luck!
{% endhint %}

You can find price modifier config section at `config.yml` file. You can create unlimited price modifier with this format and plugin itself register 3 different type of price modifier.

Only product with `price-modifier` option enabled will use this feature, for more info, please view [this page](products.md#general-options).

{% code title="" %}
```yaml
sell:
  # Item-aware modifiers are a separate global sell feature.
  # Modifiers are applied in configuration order and merged into the final multiplier.
  price-modifier:
    item-sell-menu:
      # Products with `price-modifier: true` open this item sell menu when clicked.
      enabled: true
      menu: item-sell
    durability:
      type: durability
      enabled: true
      # 1 means losing 1% durability deducts 1% from the price.
      deduction-coefficient: 1
      minimum-multiplier: 0.1
      # You can use minimum-price instead of, or together with, minimum-multiplier.
      # minimum-price: 1
    lore:
      type: lore
      enabled: false
      # SET uses the captured number as the price; MULTIPLY multiplies the current price by it.
      # Invalid operations are reported and the modifier is ignored.
      operation: SET
      # The default pattern matches lore such as: Item Value: 500
      pattern: 'Item Value[：:]\s*([+-]?(?:\d+(?:\.\d+)?|\.\d+))'
      value-group: 1
      strip-color: true
      case-sensitive: true
      # Values outside this inclusive range, or longer than maximum-number-length, are ignored.
      minimum-value: 0
      maximum-value: 1000000
      maximum-number-length: 64
    nbt:
      type: nbt
      enabled: false
      # Requires the NBTAPI plugin. Nested keys can use dot notation.
      key: item_value
      # AUTO, BYTE, SHORT, INT, LONG, FLOAT, DOUBLE or STRING.
      value-type: AUTO
      # SET uses the NBT value as the price; MULTIPLY multiplies the current price by it.
      # Invalid operations are reported and the modifier is ignored.
      operation: SET
      # Values outside this inclusive range, or longer than maximum-number-length, are ignored.
      minimum-value: 0
      maximum-value: 1000000
      maximum-number-length: 64
    mythic-changer:
      type: match_item
      enabled: false
      # MAX, MIN or STACK. STACK applies matching rules in configuration order.
      mode: STACK
      rules:
        named-item:
          # ADD or MULTIPLY. Invalid operations are reported and this rule is ignored.
          operation: MULTIPLY
          value: 1.2
          match-item:
            has-name: true
        special-lore:
          operation: ADD
          # ADD adds this value to the price multiplier: 1 + 0.1 = 1.1.
          value: 0.1
          match-item:
            contains-lore:
              - 'Special'
```
{% endcode %}

## Price modifier API

Global item-aware price modifiers are created through `PriceModifierRegistry`. A custom implementation only needs to implement `PriceModifier` and register a factory:

```java
PriceModifierRegistry.register("my-modifier", MyPriceModifier::new);
```

Each modifier receives the player, the actual sold `ItemStack`, and the current numeric price. It returns a non-negative multiplier. The backward-compatible four-argument overload also receives the number of trade units when a modifier needs quantity-aware behavior such as `SET`. Modifiers declared under `sell.price-modifier` are applied in configuration order to every sell method.

## Durability modifier

```yaml
sell:
  price-modifier:
    durability:
      type: durability
      deduction-coefficient: 1
      minimum-multiplier: 0.1
      # minimum-price: 1
```

The formula is:

```
1 - (lost durability ratio × deduction-coefficient)
```

The result never drops below `minimum-multiplier`. If `minimum-price` is configured, the modifier also prevents the current total numeric price from dropping below that value. Non-damageable items keep a multiplier of `1`.

## Lore value modifier

```yaml
sell:
  price-modifier:
    lore-value:
      type: lore
      enabled: true
      operation: SET
      pattern: 'Item Value[：:]\s*([+-]?(?:\d+(?:\.\d+)?|\.\d+))'
      value-group: 1
      strip-color: true
      case-sensitive: true
      minimum-value: 0
      maximum-value: 1000000
      maximum-number-length: 64
```

The first matching lore line supplies the number. `SET` makes that number the price of each sold trade unit, so selling a stack produces the same total as selling its contents separately. `MULTIPLY` multiplies the current price by the number. `value-group` selects the regular-expression capture group. Values outside the inclusive `minimum-value`/`maximum-value` range, values longer than `maximum-number-length`, and unmatched or invalid values leave the price unchanged. An unsupported operation is reported as a configuration error and the modifier is ignored instead of falling back to `MULTIPLY`.

## NBT value modifier

```yaml
sell:
  price-modifier:
    nbt-value:
      type: nbt
      enabled: true
      operation: SET
      key: item_value
      value-type: AUTO
      minimum-value: 0
      maximum-value: 1000000
      maximum-number-length: 64
```

The NBT modifier requires NBTAPI. `key` supports nested paths such as `custom.price`. `value-type` supports `AUTO`, `BYTE`, `SHORT`, `INT`, `LONG`, `FLOAT`, `DOUBLE`, and `STRING`; numeric strings are accepted. `SET`, `MULTIPLY`, and the numeric safety options behave the same as in the Lore modifier. Missing, non-finite, out-of-range, oversized, or invalid values leave the price unchanged.

## Match item modifier

This modifier uses MythicChanger `match-item` rules. If MythicChanger is unavailable, the modifier has no effect.

```yaml
sell:
  price-modifier:
    mythic-changer:
      type: match_item
      mode: STACK
      rules:
        named-item:
          operation: MULTIPLY
          value: 1.2
          match-item:
            has-name: true
        special-lore:
          operation: ADD
          value: 0.1
          match-item:
            contains-lore:
              - 'Special'
```

Every matching rule produces a multiplier:

* `MULTIPLY`: uses `value` directly, so `1.2` means 120% of the current price.
* `ADD`: adds `value` to the multiplier, so `0.1` means 110%.

Unsupported rule operations are reported as configuration errors and the affected rule is ignored.

`mode` controls multiple matching rules:

* `MAX` or `HIGHEST`: use the highest resulting multiplier.
* `MIN` or `LOWEST`: use the lowest resulting multiplier.
* `STACK`: process matching rules in configuration order; `MULTIPLY` values multiply and `ADD` values add to the accumulated multiplier.
