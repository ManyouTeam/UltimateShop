# 🔖示例：折扣

## 创建条件占位符

把条件占位符和 MAX 或 MIN 模式配合起来，就能很轻松地做出折扣效果。

下面为 `buy` 和 `sell` 各创建了一个条件占位符。

```yaml
mode: MAX

value:
  default: 1
  vip: 1.5
  mvp: 2

conditions:
  vip:
    1:
      type: permission
      permission: 'group.vip'
  mvp:
    1:
      type: permission
      permission: 'group.mvp'
```

```yaml
mode: MIN

value:
  default: 1
  vip: 0.5
  mvp: 0.3

conditions:
  vip:
    1:
      type: permission
      permission: 'group.vip'
  mvp:
    1:
      type: permission
      permission: 'group.mvp'
```

## 在商品配置中使用占位符

```yaml
items:
  A:
    price-mode: CLASSIC_ALL
    product-mode: CLASSIC_ALL
    products:
      1:
        material: sea_lantern
        lore: 
          - '%player_health%'
    buy-prices:
      1:
        economy-plugin: Vault
        amount: '{conditional_buy} * 5' # <--- Changed line
        start-apply: 0
        placeholder: '{amount} Coins'
    sell-prices:
      1:
        economy-plugin: Vault
        amount: '{conditional_sell} * 5' # <--- Changed line
        start-apply: 0
        placeholder: '{amount} Coins'
```

* `5` 为底价，然后在其前添加 `{conditional_buy}` 或 `{conditional_sell}`。
* 若玩家满足 VIP 条件，`{conditional_buy}` 会返回 `0.5`，此时他只需支付 `0.5*4`，也就是 `2.5` 个金币。

## 自动应用折扣

* 从 2.3.2 起，可以把条件占位符自动套用到所有价格上，只需在 `config.yml` 中配置一次，就能为每个商品自动加折扣。

```yaml
placeholder:
  auto-settings:
    add-conditional-in-all-price-amount:
      enabled: false
      buy-placeholder: buy
      sell-placeholder: sell
      black-dynamic-price: true
      black-shops:
        - 'example'
```

* 建议对动态价格关闭这项功能；如果动态价格也需要，把 `black-dynamic-price` 改成 `false` 即可。
* 想对某些商店单独关闭该功能，用 `black-shops` 指定即可。
