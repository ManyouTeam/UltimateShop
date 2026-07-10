# ⚖️Condition Format

The condition format will consist of several options.

{% hint style="info" %}
The `conditions` in the **Condition Format example** only represent **Condition Format** start from here. Please refer to the page description of the corresponding function for specific option names, such as `buy-conditions`.
{% endhint %}

## General Options

#### Apply Times

This condition will only check when player have buy/sell spcified times product.&#x20;

* start-apply: Start which times this condition will apply. **Optional. Default to 0.**
* end-apply: Last times the condition will apply. **Optional. Default to infinite.**
* apply: Which times this condition will apply, format: `[1,2,3,4]`. **Optional. Default use start-apply option value.**

```yaml
    conditions:
      1:
        apply: [1,2,3,4,5]
        start-apply: 1
        end-apply: 5
```

#### Click Type

This condition only checked when player use this click type to use the button.&#x20;

```yaml
    conditions:
      1:
        click-type: LEFT
```

#### Actions <mark style="color:red;">- Premium</mark>

{% hint style="info" %}
You should use `fail-actions` in products config if you want to modify the product buy or sell condition not meet message.
{% endhint %}

```yaml
    conditions:
      1:
        not-meet-actions:
          1: 
            type: message
            message: 'Condition not meet'
        meet-actions:
          1: 
            type: message
            message: 'Condition meet'
```

You can set actions for the entire condition, and if you want to do that, it's like this:

```yaml
conditions:
  not-meet-actions:
      1: 
        type: message
        message: 'You must use this apply item in world!'
  1:
    type: world
    world: 'test'
```

## Available Placeholders

* {world}
* {amount}

### Item-Level `buy-conditions` and `sell-conditions`

When you use `buy-conditions` or `sell-conditions` on the item itself, `{amount}` means the number of times the player is trying to buy or sell that item.

If the player buys 5, then `{amount}` is `5`.

### Single Thing `apply-conditions` and Legacy `conditions`

When you use `apply-conditions` on a single thing, `{amount}` is always `1`.

The same is true for legacy `conditions`, because in code they work as the old form of `apply-conditions`.

Why?

* `apply-conditions` are checked before the plugin finishes selecting the final single things
* at that stage, the real calculated amount is not ready yet

So in this layer:

* `{amount}` does not mean the final price
* `{amount}` does not mean the final product amount
* `{amount}` is just `1`

This means `apply-conditions` are for choosing branches, not for checking the real calculated amount.

### Single Thing `require-conditions`

When you use `require-conditions` on a single thing, `{amount}` means the final calculated amount of that selected single thing



This is the most important difference from `apply-conditions`.

* {player\_x}
* {player\_y}
* {player\_z}
* {player\_pitch}
* {player\_yaw}
* {player}
* {item} - Product ID
* {item-name} - Product Display Name
* {shop} - Shop ID
* {shop-name} - Shop Display Name
* {shop-menu} - Shop's Menu ID

## World

Player must be in the world.

<pre class="language-yaml"><code class="lang-yaml"><strong>  conditions:
</strong>    1:
      type: world
      world: lobby
</code></pre>

## Biome

Player must be in the biome.

```yaml
  conditions:
    1:
      type: biome
      biome: oraxen
```

## Permission

Player must has the permission.

**Remember that OP players will always have all permissions unless plugin set it not by default, so if you want to test this condition, you have to deop yourself.**

```yaml
  conditions:
    1:
      type: permission
      permission: 'group.vip'
```

## Placeholder

Player must be meet the placeholder condition.

Rule can be set to:

* \>=
* <=
* \>
* <
* \== (String)
* \= (Number)
* != (Number or string)
* !\*= (Number or string) Not contains.
* \*= (String) Contains, for example, str \*= string is true, but example \*= ple is false.

```yaml
  conditions:
    1:
      type: placeholder
      placeholder: '%player_health%'
      rule: '<='
      value: 5
```

## Menu

Player is opening the specified menu.

```yaml
  conditions:
    1:
      type: menu
      menu: 'example-shop-menu'
```

## Menu Type

Player is opening the specified type of menu.

Supported menu type:

* COMMON
* SHOP
* MORE
* SEARCH
* FAVOURITE

```yaml
  conditions:
    1:
      type: menu_type
      menu-type: 'favourite'
```

## Any <mark style="color:red;">- Premium</mark>

```yaml
  conditions:
    1:
      type: any
      conditions:
        1:
          type: placeholder
          placeholder: '%eco_balance%'
          rule: '>='
          value: 200
        2:
          type: placeholder
          placeholder: '%player_points%'
          rule: '>='
          value: 400
```

## Not <mark style="color:red;">- Premium</mark>

```yaml
  conditions:
    1:
      type: not
      conditions:
        1:
          type: placeholder
          placeholder: '%eco_balance%'
          rule: '>='
          value: 200
```
