# ⚡Example: Common Usage

```yaml
settings:
  menu: 'example-shop-menu'
  buy-more: true
  shop-name: 'Blocks Shop'
  hide-message: false

items:
  A:
    # ...
  B:
    # ...
  C:
    # ...
```

## Product A

This example has sell limits, which means player can only sell this product X times every day.

* Sell limits can help your server has "economy balance", even player has 10000 rotten flesh, he still can only sell 64x in this example.
* In this example, VIP players will have 50% more sell limits, which can help your server has more supporter.
* In this example, sell limits will reset at local time '0:00:00', which means midnight.
* In this example, we set price and product mode has `CLASSIC_` prefix, this will save server performance, you should use this when you didn't set `start-apply` option to other value in `prices` section.

```yaml
items:
  A:
    price-mode: CLASSIC_ALL
    product-mode: CLASSIC_ALL
    products:
      1:
        material: rotten_flesh
        amount: 1
    buy-prices:
      1:
        economy-plugin: Vault
        amount: 10
        placeholder: '&6{amount} Coins'
        start-apply: 0
    sell-prices:
      1:
        economy-plugin: Vault
        amount: 0.8
        placeholder: '&6{amount} Coins'
        start-apply: 0
    sell-limits:
      global: 1280
      default: 64
      vip: 192
    sell-limits-conditions:
      vip:
        1:
          type: permission
          permission: 'group.vip'
    sell-limits-reset-mode: 'TIMED'
    sell-limits-reset-time: '00:00:00'  
```

## Product B

Product A only brought an item from vanilla, let's try to obtain an item from a third-party plugin! Meanwhile, using **Vault** as an economy is too boring! Let's try something different, such as vanilla's experience points!

* To get how to write `hook-plugin` option and `hook-item` option, please read [Item Format](../format/itemformat-tm/) page.
* To get how to write `economy-type` option or `economy-plugin` option, please read [Economy Format](../format/economyformat-tm.md) page.

<pre class="language-yaml"><code class="lang-yaml"><strong>items:
</strong><strong>  A:
</strong><strong>    # ...
</strong>  B:
    products:
      1:
        hook-plugin: MMOItems # Plugin Name
        hook-item: AXE;;EXECUTIONER_AXE # Item ID
        amount: 1
    price-mode: ANY
    product-mode: ALL
    buy-prices:
      1:
        economy-type: exp
        amount: 1
        start-apply: 0
        placeholder: '1 Exp'
    sell-prices:
      1:
        economy-type: exp
        amount: 1
        start-apply: 0
        placeholder: '1 Exp'
</code></pre>

## Product C

Sometimes, we do not support the third-party plugin items you are using, so you can solve this by creating a command shop!

```yaml
items:
  A:
    # ...
  B:
    # ...
  C:
    price-mode: CLASSIC_ALL
    product-mode: CLASSIC_ALL
    display-item:
      name: 'Chicken Spawner'
      material: PLAYER_HEAD
      skull: eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjQ3ZTJlNWQ1NWI2ZDA0OTQzNTE5YmVkMjU1N2M2MzI5ZTMzYjYwYjkwOWRlZTg5MjNjZDg4YjExNTIxMCJ9fX0=
      amount: 1
    buy-prices:
      1:
        economy-plugin: Vault
        amount: 350000
        placeholder: '{amount}⛂'
    buy-actions:
      1:
        type: console_command
        command: "ws give %player_name% spawner chicken 1"
      # or
      # 2:
      #  multi-once: true
      #  type: console_command
      #  command: "ws give %player_name% spawner chicken {amount}"
```

* You can first enter the command `/shop generateitemformat` to generate an **Item Format** and fill it into the `display-item` option. This way, even if you haven't set any items, the plugin can still display the item in the store by reading the item in the `display-item` option.
* This product didn't set `sell-prices` option, so it can not be sold.
* After purchase, we will execute `buy-actions` which includes execute the command you set. For more info about Actions, please read [Action Format](../format/action-format.md) page.

## Seasonal Product

```yaml
items:
  A:
    price-mode: CLASSIC_ALL
    product-mode: CLASSIC_ALL
    products:
      1:
        material: potato
        amount: 1
    buy-prices:
      1:
        economy-plugin: Vault
        amount: 2
        placeholder: '&6{amount} Coins'
        conditions:
          1: 
            type: placeholder
            placeholder: '%rs_season%'
            rule: '=='
            value: 'Spring'
      2:
        economy-plugin: Vault
        amount: 1.8
        placeholder: '&6{amount} Coins'
        conditions:
          1: 
            type: placeholder
            placeholder: '%rs_season%'
            rule: '=='
            value: 'Summber'
      3:
        economy-plugin: Vault
        amount: 3.2
        placeholder: '&6{amount} Coins'
        conditions:
          1: 
            type: placeholder
            placeholder: '%rs_season%'
            rule: '=='
            value: 'Fall'
      4:
        economy-plugin: Vault
        amount: 8.8
        placeholder: '&6{amount} Coins'
        conditions:
          1: 
            type: placeholder
            placeholder: '%rs_season%'
            rule: '=='
            value: 'Winter'     
```
