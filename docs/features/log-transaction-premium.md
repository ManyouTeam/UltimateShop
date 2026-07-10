# 💳Log Transaction - Premium

Open `config.yml` and find below contents:

```yaml
# Premium version only.
log-transaction:
  # It will cost extra performance cost.
  enabled: false
  # file | database — database requires database.enabled: true
  storage: file
  # Used when storage is file. If set to empty value, we will just print the log into console.
  file: 'log.txt'
  format: '{time} | {player} | {shop} | {buy-or-sell} | {item-name} x{amount} | {price} | Price Multiplier: x{multiplier}'
  time-format: "yyyy-MM-dd HH:mm:ss"
```

* storage: Supports `file` and `database`. `database` requires `database.enabled: true` in `config.yml` file.
  * Database structured columns: `created_at`, `player_uuid`, `player_name`, `shop_id`, `shop_name`, `item_id`, `item_name`, `action`, `amount`, `multiplier`
* If you set `file` option to empty, we will just print the log into console. Otherwise, we will log into the file you put here. The file must be a txt file.

## Format available placeholders:

* {player}
* {player-uuid}
* {item} - Product ID
* {item-name} - Product Display Name
* {shop} - Shop ID
* {shop-name} - Shop Display Name
* {buy-or-sell}
* {price}
* {time} - Display the log time
* {multiplier} - The result value of [Sell Multiplier](../shops/sell-multiplier-premium.md).

## Showcase

<figure><img src="../.gitbook/assets/image (13).png" alt=""><figcaption></figcaption></figure>
