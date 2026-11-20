# 📅示例：每日商店/轮换商店

{% hint style="info" %}
此示例仅适用于 **UltimateShop** 的 <mark style="color:red;">**付费版**</mark> 版本！\
本例用随机占位符在同一家商店里展示不同商品；你也可以用条件占位符，让不同条件显示不同商品，示例见[这里](../placeholders/conditional-placeholder-premium.md#example-conditional-product)。
{% endhint %}

## 创建随机占位符

我们需要先创建一个随机占位符。&#x20;

把随机占位符配置保存为 `placeholders/random_placeholders/daily.yml`。

这个配置每天从 `elements` 抽出 5 个商品 ID，菜单显示抽取结果里的前三项。`elements` 里的 ID 要和商店文件 `items` 下的键一致。

随机占位符的更多说明见[随机占位符](../placeholders/random-placeholder-premium.md)页面。

```yaml
reset-mode: TIMED
reset-time: '00:00:00'
element-amount: 5
per-player-element: false
element-sort: true
elements:
  - 'redstone'
  - 'iron_ingot'
  - 'gold_ingot'
  - 'copper_ingot'
  - 'diamond'
  - 'lapis_lazuli'
  - 'emerald'
```

## 配置商店

本例用到的各项选项在[商店](shops.md)页面有详细说明，若不确定某项的作用可以查阅该页。

添加 `A` 到 `G` 共 7 个商品，它们的 ID 必须与占位符里的元素一致。

```yaml
settings:
  menu: 'daily-shop-menu' # The menu ID
  buy-more: true
  shop-name: 'Daily Shop (Require Premium)'
  hide-message: false

general-configs:
  price-mode: CLASSIC_ANY
  product-mode: CLASSIC_ANY
  sell-limits:
    global: 640
    default: 18
    vip: 256
  sell-limits-conditions:
    vip:
      1:
        type: permission
        permission: 'group.vip'
  sell-times-reset-mode: 'COOLDOWN_TIMED'
  sell-times-reset-time: '{random_reset}'

items:
  redstone:
    products:
      1:
        material: REDSTONE
        amount: 1
    sell-prices:
      1:
        economy-plugin: Vault
        amount: 1
        placeholder: '&6{amount} Coins'
        start-apply: 0
  iron_ingot:
    products:
      1:
        material: IRON_INGOT
        amount: 1
    sell-prices:
      1:
        economy-plugin: Vault
        amount: 3
        placeholder: '&6{amount} Coins'
        start-apply: 0
  gold_ingot:
    products:
      1:
        material: GOLD_INGOT
        amount: 1
    sell-prices:
      1:
        economy-plugin: Vault
        amount: 1.6
        placeholder: '&6{amount} Coins'
        start-apply: 0
  copper_ingot:
    products:
      1:
        material: COPPER_INGOT
        amount: 1
    sell-prices:
      1:
        economy-plugin: Vault
        amount: 2
        placeholder: '&6{amount} Coins'
        start-apply: 0
  diamond:
    products:
      1:
        material: DIAMOND
        amount: 1
    sell-prices:
      1:
        economy-plugin: Vault
        amount: 0.8
        placeholder: '&6{amount} Coins'
        start-apply: 0
  lapis_lazuli:
    products:
      1:
        material: LAPIS_LAZULI
        amount: 1
      2:
        material: EMERALD
        amount: 1
    sell-prices:
      1:
        economy-plugin: Vault
        amount: 1.2
        placeholder: '&6{amount} Coins'
        start-apply: 0
      2:
        economy-plugin: Vault
        amount: 3.3
        placeholder: '&6{amount} Coins'
        start-apply: 0
  emerald:
    products:
      1:
        material: EMERALD
        amount: 1
    sell-prices:
      1:
        economy-plugin: Vault
        amount: 5
        placeholder: '&6{amount} Coins'
        start-apply: 0
```

## 配置菜单

本例用到的各项选项在[菜单](../menus/general-menus.md)页面有详细说明，若不确定某项的作用可以查阅该页。

每个商品槽先用随机占位符选商品。如果该 ID 无法在这个槽位显示，就用 `P` 显示商店中按顺序对应的商品。

```yaml
# PREMIUM version only.

title: '{shop-name}'
size: 36

open-actions:
  1:
    type: sound
    sound: item.book.page_turn

dynamic-layout: true

shop-items: ['P']

layout:
  - '000000000'
  - '000`{random_daily;;1}||P``{random_daily;;2}||P``{random_daily;;3}||P`000'
  - '000000000'
  - 'a0003000b'

buttons:
  3:
    display-item:
      material: ARROW
      name: '&c« Go back'
    actions:
      1:
        type: open_menu
        menu: main
```

## 效果展示

<figure><img src="../.gitbook/assets/image.png" alt=""><figcaption></figcaption></figure>
