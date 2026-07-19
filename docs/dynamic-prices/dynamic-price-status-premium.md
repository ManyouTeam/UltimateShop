# 🔴Dynamic Price Status - Premium

## Dynamic Price Status

You can use `{status}` placeholder in price section's amount option, like this:

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

Please note that to use this placeholder, you have to make sure in `amount` option, the first number is base price. Like here, **550** is the base price.

You can also enable `add-status-in-dynamic-price-placeholder` option in `config.yml` to let plugin auto add this for you, you don't need have to manually add the placeholder at each price placeholder!

## Available Placeholders

* {base} - The base price of dynamic price. The first number in `amount` option will be consider as base price.
* {compare} - The now price.

Those placeholders will also available in `{compare_<xxx>}` placeholder.

## Display Percentage Change

If your math is good enough, I think you can easily find the formula for the percentage of the base price and the current price. If your math is not that good, then I can give you the formula.

Find those content in `config.yml` and change it like me:

```yaml
  # Premium version only
  compare:
    up: '↑ &c+{math_(({compare}-{base})/{base}) * 100}% Debug: {compare} {base}'
    down: '↓ &a-{math_(({base}-{compare})/{base}) * 100}% Debug: {compare} {base}'
    same: '-'
```

## Showcase

<figure><img src="../.gitbook/assets/image (4).png" alt=""><figcaption></figcaption></figure>
