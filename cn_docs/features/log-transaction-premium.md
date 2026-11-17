# 💳交易日志 - 付费版

在 `config.yml` 中找到以下内容：

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

* `storage`：支持 `file` 和 `database`。使用 `database` 时，需要在 `config.yml` 中设置 `database.enabled: true`。
  * 数据库的结构化列：`created_at`、`player_uuid`、`player_name`、`shop_id`、`shop_name`、`item_id`、`item_name`、`action`、`amount`、`multiplier`。
* `file` 留空时，仅将日志输出到控制台；否则写入此处指定的文件，文件必须为 TXT 格式。

## 日志格式可用占位符

* {player}
* {player-uuid}
* {item}：商品 ID。
* {item-name}：商品显示名称。
* {shop}：商店 ID。
* {shop-name}：商店显示名称。
* {buy-or-sell}
* {price}
* {time}：日志时间。
* {multiplier}：[出售倍率](../shops/sell-multiplier-premium.md)的最终结果。

## 效果展示

<figure><img src="../.gitbook/assets/image (12).png" alt=""><figcaption></figcaption></figure>
