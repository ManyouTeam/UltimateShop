# 🏆示例：每日奖励

奖励与商店很相似，但奖励没有任何价格，通常需要与菜单系统配合。

## 每日奖励示例

在`/shops/`文件夹中创建新的商店配置，然后创建新的商品，如下所示：&#x20;

```yaml
  A:
    price-mode: CLASSIC_ALL
    product-mode: CLASSIC_ALL
    products:
      1:
        material: DIAMOND
        amount: 16
      2:
        material: IRON_INGOT
        amount: 64
      3:
        economy-plugin: Vault
        amount: 1500
    buy-prices:
      1:
        economy-type: exp
        amount: 0
        placeholder: 'Free'
    buy-limits:
      default: '1'
    buy-times-reset-mode: 'TIMED'
    buy-times-reset-time: '00:00:00' 
```

在这个例子中，玩家每天可以购买一次这个“商品”，而且由于价格是免费的，所以我们可以将其视为“奖励”。

如果你想让VIP 玩家获得额外64x面包，那么只需使用我们的条件系统，如下所示：

```yaml
  A:
    price-mode: CLASSIC_ALL
    product-mode: CLASSIC_ALL
    products:
      1:
        material: DIAMOND
        amount: 16
      2:
        material: IRON_INGOT
        amount: 64
      3:
        economy-plugin: Vault
        amount: 1500
      4:
        material: BREAD
        amount: 64
        conditions:
          1:
            type: permission
            permission: 'group.vip'
    buy-prices:
      1:
        economy-type: exp
        amount: 0
        placeholder: 'Free'
    buy-limits:
      default: '1'
    buy-times-reset-mode: 'TIMED'
    buy-times-reset-time: '00:00:00' 
```

如果你还想VIP玩家有**50%**的机会获得额外的1x钻石剑，那么我们需要在`config.yml`创建一个新的随机占位符：

```yaml
  random:
    chance:
      reset-mode: ONCE
      reset-time: '00:00:00'
      elements:
        - '1~100'
```

然后也使用条件系统：

```yaml
  A:
    price-mode: CLASSIC_ALL
    product-mode: CLASSIC_ALL
    products:
      1:
        material: DIAMOND
        amount: 16
      2:
        material: IRON_INGOT
        amount: 64
      3:
        economy-plugin: Vault
        amount: 1500
      4:
        material: BREAD
        amount: 64
        conditions:
          1:
            type: permission
            permission: 'group.vip'
      5:
        material: BREAD
        amount: 64
        conditions:
          1:
            type: permission
            permission: 'group.vip'
          2:
            type: placeholder
            placeholder: '{random_chance}'
            rule: '>'
            value: '50'
    buy-prices:
      1:
        economy-type: exp
        amount: 0
        placeholder: 'Free'
    buy-limits:
      default: '1'
    buy-times-reset-mode: 'TIMED'
    buy-times-reset-time: '00:00:00' 
```
