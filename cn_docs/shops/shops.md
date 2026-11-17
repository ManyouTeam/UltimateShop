# 📂商店

商店文件示例如下：

```yaml
settings:
  menu: 'example-shop-menu'
  # Optional: inline menu config for this shop. You can use this without settings.menu.
  menu-settings:
    dynamic-layout: false
    layout:
      - '000000000'
      - '0ABCDEFG0'
      - '0HIJKLMN0'
      - '0OPQRSTU0'
      - '000000000'
      - 'a0003000b'
  buy-more: true
  shop-name: 'Food Shop'
  hide-message: false
  secret-shop-items: true
  custom-command:
    name: 'mineral'
    description: 'Custom Words'
  
general-configs:
  # This means all products in this shop will use this price mode and product mode.
  # unless they have set other value.
  price-mode: CLASSIC_ANY
  product-mode: CLASSIC_ANY
  # Support all product options, like prices, products, limits and so on!
  fail-actions:
    1:
      type: sound
      sound: block.note_block.bass
    
items:
  A: # Product A config
  #...
  B: # Product B config
  #...
  C: # Product C config
  #...

buttons:
  a:
    display-item:
      material: arrow
      name: '&cPrevious page'
      lore:
        - '&7Click to view previous page!'
    actions:
      1:
        type: shop_menu
        shop: 'crops'    
```

## 创建商店

在`plugins/UltimateShop/shops/<shopName>.yml` 创建配置文件：

{% code title="PUT_SHOP_ID_INTO_FILE_NAME.yml" %}
```yaml
settings:
  menu: starter-menu
  buy-more: true
  shop-name: Starter Shop
  hide-message: false

items:
  D:
    price-mode: CLASSIC_ALL
    product-mode: CLASSIC_ALL
    products:
      1:
        material: DIAMOND
        amount: 1
    buy-prices:
      1:
        economy-plugin: Vault
        amount: 100
        placeholder: '&6$100'
        start-apply: 0
    sell-prices:
      1:
        economy-plugin: Vault
        amount: 60
        placeholder: '&6$60'
        start-apply: 0
```
{% endcode %}

## 设置

*   menu：商店菜单名，也就是菜单配置的文件名，它是商店与菜单之间的连接点。本例中设为 `example-shop-menu`。

    菜单文件位于 `menus/shop_menu_templates` 文件夹，名为 `example-shop-menu.yml`。菜单的说明见[菜单](../menus/general-menus.md)页面。&#x20;
* menu-settings：可以为本店单独写一份菜单配置，其中的同名项会覆盖对应菜单配置。若这里已包含你需要的全部菜单选项，可以把 `menu` 删掉。
* buy-more：本店商品能否打开批量购买菜单。&#x20;
* shop-name：商店的显示名称，用在 `{shop-name}` 占位符中。&#x20;
* hide-message：是否隐藏玩家在本店买卖物品后的提示消息。&#x20;

{% hint style="warning" %}
默认只隐藏买入/卖出成功的提示。至于达到上限、余额不足这类失败提示，有两种隐藏办法：

* 同时在 `config.yml` 里把 `placeholder.click.enabled` 设为 `true`。失败原因会显示在展示物品描述中，但会额外消耗性能。
* 同时在 `config.yml` 里把 `force-display-fail-message` 设为 `true`。推荐这种做法。（4.2.11+）
{% endhint %}

* secret-shop-items：开启后，若玩家不满足该商品所在商店对应菜单的开启条件，或该商品并未显示在菜单里，它会自动隐藏且无法交易。判定规则在 `config.yml` 中设置。

```yaml
secret-shop-items:
  require-display-in-menu: true
  require-meet-menu-open-conditions: true
```

* custom-command：为本店自定义打开命令。不设置的话，这个菜单只能通过 `/shop menu` 打开。<mark style="color:red;">**（付费版）**</mark>

## 通用配置

这里设置的商品选项会作用于本店所有商品。其中 `buy-action`、`sell-actions`、`fail-actions` 会与商品配置里的同名项自动合并。

## 物品

items 就是商品。商品既可以是真实物品，也可以是虚拟的，比如 100 点宝石货币。

欲了解更多信息，请查看[商品](products.md)页面。

## 按钮

商店可以添加按钮，玩家点击后执行自定义动作。

对于每个按钮，我们都有以下选项：

* display-item：该按钮的展示物品，应使用[展示物品格式](../format/display-item-format.md)。
* actions：点击按钮后执行的动作，使用[动作格式](../format/action-format.md)。
* fail-actions：不满足按钮条件时执行的动作，使用[动作格式](../format/action-format.md)。
* conditions：该按钮的条件。玩家不满足时会执行 `fail-action`，写法见[条件格式](../format/condition-format.md)。
