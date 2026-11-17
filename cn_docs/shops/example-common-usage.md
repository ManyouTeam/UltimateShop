# ⚡示例：常见用法

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

## 商品 A - VIP 限制

这个例子设置了出售上限，也就是玩家每天只能卖出这个商品固定的次数。

* 出售上限能让服务器的经济保持平衡：哪怕玩家攒了 10000 个腐肉，在这个例子里他也只能卖 64 个。
* 本例中 VIP 玩家的出售上限多 50%，有助于为服务器争取更多支持者。
* 本例中出售上限会在本地时间 0:00:00（即午夜）重置。
* 本例把价格模式和商品模式都设成带 `CLASSIC_` 前缀的形式，能省下服务器性能。只要你在 `prices` 里没把 `start-apply` 改成别的值，就该用这种模式。

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

## 商品 B - 用经验当货币

商品 A 用的只是原版物品，这次换从第三方插件里取物品！另外，老拿 **Vault** 当货币也太无趣了，换个花样试试——比如原版经验值！

* `hook-plugin` 与 `hook-item` 怎么写，见[物品格式](../format/itemformat-tm/)页面。
* `economy-type` 或 `economy-plugin` 怎么写，见[经济格式](../format/economyformat-tm.md)页面。

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

## 商品 C - 用命令发货

有时你用的第三方插件物品我们并不支持，这时做一家命令商店就能绕过去。

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

* 可以先执行 `/shop generateitemformat` 生成一段 **ItemFormat**，填进 `display-item`。这样即使没配任何真实物品，插件也能靠 `display-item` 里的内容在商店里把它显示出来。
* 这个商品没有配 `sell-prices`，所以无法出售。
* 购买后会执行 `buy-actions`，也就是执行你设置的那条命令。动作的写法见[动作格式](../format/action-format.md)页面。

## 季节性商品

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
