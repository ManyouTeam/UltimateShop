# 💴简易价格

{% hint style="danger" %}
此功能的代码已长期未更新，可能存在问题。插件作者不计划继续修复或更新该功能，请自行承担使用风险。
{% endhint %}

在 `config.yml` 的 `prices` 下定义简易价格，例如：

```yaml
prices:
  example:
    economy-plugin: Vault
    placeholder: '200 Coins'
  mmoitems-example:
    hook-plugin: MMOItems
    hook-item: AXE;;TEST_AXE
    amount: 1
    placeholder: '1 Mythic Axe'
```

这里定义了 `example` 和 `mmoitems-example` 两种简易价格。可在商品的 `buy-prices` 或 `sell-prices` 中通过 `type` 引用，例如：

```yaml
products:
  A:
    give-item: true
    price-mode: ALL
    product-mode: ALL
    products:
      1:
        material: APPLE
        amount: 1
    buy-prices:
      1:
        custom-type: example # <--- New Option!
        amount: 20
        start-apply: 0
      2:
        custom-type: mmoitems-example # <--- New Option!
        placeholder: '{amount} axes'
        start-apply: 0
```
