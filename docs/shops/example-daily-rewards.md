# 🏆Example: Daily Rewards

Rewards is very similar to Shops, but rewards don't have any price, and usually, it needs to be coordinated with the menu system.

## Example for Daily Rewards

Create new shop config at `/shops/` folder, then create new product like this:&#x20;

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

In this example, players can purchase this "product" once each day, and because the price is free, so we can consider it as "reward".

If you want to make VIP players have bonus 64x bread, then just use our conditions system, like this:

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

If you also want to VIP players have **50%** chance to get bonus 1x diamond sword, then we need create a new random placeholder at `config.yml`:

```yaml
  random:
    chance:
      reset-mode: ONCE
      reset-time: '00:00:00'
      elements:
        - '1~100'
```

Then also use condition system:

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
