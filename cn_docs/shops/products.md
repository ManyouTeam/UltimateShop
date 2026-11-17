# 🛒商品

下面给出两个商品配置的示例：

```yaml
items:
  A:
    display-name: "Apple"
    price-mode: ANY
    product-mode: ALL
    products:
      1:
        material: APPLE
        amount: 1
      2: 
        material: BREAD
        amount: 5
        conditions:
          1:
            type: permission
            permission: 'group.vip'
        give-actions:
          1:
            type: message
            message: 'Wow, seems that you are a VIP player, so we bonud give you 5 breads!'
    buy-prices:
      1:
        economy-plugin: Vault
        amount: 200
        placeholder: '{amount} Coins'
        start-apply: 0
      2:
        economy-plugin: PlayerPoints
        amount: 10
        placeholder: '{amount} Points'
        start-apply: 5
    sell-prices:
      1:
        economy-plugin: Vault
        amount: 50
        placeholder: '{amount} Coins'
      2:
        economy-plugin: PlayerPoints
        amount: 1
        start-apply: 5
        placeholder: '{amount} Points'
        give-actions:
          1: 
            type: message: 
            message: 'Wow, seems that you have already sell 5 apples!'
    buy-actions:
      1:
        type: player_command
        command: 'say %player_name% purchased an Apple!'
      2:
        type: announcement
        message: '&7%player_name% purchased an Apple!'
  B:
    display-item:
      material: BREAD
      name: '&cSuper Bread'
    display-name: "Bread"
    add-lore:
      - '@a&ePurchase: {buy-price}'
      - '@b&eSell: {sell-price}'
      - '&eDrop to buy, right to sell'
    click-event:
      buy: 'DROP'
      sell: 'RIGHT'
    bedrock:
      hide: false
      icon: 'url;;https://raw.githubusercontent.com/Jens-Co/MinecraftItemImages/main/1.20/bread.png'
    buy-more: true
    buy-more-menu:
      menu: buy-more-2
      max-amount: 16
    price-mode: ANY
    product-mode: ALL
    products:
      1:
        material: BREAD
        amount: 1
    buy-prices:
      1:
        economy-plugin: Vault
        amount: 200
        placeholder: '{amount} Coins'
        start-apply: 0
      2:
        economy-plugin: PlayerPoints
        amount: 10
        placeholder: '{amount} Points'
        start-apply: 5
    sell-prices:
      1:
        economy-plugin: Vault
        amount: 50
        placeholder: '{amount} Coins'
      2:
        economy-plugin: PlayerPoints
        amount: 1
        start-apply: 5
        placeholder: '{amount} Points'
    buy-limits:
      global: 100
      default: 10
      test-condition: 20
    buy-limits-conditions:
      test-condition:
        1:
          type: permission
          permission: 'test.permission'
    buy-times-reset-mode: 'TIMED'
    buy-times-reset-time: '00:00:00'
  C:
    display-item: 
      material: DIAMOND
    as-sub-button: A
```

## 配置结构

```
Shop file
└─ Product entry
   ├─ display item and menu behavior
   ├─ products       → what changes hands as the product
   ├─ buy-prices     → what the player pays when buying
   ├─ sell-prices    → what the player receives when selling
   ├─ buy-conditions/sell-conditions     → who may use or see it
   ├─ buy-actions/sell-actions           → what else happens
   └─ buy-limits/sell-limits             → how often it may be used
```

<a id="general-options"></a>

## 常规选项

{% hint style="info" %}
点击[此处](shops.md) 查看这些常规选项的详细示例。
{% endhint %}

* display-item：商店菜单里代表该商品的展示物品，可以和玩家购买后实际拿到的物品不同。虚拟商品必须在这里设置 `display-item`，否则无法在 GUI 中显示；真实物品商品则可以把这一节省掉——先在 `config.yml` 的 `display-item` 下开启 `auto-set-first-product`，开启后若未设置 `display-item`，会自动用第一个真实物品作为展示物品。本节使用[物品格式](../format/itemformat-tm/)。**可选（不设置时取第一条 products）。**
  * display-item.modify-lore：是否自动改写展示物品的描述，把价格、限购等信息写进去。默认内容可在 `config.yml` 中配置，也能用商品级的 `add-lore` 覆盖，详见[展示物品附加描述](../menus/display-item-add-lore.md)页面。**可选（默认 true）。**
* display-name：设置 `{product}` 占位符以及批量购买菜单中该商品的显示名称。**可选（不设置时使用展示物品的名称）。**
* add-lore：为本商品单独设置[展示物品的附加描述](../menus/display-item-add-lore.md)；不设置则使用 `config.yml` 中的默认值。**可选。**
* click-event：为本商品单独设置点击事件，不设置则使用 `config.yml` 中的默认值。记得同步改 `add-lore`，让提示文字和实际点击方式对得上。**可选。**

```yaml
    add-lore:
      - '@a&ePurchase: {buy-price}'
      - '@b&eSell: {sell-price}'
      - '&eDrop to buy, right to sell' # Modified add lore to to correspond to the modified click event
    click-event:
      buy: 'DROP'
      sell: 'RIGHT'
```

* bedrock：参见[此页面](../menus/bedrock-menus-premium.md)。
* buy-more：设置本商品能否打开批量购买菜单。**可选（默认 true）。**
* buy-more-menu：为本商品单独指定批量购买菜单。**可选，需要 2.2.10+（不设置则使用 `config.yml` 中的默认值）。**

```yaml
    buy-more: true
    sell-all: true
    price-modifier: false
    hide-message: false
    buy-more-menu:
      menu: buy-more-2
      max-amount: 16
```

* sell-all：设置本商品能否使用一键全卖功能。**可选，默认 true（3.9.3 起）。**
* price-modifier：为本商品启用全局 `sell.price-modifier` 规则。启用后不再直接显示出售价格，只能通过物品出售菜单卖出：点 `sell` 或 `sell-all` 会打开该菜单，购买、选择数量和其他自定义操作照常。即使 `sell-all` 为 false，这类商品仍可在物品出售菜单中卖出。详见[价格修改器](price-modifier.md)。**可选，默认 false。**
* shared-use-times：请查看[本页](shared-product-data-premium.md)。
*   hide-message：是否隐藏玩家买入或卖出本商品后的提示消息。**可选，默认 false（4.2.11 起）。**

    <div data-gb-custom-block data-tag="hint" data-style="warning" class="hint hint-warning"><p>默认只隐藏成功买入/卖出的提示。至于达到上限、余额不足这类失败提示，有两种隐藏办法：</p><ul><li>同时在 <code>config.yml</code> 中把 <code>placeholder.click.enabled</code> 设为 <code>true</code>。失败原因会显示在展示物品描述里，但会额外消耗性能。</li><li>同时在 <code>config.yml</code> 中把 <code>force-display-fail-message</code> 设为 <code>true</code>。推荐这种方式。（4.2.11+）</li></ul></div>
* buy-prices/sell-prices/products：请查看[本页](products-config-single-thing/)。
* price-mode/product-mode：支持`ANY, ALL, CLASSIC_ANY, CLASSIC_ALL`。  请参阅[模式](products.md#mode)了解更多信息。 **必填。**
* buy-actions：购买本商品后执行的动作，使用[动作格式](../format/action-format.md)。**可选。**&#x20;
* sell-actions：卖出本商品后执行的动作，使用[动作格式](../format/action-format.md)。**可选。**
* fail-actions：买入或卖出失败时执行的动作，使用[动作格式](../format/action-format.md)。**可选。**&#x20;
* buy-conditions：购买本商品需要满足的条件，使用[条件格式](../format/condition-format.md)。**可选。**
* sell-conditions：卖出本商品需要满足的条件，使用[条件格式](../format/condition-format.md)。**可选。**
* buy-limits：设置购买/出售次数上限。**可选，不设置则不限次数。**
  * buy-limits.global：全局上限，所有玩家共用。**可选。**
  * buy-limits.default：不满足下面任何条件的玩家使用这个上限。**设置了 buy-limits 时必填。**
* buy-limits.\<条件 ID>：满足该条件的玩家使用对应的上限。条件写法见[条件格式](../format/condition-format.md)。例如：

```yaml
buy-limits:
  default: 10
  vip: 20
buy-limits-conditions:
  vip: 
    1:
      type: permission
      permission: 'test.permission'
```

* sell-limits：用法与 buy-limits 相同，只是作用于出售。

{% hint style="info" %}
如何重置限制，请查看[本页](product-config-buy-sell-times-reset.md)。
{% endhint %}

## 单个条目选项

本节包含以下选项：

* buy-prices
* sell-prices
* products

这些选项在单独页面介绍，请[点击这里](products-config-single-thing/)查看。

## 购买/出售次数重置选项

本节包含以下选项：

* buy-times-reset-mode
* buy-times-reset-time
* buy-times-reset-time-format
* buy-times-reset-value
* buy-times-max-value
* sell-times-reset-mode&#x20;
* sell-times-reset-time
* sell-times-reset-time-format
* sell-times-reset-value
* sell-times-max-value

这些选项在单独页面介绍，请[点击这里](product-config-buy-sell-times-reset.md)查看。

<a id="dynamic-value"></a>

## 动态值

商品配置里 `buy-prices`、`sell-prices` 的 `amount`，以及 `buy-limits`、`sell-limits` 的数值，都可以使用占位符（包括 PlaceholderAPI）和[数学计算格式](../format/math-calculate-format.md)。

默认情况下动态值是实时计算的，但不会定时刷新界面。玩家在 GUI 里看到的值，只会在每次打开界面或点击物品时重新算一次。举例来说：购买价用了动态值，玩家打开界面后这个值又变了，界面上的显示不会跟着变，但真正下单时插件会按最新值扣款。这样做是为了兼顾服务器性能与开销。如果你希望界面上也立刻刷新，请[点击这里](../menus/general-menus.md#all-buttons-update)。

可用的内置占位符如下，详细说明见[内置占位符](../placeholders/built-in-placeholders.md)页面。

* {buy-times-player}
* {buy-times-server}
* {buy-total-player} <mark style="color:red;">**- 付费版, 3.9.0+**</mark>
* {buy-total-server} <mark style="color:red;">**- 付费版, 3.9.0+**</mark>

{% hint style="info" %}
**times** 系占位符与 **total** 系占位符的区别：

* **times** 系每次重置后都会归零，重新从你设置的值开始计数。
* **total** 系重置后依然保留历史数据，会一直累加。用命令设置购买/出售次数同样会算进 total。累加到 int 上限时会自动归零。
{% endhint %}

* {sell-times-player}
* {sell-times-server}
* {sell-total-player} <mark style="color:red;">**- 付费版, 3.9.0+**</mark>
* {sell-total-server} <mark style="color:red;">**- 付费版, 3.9.0+**</mark>
* {last-buy-player} <mark style="color:red;">**- 付费版**</mark>

表示该玩家距上次购买本商品的秒数。若玩家从未买过，或购买次数已被重置，则返回 `0`。

* {last-buy-server} <mark style="color:red;">**- 付费版**</mark>

表示全服距上次购买本商品的秒数。若服务器上还没人买过，或购买次数已被重置，则返回 `0`。

* {last-sell-player} <mark style="color:red;">**- 付费版**</mark>

表示该玩家距上次卖出本商品的秒数。若玩家从未卖过，或出售次数已被重置，则返回 `0`。

* {last-sell-server} <mark style="color:red;">**- 付费版**</mark>\
  表示全服距上次卖出本商品的秒数。若服务器上还没人卖过，或出售次数已被重置，则返回 `0`。
* {last-buy-reset-player} <mark style="color:red;">**- 付费版, 3.9.0+**</mark>

表示该玩家距上次购买次数重置（或重置后首次购买）的秒数，具体取决于你选的重置模式，详见[本页](product-config-buy-sell-times-reset.md)。若玩家从未买过，或购买次数已被重置，则返回上次购买时间。

* {last-buy-reset-server} <mark style="color:red;">**- 付费版, 3.9.0+**</mark>

表示全服距上次购买次数重置（或重置后首次购买）的秒数，具体取决于你选的重置模式，详见[本页](product-config-buy-sell-times-reset.md)。若服务器上还没人买过，或购买次数已被重置，则返回上次购买时间。

* {last-sell-reset-player} <mark style="color:red;">**- 付费版, 3.9.0+**</mark>

表示该玩家距上次出售次数重置（或重置后首次出售）的秒数，具体取决于你选的重置模式，详见[本页](product-config-buy-sell-times-reset.md)。若玩家从未卖过，或出售次数已被重置，则返回上次卖出时间。

* {last-sell-reset-server} <mark style="color:red;">**- 付费版, 3.9.0+**</mark>

表示全服距上次出售次数重置（或重置后首次出售）的秒数，具体取决于你选的重置模式，详见[本页](product-config-buy-sell-times-reset.md)。若服务器上还没人卖过，或出售次数已被重置，则返回上次卖出时间。

`buy-prices` 和 `sell-prices` 还额外支持两个选项：

* max-amount：价格上限，动态价格常用。**可选。**
* min-amount：价格下限，动态价格常用。**可选。**

`amount` 使用动态值时，可用 `min-amount` 和 `max-amount` 限制计算结果的下限和上限，动态价格场景很实用。

注意：使用我们 PlaceholderAPI 扩展的占位符时，必须写成新格式（不带花括号），例如：

```yaml
    buy-prices:
      1:
        economy-plugin: Vault
        amount: '15 - {sell-times-player} * 0.1 + %ultimateshop_farming_B_sell-times-player% * 0.1'
        # We use the new format without { and } symbol.
        placeholder: '{amount}$'
        start-apply: 0
```

另外，如果当前打开的菜单里也显示了这个商品，需要把 `menu.shop.click-update` 设为 `true`，否则卖出 B 商品后这个价格不会自动刷新。

<a id="default-trade-amounts"></a>

## 默认交易数量

在商品层级设置 `default-buy-amount` 和 `default-sell-amount`，可以控制直接买入、卖出时默认的交易笔数。默认均为 `1`，可取 `1` 到 `9999` 的整数；填了无效值会输出警告并回退为 `1`。

```yaml
items:
  A:
    default-buy-amount: 1
    default-sell-amount: 1
    products:
      1:
        material: DIAMOND
        amount: 1
    buy-prices:
      1:
        economy-plugin: Vault
        amount: 100
    sell-prices:
      1:
        economy-plugin: Vault
        amount: 50
  B:
    as-sub-button: A
    default-buy-amount: 10
    default-sell-amount: 5
    display-item:
      material: DIAMOND
      amount: 1
      lore:
        - '&eBuy {buy-amount} trades'
        - '&aSell {sell-amount} trades'
```

点击 B 会花 1000 买入 10 颗钻石，或以 250 卖出 5 颗钻石。子按钮上没单独设置的数值，会从它指向的商品那里继承，跨商店引用（`ShopID;;ProductID`）同样适用。

这里的数值是**交易笔数**，会与每条 products 配置的数量以及对应价格相乘。若一笔交易给 3 个物品，默认 10 笔就是 30 个。条件和限购按交易笔数计算。

这些默认值作用于直接点击 `buy`、`sell`、`buy-or-sell`，以及表单/对话框里最初的商品信息按钮。数量选择菜单、命令、动作和 API 调用中显式指定的数量保持原有含义；`sell-all` 的行为也不变。

展示的买价/卖价以及能否点击，各自按对应的数量计算。可以在显示名称、展示物品描述或 `add-lore` 中用 `{buy-amount}` 和 `{sell-amount}` 同时显示两个数量。`{amount}` 在可购买时表示买入笔数，否则表示卖出笔数；在数量选择菜单里仍表示当前选中的数量。若未设置 `display-item.amount`，且解析出的默认买入与卖出笔数相同，图标堆叠数会自动取该值；显式写了 `display-item.amount` 则始终以它为准。买入与卖出笔数不同时，保留原来的展示数量。子按钮使用自己解析出的默认值。这只是展示用的数量，不会再次乘上每笔的商品数量。`modify-lore: false` 依然会关闭自动改写描述。

在 Minecraft 1.20.5+ 上，`menu.buy-more-menu.display-item-max-stack: true` 还会把展示物品的最大堆叠数设为 `99`，包括显式写的展示数量和由默认买卖笔数推断出的数量。该设置只影响展示物品；关闭后保留配置值或原有的最大堆叠数。

## 子按钮 <mark style="color:red;">- 付费版</mark>

有时你需要在不同菜单里展示同一个商品，或者为同一个商品做多个按钮，这时可以用 `as-sub-button`。在这里填另一个商品 ID，这个按钮就会被当作那个商品来处理。

* display-item：支持为子按钮设置不同的展示物品。
* as-sub-button：在此输入 `Product ID` 或 `ShopID;;ProductID`。

**子按钮**的示例配置可以在[Shops](shops.md)页面找到，请查看头部示例中`items`下的`C`部分。

{% hint style="warning" %}
如果子按钮对应的商品没有显示在商店界面里，或者玩家不满足打开对应商店菜单的条件，该物品就无法交易。若不希望这样，把商店配置中的 `settings.secret-shop-items` 改为 `false`。
{% endhint %}

<a id="mode"></a>

## 模式

### ALL 示例：金币和钻石

```yaml
price-mode: ALL
buy-prices:
  coins:
    economy-plugin: Vault
    amount: 500
    placeholder: '&6$500'
  diamonds:
    material: DIAMOND
    amount: 8
    placeholder: '&b8 diamonds'
```

玩家必须同时支付这两项。

### ANY 示例：金钱或积分

```yaml
price-mode: ANY
buy-prices:
  money:
    economy-plugin: Vault
    amount: 1000
    placeholder: '&6$1,000'
  points:
    economy-plugin: PlayerPoints
    amount: 50
    placeholder: '&e50 points'
```

只会采用其中一个满足条件的选项，具体哪些选项可用由条件决定。

### 何时不使用 CLASSIC 模式

如果条目依赖分步处理——比如动态价格、`start-apply`/`end-apply`，或每条目各自的复杂条件——请用 `ALL` 或 `ANY`。只有配置完全符合「立即生效」的简单模型时，才用 `CLASSIC_` 系列。

<table><thead><tr><th width="118">模式</th><th width="179">ANY</th><th>ALL</th><th>CLASSIC_ANY</th><th>CLASSIC_ALL</th></tr></thead><tbody><tr><td>发放商品</td><td>随机发放符合条件的商品。</td><td>发放全部商品。</td><td>同 ANY。</td><td>同 ALL。</td></tr><tr><td>扣除商品/价格</td><td>取第一条「玩家满足条件且数量足够」的商品/价格。</td><td>玩家必须备齐所有符合条件的商品/价格才能出售。</td><td>同 ANY。</td><td>同 ALL。</td></tr><tr><td>发放价格（即出售）</td><td>取第一条满足条件要求的价格。</td><td>发放全部价格。</td><td>同 ANY。</td><td>同 ALL。</td></tr><tr><td>价格支持</td><td>支持动态价格 &#x26; <code>apply</code> 选项。</td><td>同 ALL。</td><td>每次价格都必须相同。</td><td>同 CLASSIC_ALL。</td></tr><tr><td>products 的 <code>amount</code> 使用动态值时能否全卖</td><td><strong>不能</strong><br>数量是动态算出来的，插件无法得知最多能卖多少次。</td><td><strong>不能</strong><br>数量是动态算出来的，插件无法得知最多能卖多少次。</td><td>能</td><td>能</td></tr><tr><td>服务器性能</td><td>买卖请求多时开销可能偏高。</td><td>同 ALL。</td><td>低，和别的商店插件一样。</td><td>同 CLASSIC_ANY。</td></tr></tbody></table>
