# 🔖Example: Discount

## Create Conditional Placeholder

Use conditional placeholder with MAX or MIN mode, discount feature can be easily implemented.

Here we created 2 conditional placeholder for `buy` and `sell`.

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

## Use the placeholder in product configs

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

* `5` is base price, then add `{conditional_buy}` or `{conditional_sell}` before it.
* If player meet vip condition, the `{condition_buy}` will return `0.5`, so he will only need pay `0.5*4` which means only `2.5` coins!

## Auto Apply Discount

* Start from 2.3.2, you can auto apply conditional placeholder to all prices! Just configure it in `config.yml` file. This can helps you auto add discount for each products.

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

* It is recommend you disable this feature for dynamic price, if you want to enable it for dynamic price too, just change `black-dynamic-price` option to false here!
* If you want to disable this feature for specified shops, use `black-shops` option.
