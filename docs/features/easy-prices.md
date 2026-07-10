# 💴Easy Prices

{% hint style="warning" %}
❌This feature is no longer get support and will be <mark style="color:red;">removed</mark> in future version.
{% endhint %}

You can use `prices` section in `config.yml` file to create new easy prices!

For exmaple:

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

This has 2 easy prices, one is `example`, the other is `mmoitems-example`. You can use them at shop products `buy-prices` and `sell-prices` section with type option. Just like:

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
