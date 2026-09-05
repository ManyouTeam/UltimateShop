# 💹Price Modifier

{% hint style="danger" %}
This feature is offered to users as an early experimental feature and will be officially available after version 5.0.0.

The feature is still under testing and may change before its final release.
{% endhint %}

Price Modifier allows UltimateShop to adjust a product's sell reward according to properties of the **actual item being sold**.

For example, you can:

* Reduce the sell price of a damaged item.
* Read a numeric value from item lore and use it as the sell price.
* Read a numeric value from NBT and use it as the sell price.
* Apply different multipliers according to MythicChanger `match-item` rules.

Price modifiers are configured globally under:

```yaml
sell:
  price-modifier:
```

However, a product will only use them when the following option is enabled in that product:

```yaml
price-modifier: true
```

See [Products](products.md#general-options) for more information about product options.

***

## Item Matching vs Price Modifier

{% hint style="warning" %}
**Item matching and Price Modifier are two different systems.**

Item matching decides:

> **Can this item be sold as this product?**

Price Modifier decides:

> **How much should this actual item be worth?**
{% endhint %}

The simplified selling process is:

```
Actual item in player inventory
        ↓
Find a matching product
        ↓
Check whether the item matches the product
        ↓
Calculate normal sell price
        ↓
Apply conditional sell multiplier
        ↓
Apply Price Modifier using the actual ItemStack
        ↓
Round the calculated result
        ↓
Give the final sell reward
```

This distinction is especially important when a Price Modifier uses item data that is expected to change between different items.

For example, suppose a shop sells and buys a diamond sword.

The configured product contains:

```
Diamond Sword
Damage: 0
```

But the player tries to sell:

```
Diamond Sword
Damage: 500
```

If the two items fail the sell matching check, the damaged sword will never reach the durability Price Modifier.

The Price Modifier does **not** automatically make differently damaged, named, enchanted, or otherwise modified items match the same product.

***

### Bukkit and ItemFormat sell methods

UltimateShop supports two global item matching methods:

```yaml
sell:
  sell-method: Bukkit
```

and:

```yaml
sell:
  sell-method: ItemFormat
```

With `Bukkit`, UltimateShop uses Bukkit's item similarity check. Differences in item data such as durability, lore, enchantments, names, components, or NBT may cause an item to no longer match the configured product.

`ItemFormat` provides more control because specific item properties can be ignored during the matching process.

Example:

```yaml
sell:
  sell-method: ItemFormat

  item-format:
    require-same-key: false
    ignore-key:
      - 'damage'
```

In this example, different durability values do not prevent items from matching the product.

{% hint style="info" %}
Ignoring a property in `item-format.ignore-key` only affects **item matching**.

It does **not** remove that information from the actual item.

For example, if `damage` is ignored during matching, the durability Price Modifier can still read the actual damage value of the item being sold.
{% endhint %}

For more information about item matching, see [Custom Item Match Method](../features/custom-item-match-method.md).

You can also hold an item and use:

```
/shop generateitemformat
```

to inspect its ItemFormat data and determine the correct key path.

***

## Basic Configuration

A complete example can look like this:

```yaml
sell:
  # Item matching method.
  #
  # ItemFormat is recommended when a Price Modifier depends on item
  # properties that are expected to differ between otherwise sellable items.
  sell-method: ItemFormat

  item-format:
    require-same-key: false
    ignore-key:
      # Allow different durability values to match the same product.
      - 'damage'

  # Global Price Modifier configuration.
  price-modifier:

    item-sell-menu:
      # Products with `price-modifier: true` open this menu when
      # the player chooses a sell action.
      enabled: true
      menu: item-sell

    durability:
      type: durability
      enabled: true

      # Losing 1% durability deducts 1% from the price.
      deduction-coefficient: 1

      # Final modifier cannot become lower than this value.
      minimum-multiplier: 0.1

      # You can also set a minimum numeric price.
      # minimum-price: 1

    lore:
      type: lore
      enabled: false

      # SET or MULTIPLY.
      operation: SET

      # Example:
      # Item Value: 500
      pattern: 'Item Value[：:]\s*([+-]?(?:\d+(?:\.\d+)?|\.\d+))'

      value-group: 1
      strip-color: true
      case-sensitive: true

      minimum-value: 0
      maximum-value: 1000000
      maximum-number-length: 64

    nbt:
      type: nbt
      enabled: false

      # Requires NBTAPI.
      key: item_value

      # AUTO, BYTE, SHORT, INT, LONG, FLOAT, DOUBLE or STRING.
      value-type: AUTO

      # SET or MULTIPLY.
      operation: SET

      minimum-value: 0
      maximum-value: 1000000
      maximum-number-length: 64

    mythic-changer:
      type: match_item
      enabled: false

      # MAX, MIN or STACK.
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

Modifiers are processed in configuration order.

***

## Enable Price Modifier for a Product

Price Modifier is disabled for products by default.

Enable it by adding:

```yaml
price-modifier: true
```

to the product configuration.

Example:

```yaml
items:
  diamond-sword:
    price-modifier: true

    products:
      1:
        material: DIAMOND_SWORD
        amount: 1

    sell-prices:
      1:
        economy-plugin: Vault
        amount: 100
        placeholder: '{amount} Coins'
```

The configured sell price is still the product's **base sell price**.

Price Modifier changes that price according to the actual item being sold.

***

## Durability Modifier

The durability modifier reduces the sell price according to how much durability the item has lost.

Example:

```yaml
sell:
  price-modifier:
    durability:
      type: durability
      enabled: true
      deduction-coefficient: 1
      minimum-multiplier: 0.1
```

The basic formula is:

```
1 - (lost durability ratio × deduction coefficient)
```

For example:

```
Item durability lost: 30%
deduction-coefficient: 1

Multiplier:
1 - (0.30 × 1)
= 0.70
```

If the base sell price is:

```
100
```

the modified price becomes:

```
100 × 0.70
= 70
```

Non-damageable items use a multiplier of `1`.

***

### minimum-multiplier

You can prevent damaged items from becoming almost worthless:

```yaml
minimum-multiplier: 0.1
```

For example, even if the durability calculation produces:

```
0.03
```

the final modifier will be:

```
0.1
```

***

### minimum-price

You can also specify a minimum numeric price:

```yaml
minimum-price: 10
```

This prevents the calculated numeric price from dropping below the configured value.

`minimum-price` and `minimum-multiplier` can be used separately or together.

***

### Item matching for durability

{% hint style="warning" %}
If damaged and undamaged items should be treated as the same sellable product, their different damage values must not prevent item matching.
{% endhint %}

The recommended configuration is:

```yaml
sell:
  sell-method: ItemFormat

  item-format:
    require-same-key: false
    ignore-key:
      - 'damage'
```

This creates the following behavior:

```
Shop item:
Diamond Sword
Damage: 0

Player item:
Diamond Sword
Damage: 500

        ↓

ItemFormat matching
damage is ignored

        ↓

Item matches product

        ↓

Durability Price Modifier
reads actual Damage: 500

        ↓

Sell price is reduced
```

`damage` is ignored only when deciding whether the item matches the product. The modifier still receives the actual damaged ItemStack.

***

## Lore Value Modifier

The Lore modifier reads a numeric value from an item's lore.

Example item:

```
Legendary Sword
Damage: 25
Item Value: 500
```

Configuration:

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

The first matching lore line supplies the numeric value.

***

### SET

With:

```yaml
operation: SET
```

the captured value becomes the price of each sold trade unit.

For example:

```
Base sell price: 100
Lore:
Item Value: 500
```

results in:

```
Final unit price: 500
```

When multiple trade units are sold together, UltimateShop keeps the result consistent with selling those units separately.

***

### MULTIPLY

With:

```yaml
operation: MULTIPLY
```

the captured value multiplies the current price.

For example:

```
Current price: 100

Lore:
Price Multiplier: 1.5
```

results in:

```
100 × 1.5
= 150
```

***

### Numeric safety options

The following options prevent invalid or unreasonable values from being used:

```yaml
minimum-value: 0
maximum-value: 1000000
maximum-number-length: 64
```

Values outside the configured range are ignored.

Values longer than `maximum-number-length` are also ignored.

Missing, invalid, or unmatched values leave the current price unchanged.

Unsupported operations are reported as configuration errors and the affected modifier is ignored.

***

### Item matching for Lore

If the lore used by the modifier is expected to differ between individual items, the lore difference must not prevent the item from matching the product.

For example:

```
Sword A:
Item Value: 100

Sword B:
Item Value: 500
```

If both should be sellable as the same product, you can use:

```yaml
sell:
  sell-method: ItemFormat

  item-format:
    require-same-key: false
    ignore-key:
      - 'lore'
```

{% hint style="info" %}
Ignoring `lore` during item matching does not prevent the Lore Price Modifier from reading the actual lore of the sold item.
{% endhint %}

However, ignoring the complete `lore` property means **all lore differences** are ignored during sell matching.

Only do this if all such lore differences are acceptable for that product.

For more precise item matching, consider using product-level `match-item` rules.

***

## NBT Value Modifier

The NBT modifier reads a numeric value from the item's NBT data.

{% hint style="warning" %}
This modifier requires the **NBTAPI** plugin.
{% endhint %}

Example:

```yaml
sell:
  price-modifier:
    nbt-value:
      type: nbt
      enabled: true

      key: item_value
      value-type: AUTO

      operation: SET

      minimum-value: 0
      maximum-value: 1000000
      maximum-number-length: 64
```

Nested NBT paths are supported:

```yaml
key: custom.price
```

***

### value-type

Supported values are:

```
AUTO
BYTE
SHORT
INT
LONG
FLOAT
DOUBLE
STRING
```

`AUTO` automatically attempts to obtain a usable numeric value.

Numeric strings are also accepted.

***

### SET and MULTIPLY

The NBT modifier supports the same operations as the Lore modifier.

#### SET

```yaml
operation: SET
```

uses the NBT value as the unit price.

#### MULTIPLY

```yaml
operation: MULTIPLY
```

multiplies the current price by the NBT value.

***

### Invalid values

The modifier leaves the current price unchanged when the NBT value is:

* Missing.
* Invalid.
* Non-numeric.
* Non-finite.
* Outside `minimum-value` and `maximum-value`.
* Longer than `maximum-number-length`.

***

### Item matching for NBT

If the NBT value used as the dynamic price differs between individual items, that value may also need to be ignored during ItemFormat matching.

For example, after inspecting the item with:

```
/shop generateitemformat
```

you may find a key such as:

```
nbt.string.item_value
```

You can then configure:

```yaml
sell:
  sell-method: ItemFormat

  item-format:
    require-same-key: false
    ignore-key:
      - 'nbt.string.item_value'
```

{% hint style="warning" %}
Whenever possible, ignore only the specific dynamic NBT key.

Avoid ignoring the entire `nbt` section unless you intentionally want all NBT differences to be ignored.
{% endhint %}

For example:

```yaml
ignore-key:
  - 'nbt'
```

may cause many otherwise different items to be treated as the same product.

Use `/shop generateitemformat` to determine the most specific ItemFormat key available for your item.

***

## Match Item Modifier

The `match_item` modifier uses MythicChanger `match-item` rules.

The legacy type:

```yaml
type: mythic_changer
```

remains available as an alias.

{% hint style="warning" %}
This modifier requires **MythicChanger**.

If MythicChanger is unavailable, the modifier has no effect.
{% endhint %}

Example:

```yaml
sell:
  price-modifier:
    mythic-changer:
      type: match_item
      enabled: true

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

Each rule contains:

```yaml
operation:
value:
match-item:
```

The `match-item` section uses MythicChanger's item matching system.

***

### MULTIPLY

```yaml
operation: MULTIPLY
value: 1.2
```

produces a multiplier of:

```
1.2
```

which means:

```
120%
```

of the current price.

***

### ADD

```yaml
operation: ADD
value: 0.1
```

adds the value to the multiplier.

For example:

```
1 + 0.1
= 1.1
```

which means:

```
110%
```

of the current price.

A valid expression whose result is `0` is still accepted.

Invalid values or unsupported operations are reported through UltimateShop's error manager and the affected rule is ignored.

***

## Multiple Match Item Rules

The `mode` option controls what happens when multiple rules match.

Supported modes are:

```
MAX
HIGHEST
MIN
LOWEST
STACK
```

***

### MAX / HIGHEST

Uses the highest resulting multiplier.

For example:

```
Rule A: 1.2
Rule B: 1.5
```

result:

```
1.5
```

***

### MIN / LOWEST

Uses the lowest resulting multiplier.

For example:

```
Rule A: 1.2
Rule B: 1.5
```

result:

```
1.2
```

***

### STACK

Processes matching rules in configuration order.

`MULTIPLY` values multiply the accumulated multiplier.

`ADD` values add to the accumulated multiplier.

For example:

```
Initial multiplier:
1

Rule A:
MULTIPLY 1.5

1 × 1.5
= 1.5

Rule B:
ADD 0.2

1.5 + 0.2
= 1.7
```

Final multiplier:

```
1.7
```

***

## Multiple Price Modifiers

All enabled Price Modifiers under:

```yaml
sell:
  price-modifier:
```

are applied in configuration order.

For example:

```yaml
price-modifier:

  durability:
    type: durability
    enabled: true

  lore:
    type: lore
    enabled: true
```

the durability modifier is processed before the lore modifier.

The result of an earlier modifier becomes part of the current price used by later modifiers.

Therefore, changing the configuration order may also change the final result.

***

## Selling Flow

Products with:

```yaml
price-modifier: true
```

require access to the actual ItemStack being sold.

Because of this, sell flows that cannot provide an actual item stack do not directly display or execute the normal sell price.

When the player chooses:

```
sell
```

or:

```
sell-all
```

UltimateShop opens the item sell menu configured here:

```yaml
sell:
  price-modifier:
    item-sell-menu:
      enabled: true
      menu: item-sell
```

Buy actions, amount-selection actions, and custom actions are not redirected by this behavior.

Bedrock forms and dialog shop lists open the product information view first, where buy and sell remain separate actions.

A product with Price Modifier enabled can still be sold through the item sell menu even if:

```yaml
sell-all: false
```

is configured for that product.

***

## Price Calculation Order

The simplified numeric calculation is:

```
Base sell price
        ↓
Conditional sell multiplier
        ↓
Price Modifier chain
        ↓
Configured numeric precision
        ↓
Final reward
```

For example:

```
Base price:
100

Conditional sell multiplier:
1.2

After conditional multiplier:
120

Durability modifier:
0.5

Final:
120 × 0.5
= 60
```

***

## Precision and Rounding

Applying a multiplier is treated as a mathematical calculation.

UltimateShop rounds calculation results using:

```
HALF_UP
```

to the decimal places configured by:

```yaml
math:
  scale:
```

This applies to:

* Price Modifiers.
* Global sell multipliers.
* Price previews.
* Final sell rewards.

For more information, see [Math Calculate Format](../format/math-calculate-format.md#result-precision).

***

## Transaction Event Behavior

During an actual transaction, the sell price stored in the post-event `GiveResult` is authoritative.

If another plugin changes the sell price through:

```
ItemPreTransactionEvent
```

UltimateShop preserves that changed result.

When different actual ItemStacks need different Price Modifier results, recalculated stack prices are used only to determine how the already-authoritative total should be distributed between those stacks before their item-aware modifiers are applied.

This prevents a transaction event's price modification from being accidentally replaced by a second normal price calculation.

***

## Recommended Configurations

### Durability-based price

If the only dynamic property is durability:

```yaml
sell:
  sell-method: ItemFormat

  item-format:
    require-same-key: false
    ignore-key:
      - 'damage'

  price-modifier:
    durability:
      type: durability
      enabled: true
      deduction-coefficient: 1
      minimum-multiplier: 0.1
```

Product:

```yaml
price-modifier: true
```

***

### Lore-based price

If the price stored in lore is expected to vary:

```yaml
sell:
  sell-method: ItemFormat

  item-format:
    require-same-key: false
    ignore-key:
      - 'lore'

  price-modifier:
    lore-value:
      type: lore
      enabled: true
      operation: SET
      pattern: 'Item Value[：:]\s*([+-]?(?:\d+(?:\.\d+)?|\.\d+))'
      value-group: 1
      minimum-value: 0
      maximum-value: 1000000
```

Product:

```yaml
price-modifier: true
```

***

### NBT-based price

First inspect the item:

```
/shop generateitemformat
```

Then ignore only the dynamic ItemFormat key if necessary:

```yaml
sell:
  sell-method: ItemFormat

  item-format:
    require-same-key: false
    ignore-key:
      - 'nbt.string.item_value'

  price-modifier:
    nbt-value:
      type: nbt
      enabled: true
      key: item_value
      value-type: AUTO
      operation: SET
      minimum-value: 0
      maximum-value: 1000000
```

Product:

```yaml
price-modifier: true
```

***

## Common Problems

### Damaged items cannot be sold

If you are using:

```yaml
type: durability
```

but damaged items cannot be sold, check your global sell method.

Using:

```yaml
sell-method: Bukkit
```

may cause different damage values to fail item matching.

Consider:

```yaml
sell-method: ItemFormat
```

with:

```yaml
ignore-key:
  - 'damage'
```

***

### Lore modifier does not run

If the item's lore differs from the product item, the item may be rejected before the modifier is reached.

If that lore difference is intentional, configure the item matching system accordingly.

***

### NBT modifier does not run

Check both systems separately:

1. Can the actual item match the product?
2. Can the NBT modifier find the configured NBT key?

Use:

```
/shop generateitemformat
```

to inspect ItemFormat matching data.

The NBT key used by the Price Modifier and the ItemFormat key used by `ignore-key` represent related item data, but they are configured for different purposes.

***

### Price Modifier has no effect

Check that the product contains:

```yaml
price-modifier: true
```

Also check that the corresponding global modifier contains:

```yaml
enabled: true
```

For example:

```yaml
sell:
  price-modifier:
    durability:
      type: durability
      enabled: true
```

***

### Different items are unexpectedly treated as the same product

Check:

```yaml
sell:
  item-format:
    ignore-key:
```

Do not ignore more item data than necessary.

For example:

```yaml
ignore-key:
  - 'nbt'
```

is much broader than:

```yaml
ignore-key:
  - 'nbt.string.item_value'
```

Use the most specific key possible.

***

## Price Modifier API

Global item-aware Price Modifiers are registered through `PriceModifierRegistry`.

A custom implementation only needs to implement `PriceModifier` and register a factory:

```java
PriceModifierRegistry.register(
        "my-modifier",
        MyPriceModifier::new
);
```

Each modifier receives:

* The player.
* The actual sold `ItemStack`.
* The current numeric price.

It returns a non-negative multiplier.

The four-argument overload also receives the number of trade units for modifiers that require quantity-aware behavior, such as `SET`.

Modifiers configured under:

```yaml
sell:
  price-modifier:
```

are applied in configuration order.

Registering a new factory, or replacing an existing factory with the same type, immediately rebuilds the configured Price Modifier chain.

A server reload is not required.
