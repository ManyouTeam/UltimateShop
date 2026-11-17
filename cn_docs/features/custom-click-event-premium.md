# 🎮自定义点击事件 - 付费版

* 从 2.5.1 起，可为商店 GUI 中的商品设置自定义点击事件。
* 在 `config.yml` 中找到以下配置。

{% hint style="info" %}
支持为指定商品覆盖默认点击事件，详见[商品](../shops/products.md)。
{% endhint %}

```yaml
  # Support value: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/inventory/ClickType.htm
  # Support use ;; symbol to make multi click type.
  click-event:
    buy: 'SHIFT_LEFT'
    sell: 'RIGHT'
    buy-or-sell: 'LEFT'
    # If you want to disable select-amount feature, set this to NEVER.
    select-amount: 'SHIFT_RIGHT'
    sell-all: 'DROP'
    buy-one-stack: 'SWAP_OFFHAND'
  # Custom click actions for shop menu.
  # Premium version only.
  click-event-actions:
    buy-one-stack:
      display-name: 'Buy One Stack'
      buy-only: true
      1:
        type: buy
        shop: '{shop}'
        item: '{item}'
        amount: 64
    sell-one-stack:
      display-name: 'Sell One Stack'
      sell-only: true
      1:
        type: sell
        shop: '{shop}'
        item: '{item}'
        amount: 64
```

* 此处创建了名为 `buy-one-stack` 的自定义点击事件，执行的动作会购买 64 个该商品。
* 重载服务器后，将光标放在商品上按 **F**，就会执行 `click-event-actions` 中的动作；本例中即购买 64 个该物品。

{% hint style="info" %}
商品的自动附加描述与点击事件由不同代码实现。插件无法根据自定义事件自动生成匹配的描述。例如，把右键由默认出售改为购买后，还需要手动修改自动附加描述，才能正确显示“右键购买商品”。
{% endhint %}

## 配置选项

`click-event-actions` 中的每个动作均支持以下选项，如上例所示：

* `display-name`：显示给玩家的易读名称。
* `buy-only`：仅在商品可购买（存在购买价格）时显示此事件按钮。
* `sell-only`：仅在商品可出售（存在出售价格）时显示此事件按钮。

这些选项仅作用于 Bedrock 表单 UI。

## 效果展示

<figure><img src="../.gitbook/assets/image (13).png" alt=""><figcaption></figcaption></figure>

<figure><img src="../.gitbook/assets/image (14).png" alt=""><figcaption></figcaption></figure>

本例中的 **Buy One Stack（购买一组）** 按钮仅对配置了购买价格的商品显示。

## 示例：只通过批量购买菜单交易

本例中，玩家必须打开批量购买菜单并选择数量后才能交易。左键打开批量购买流程，右键打开批量出售流程。

{% hint style="info" %}
请同时更新自动附加描述，使商品说明与实际点击事件一致。
{% endhint %}

```yaml
  # Support value: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/inventory/ClickType.html
  # Support use ;; symbol to make multi click type.
  click-event:
    buy: 'NEVER'
    sell: 'NEVER'
    buy-or-sell: 'NEVER'
    select-amount: 'NEVER'
    sell-all: 'NEVER'
    buy-more-buy: 'LEFT'
    buy-more-sell: 'RIGHT'
  # Custom click actions for shop menu.
  # Premium version only.
  click-event-actions:
    buy-more-buy:
      display-name: 'Buy'
      buy-only: true
      1:
        type: buy_more_menu
        shop: '{shop}'
        item: '{item}'
        buy-more-menu:
          menu: buy-more-buy
          max-amount: 128
    buy-more-sell:
      display-name: 'Buy'
      sell-only: true
      1:
        type: buy_more_menu
        shop: '{shop}'
        item: '{item}'
        buy-more-menu:
          menu: buy-more-sell
          max-amount: 128  
```

## 示例：Java 保留默认行为，Bedrock 仅允许左键

保留 Java 玩家现有的 `menu.click-event` 映射，在 `config.yml` 的 `menu.bedrock` 下增加以下选项：

```yaml
menu:
  bedrock:
    enabled: true
    check-method: FLOODGATE
    click-event: 'select-amount'
```

Java 玩家仍可使用所有现有点击动作。Bedrock 玩家只能通过 `LEFT`（点按商品）打开批量购买菜单；右键、Shift 点击、Q 和 F 均不会触发商品动作。表单和对话框中的商店商品按钮会直接执行该动作，不再先打开商品信息界面。批量购买菜单内的数量选择与确认控件仍按其自身规则工作。

商品必须设置 `buy-more: true`，且批量购买菜单有效。如果服务器通过 UUID 识别 Bedrock 玩家，请将 `FLOODGATE` 改为 `UUID`。该选项可以填写 `select-amount` 等内置动作，也可填写 `menu.click-event-actions` 中定义的自定义事件名。省略或设为空字符串时，沿用原有 Bedrock 商品信息流程；设为 `none` 则禁用 Bedrock 商品点击。

商品或子按钮可以用相同的单个字符串覆盖全局设置：

```yaml
items:
  A:
    buy-more: true
    bedrock:
      click-event: 'select-amount'
```

优先级为：子按钮设置 → 目标商品设置 → 全局 `menu.bedrock.click-event`。Java 玩家始终使用原有的商品/全局 `click-event` 映射。这些覆盖只作用于商店商品和子按钮，不作用于批量购买确认按钮。

修改附加描述时，Java 提示使用 `@y`，Bedrock 提示使用 `@x`，例如 `@x&eClick to select an amount`，详见[展示物品的附加描述](../menus/display-item-add-lore.md)。

### 自定义购买/出售动作使用默认数量

自定义动作中设置 `amount: '{amount}'`，即可使用本次点击对应的默认购买或出售次数，支持不同默认值和子按钮覆盖：

```yaml
menu:
  click-event:
    custom-buy: 'LEFT'
    custom-sell: 'RIGHT'
  bedrock:
    click-event: 'select-amount'
  click-event-actions:
    custom-buy:
      display-name: 'Buy'
      1:
        type: buy
        shop: '{shop}'
        item: '{item}'
        amount: '{amount}'
    custom-sell:
      display-name: 'Sell'
      1:
        type: sell
        shop: '{shop}'
        item: '{item}'
        amount: '{amount}'
```

当 `default-buy-amount: 10`、`default-sell-amount: 5` 时，Java 自定义购买动作交易 10 次，出售动作交易 5 次。子按钮使用自身覆盖值，未设置时继承目标商品默认值。`amount: 1`、`amount: 64` 等显式值保持固定；省略 `amount` 时仍默认为 1。在商品点击以外的场景，`{amount}` 保留动作上下文原有含义。每次点击只执行一次动作列表，默认数量不会让动作列表重复执行。
