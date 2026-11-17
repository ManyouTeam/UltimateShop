# 🔴动态价格状态 - 付费版

## 动态价格状态

可以在价格配置段的 `amount` 中使用 `{status}` 占位符，例如：

```yaml
    buy-prices:
      1:
        economy-plugin: Vault
        amount: '550+{buy-times-server}*0.2-{sell-times-server}*0.1'
        max-amount: 5500
        min-amount: 325
        placeholder: '&6{amount} Coins {status}' # <--- We use {status} here.
        start-apply: 0
```

使用此占位符时，必须确保 `amount` 中的第一个数值就是基础价格。例如上例的基础价格为 **550**。

也可以启用 `config.yml` 中的 `add-status-in-dynamic-price-placeholder`，让插件自动附加状态，无需为每个价格占位符手动添加。

## 可用占位符

* {base}：动态价格的基础价格，即 `amount` 中的第一个数值。
* {compare}：当前价格。

这些占位符也可以在 `{compare_<xxx>}` 占位符中使用。

## 显示价格变化百分比

可以通过公式计算基础价格与当前价格之间的变化百分比。找到 `config.yml` 中对应内容，按以下示例修改即可：

```yaml
  # Premium version only
  compare:
    up: '↑ &c+{math_(({compare}-{base})/{base}) * 100}% Debug: {compare} {base}'
    down: '↓ &a-{math_(({base}-{compare})/{base}) * 100}% Debug: {compare} {base}'
    same: '-'
```

## 效果展示

<figure><img src="../.gitbook/assets/image (4).png" alt=""><figcaption></figcaption></figure>
