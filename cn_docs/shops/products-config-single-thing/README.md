# 💰商品配置：单项

本页讲 `products`、`buy-prices`、`sell-prices` 这三节，按插件在代码里实际的运作方式来解释。

如果只记一条，请记这个：

* **单项**就是 `products`、`buy-prices`、`sell-prices` 下面的一个条目。
* 一个商品可以有多个单项。
* 插件先判断哪些单项可用、算出各自数量、校验条件，再把它们发放或扣除。

## 什么是单项？

同一个商店物品里，下面这三节都是由单项组成的：

#### `products`

买入时玩家拿到什么，卖出时玩家必须交出什么。可选。

若没有 `products`：

* 买入时依然会执行 `buy-actions`
* 卖出时依然会执行 `sell-actions`
* 但买入不会给玩家任何物品
* 卖出也不要求玩家交出任何物品

做命令商店时这种配置很有用。

#### `buy-prices`

买入需要支付什么。可选；不写就无法购买。

#### `sell-prices`

卖出后玩家得到什么。可选；不写就无法出售。

示例：

```yaml
items:
  A:
    products:
      1:
        material: emerald
      2:
        material: diamond
    buy-prices:
      1:
        economy-plugin: Vault
        amount: 100
        placeholder: '{amount}$'
    sell-prices:
      1:
        economy-plugin: Vault
        amount: 20
        placeholder: '{amount}$'
```

上例中：

* `products.1` 是一个单项
* `products.2` 是一个单项
* `buy-prices.1` 是一个单项
* `sell-prices.1` 是一个单项

## 插件如何使用单项？

玩家买卖时，插件并不是从上到下顺着读这一节，而是按下面的流程处理：

1. 读出 `products`、`buy-prices` 或 `sell-prices` 里的所有单项。
2. 用 `apply-conditions`（旧版写法是 `conditions`）过滤一遍。
3. 按这一节的模式（`ANY`、`ALL`、`CLASSIC_ANY`、`CLASSIC_ALL`）决定选中哪些单项。
4. 计算每个被选中单项的实际数量。
5. 检查玩家持有量是否足够。
6. 检查 `require-conditions`。
7. 执行 `give-actions` / `take-actions`，实际发放或扣除。

这就是 `apply-conditions` 与 `require-conditions` 不是一回事的原因。

## 单项的类型

插件会根据你在单项里写的选项，判断它属于哪种类型。

### 原版物品

用普通的[物品格式](../../format/itemformat-tm/README.md)告诉插件：你想在商店里卖什么原版物品，或想让玩家支付什么。**（购买/出售/商品）**

```yaml
products:
  1:
    material: emerald
  2:
    material: diamond
    amount: 16
```

在以下情况下使用：

* 这个对象就是普通的 Minecraft 物品
* 你希望插件真的去比对、发放或扣除实际物品

### 挂钩物品

用[支持的插件](../../info/compatibility.md)提供的物品，告诉插件你想卖什么自定义物品、或想让玩家支付什么。该类型同样使用[物品格式](../../format/itemformat-tm/README.md)。**（购买/出售/商品）**

```yaml
products:
  1:
    hook-plugin: MMOItems
    hook-item: 'AXE;;MAGIC_AXE'
```

在以下情况下使用：

* 该物品由其他物品插件提供
* 你仍希望 UltimateShop 把它当作物品来处理

### 匹配物品

用[自定义物品匹配方式](../../features/custom-item-match-method.md)里的匹配规则，告诉插件要匹配哪些物品。**（购买/商品）**

```yaml
products:
  1:
    match-item:
      contains-lore:
        - 'Magic Flight Paper'
    amount: 64
```

在以下情况下使用：

* 没有精确的物品 ID 可供比对
* 你想按物品描述、NBT、名称或其他自定义规则来匹配

### 原版经济 / 挂钩经济

直接操作经济余额，而不是物品。写法见[经济格式](../../format/economyformat-tm.md)。

```yaml
buy-prices:
  1:
    economy-plugin: Vault
    amount: 15
    placeholder: '{amount}$'
```

在以下情况下使用：

* 玩家需要支付或获得金钱、点券、经验、等级之类的东西

### 自定义 <mark style="color:red;">- 付费版</mark>

当对象既不是普通物品、也不是已支持的经济类型时，用 `match-placeholder`。

```yaml
buy-prices:
  1:
    match-placeholder: '%player_health%'
    amount: 5
    placeholder: '{amount} Health'
    take-actions:
      1:
        multi-once: true
        type: console_command
        command: 'health take {player} {amount}'
```

在以下情况下使用：

* 想比较任意自定义数值
* 插件本身并不支持那种货币或系统

要注意：

* 占位符只负责读出玩家当前有多少
* 真正修改数值的是 `take-actions` / `give-actions`

### 免费 / 空

如果某个单项既没有物品数据、经济数据，也没有 `match-item` 或 `match-placeholder`，它就被视为免费。

适用于：

* 命令商店
* 权限商店
* 只靠动作发奖的奖励

## 单项的选项

下面这些是实际配置时最常用到的选项。

#### `amount`

单项的基础数量。

可以是：

* 一个固定数字
* PlaceholderAPI 变量，可用的内置占位符见[本页](../products.md#dynamic-value)。&#x20;
* 数学表达式，写法见[数学计算格式](../../format/math-calculate-format.md)。
* 动态公式

示例：

```yaml
amount: '55 + ({buy-times-server} - {sell-times-server}) * 0.1'
```

#### `apply-conditions`

这个单项是否参与本次交易。

#### `require-conditions`

被选中之后，是否允许这笔交易真的使用它。

#### `give-actions`

把这个单项发放给玩家时执行的动作。

常见用途：

* 命令商店的奖励
* 权限奖励
* 自定义货币奖励

#### `take-actions`

从这个单项扣除时执行的动作。

常见用途：

* 扣除自定义货币
* 执行一条当作花费的命令
* 与其他插件同步

#### `give-item`

只对「像物品的奖励」有意义。

设为 `false` 时，插件不会真的发放该物品，但 `give-actions` 照常执行。

大多数命令商店都是这么做出来的。

#### `take`

设为 `false` 时，该单项仍会作为条件被检查，但实际不会被扣除。

适合这种需求：

* 必须有这件物品
* 但不想把它消耗掉

### `apply-conditions` 与 `require-conditions` 的区别

这是整套机制里最关键的部分。

#### `apply-conditions`

`apply-conditions` 决定这个单项到底要不要纳入本次交易。

条件不满足时：

* 该单项被忽略
* 不会被选中
* 不参与价格/商品的计算

适合用来做：

* 仅 VIP 可用的价格档位
* 季节限定商品
* 不同玩家给不同奖励
* 按权限或占位符区分价格

示例：

```yaml
products:
  1:
    material: PAPER
    amount: 1
  2:
    material: PAPER
    amount: 2
    apply-conditions:
      1:
        type: permission
        permission: group.vip
```

结果：

* 普通玩家用的是 `products.1`
* VIP 玩家还能用上 `products.2`

#### `require-conditions`

`require-conditions` 不负责挑选。它管的是：单项已经被选中、数量也已经算好之后，这笔交易还能不能继续。

条件不满足时：

* 交易被拦下
* 商品/价格虽然被选中了，却用不了

适合用来做：

* 支付或获得某样东西之前先要求权限
* 玩家不满足某条额外规则就不给奖励
* 算出来的数量只在某些情况下才允许成交

示例：

```yaml
buy-prices:
  1:
    economy-plugin: Vault
    amount: 50
    placeholder: '{amount}$'
    require-conditions:
      1:
        type: permission
        permission: shop.buy.special
```

结果：

* 这个价格照样能被选中
* 但玩家没有 `shop.buy.special` 时购买会失败

### 与单项的 `give-actions` / `take-actions`、物品的 `buy-actions` / `sell-actions` 有何不同

这两层动作看着很像，但负责的事情并不一样。

#### 单项的 `give-actions` / `take-actions`

这些动作属于某一个具体的单项。

只有下面两条同时成立时才执行：

* 被选中的正是这个单项
* 并且确实在发放或扣除这个单项

因此它们最适合：

* 某一条特殊的奖励分支
* 某一条特殊的价格分支
* 某一条自定义货币分支
* 只该为某个单项执行的命令

#### 物品的 `buy-actions` / `sell-actions`

这些动作属于整个商店物品。

它们在下面情况执行：

* 整笔买入交易成功，或
* 整笔卖出交易成功

它们不绑定到某个具体的单项分支。

因此它们最适合：

* 全局的成功提示
* 日志记录
* 音效
* 每次买卖成功后都该执行的命令
* 作用于整个商品、而不是某一条价格或奖励分支的效果

#### 最关键的区别：`{amount}`

在单项的动作里：

* `{amount}` 表示该单项最终的数量

在物品的动作里：

* `{amount}` 表示买卖流程传来的、商品层级的交易数量
* 一般配置下，这就是界面上显示的那笔交易总量
* 若关闭了 `display-item.calculate-amount`，通常就是买入/卖出的份数

示例：

```yaml
products:
  1:
    material: PAPER
    amount: 4
    give-actions:
      1:
        type: message
        message: 'Single thing amount = {amount}'

buy-actions:
  1:
    type: message
    message: 'Item action amount = {amount}'
```

如果玩家买了 5 次：

* 单项的 `give-actions` 拿到的是该分支真实的数量
* 物品层级的 `buy-actions` 拿到的是购买流程准备好的交易数量

很多常规配置里两个值看起来差不多，但它们来自不同的层级，也该用在不同的事情上。

#### 实用规则

逻辑属于某一条被选中的价格或奖励时，用单项动作。

逻辑是「不论走了哪条分支，买卖成功后就该执行」时，用物品动作。

### 仅适用于价格的选项

这些选项主要在 `buy-prices` 和 `sell-prices` 中起作用。

#### `placeholder`

在 `{price}` 这类价格占位符中显示的名称。

```yaml
placeholder: '{amount}$'
```

价格一般都应该配置这一项。

支持的占位符：

* {amount}
* {status} - 显示动态价格的状态。
* {display} - 若价格是原版物品或挂钩物品，该占位符会显示它的名称。会额外消耗服务器性能。
* 所有 PlaceholderAPI 占位符。

#### `start-apply`、`end-apply`、`apply`

这几项控制价格在什么时候生效。

它们主要在有别于经典模式的价格模式下才有意义：

* `ANY`
* `ALL`

适合用来实现：

* 早买便宜
* 越买越贵
* 购买次数落在某段区间时使用某个价格

#### `min-amount` 与 `max-amount`

这两项用来夹住最终算出的动态数量。

适用于：

* 价格公式可能算得过小
* 价格公式可能算得过大

示例：

```yaml
buy-prices:
  1:
    economy-plugin: Vault
    amount: '55 + {buy-times-player} * 0.5'
    min-amount: 55
    max-amount: 500
    placeholder: '{amount}$'
```

## 关于模式：`ANY`、`ALL`、`CLASSIC_ANY`、`CLASSIC_ALL`

不必把细节全搞懂也能用，但记住下面这个概念会有帮助：

* `ANY`：每次只用一个适用的单项，且动态生效区间会随数量变化
* `ALL`：每次都使用所有适用的单项
* `CLASSIC_ANY`：每次用一个适用的单项，但更像是固定打包
* `CLASSIC_ALL`：所有适用的单项作为一个固定包使用

实用建议：

* 简单的固定价格商店用 `CLASSIC_ALL`
* 「从多个有效奖励/价格里挑一个」的配置用 `CLASSIC_ANY`
* 只有需要生效区间或更高级的动态行为时，才用 `ANY` / `ALL`

## 共享条件键

插件还支持通过 `config.yml` 指定共享的 apply 条件节：

```yaml
conditions:
  products-key: 'products-conditions'
  buy-prices-key: 'buy-prices-conditions'
  sell-prices-key: 'sell-prices-conditions'
  display-item-key: 'display-item-conditions'
```

这样就可以写成：

```yaml
products:
  one:
    material: REDSTONE
  two:
    material: IRON_INGOT

products-conditions:
  one:
    1:
      type: placeholder
      placeholder: '{random_daily}'
      rule: '=='
      value: 'A'
  two:
    1:
      type: placeholder
      placeholder: '{random_daily}'
      rule: '=='
      value: 'B'
```

要注意：

* 这些共享键只管 apply 条件
* `require-conditions` 仍然写在单项自己内部

## 两个最常见的套路

### 套路 A：普通物品商店

```yaml
A:
  price-mode: CLASSIC_ALL
  product-mode: CLASSIC_ALL
  products:
    1:
      material: APPLE
      amount: 64
  buy-prices:
    1:
      economy-plugin: Vault
      amount: 150
      placeholder: '{amount}$'
  sell-prices:
    1:
      economy-plugin: Vault
      amount: 30
      placeholder: '{amount}$'
```

这是最简单的配置：

* 买入给苹果
* 买入要花钱
* 卖出扣苹果
* 卖出给钱

### 套路 B：命令商店

```yaml
  A:
    price-mode: CLASSIC_ALL
    product-mode: CLASSIC_ALL
    display-item:
      name: 'Magic Crate Key'
      material: PAPER
      custom-model-data: 500
      amount: 1
    buy-prices:
      1:
        economy-plugin: Vault
        amount: 150
        placeholder: '{amount}⛂'
    buy-actions: # In product config
      1:
        type: console_command
        command: "crate give {player} magic" # Put command here.
      2:
        multi-once: true # If you want to use {amount} placeholder in your give item command, make sure add this line to make sure this action only execute once when purchase multi quantity.
        type: console_command
        command: "crate give {player} magic {amount}" 
  B:
    price-mode: CLASSIC_ALL
    product-mode: CLASSIC_ALL
    products:
      1:
        name: 'Magic Crate Key'
        material: PAPER
        custom-model-data: 500
        amount: 1
        give-item: false # You need add this to make sure the "fake" product will not give to player
        give-actions: # In single things config
          1:
            type: console_command
            command: "crate give {player} magic"
          2:
            multi-once: true # If you want to use {amount} placeholder in your give item command, make sure add this line to make sure this action only execute once when purchase multi quantity.
            type: console_command
            command: "crate give {player} magic {amount}" 
    buy-prices:
      1:
        economy-plugin: Vault
        amount: 150
        placeholder: '{amount}⛂'
```

上面两个例子最终效果完全一样。不过你想过没有：如果配合 `conditions` 选项，用 **give-actions** 的方式，还能让不同玩家走不同的条件！

```yaml
  B:
    price-mode: CLASSIC_ALL
    product-mode: CLASSIC_ALL
    products:
      1:
        name: 'Magic Crate Key'
        material: PAPER
        custom-model-data: 500
        amount: 1
        give-item: false # You need add this to make sure the "fake" product will not give to player
        give-actions: # In single things config
          1:
            multi-once: true
            type: console_command
            command: "crate give {player} magic {amount}"
      2:
        name: 'Magic Crate Key (VIP plus 1 for free)'
        material: PAPER
        custom-model-data: 500
        amount: 1
        give-item: false # You need add this to make sure the "fake" product will not give to player
        give-actions: # In single things config
          1:
            multi-once: true
            type: console_command
            command: "crate give {player} magic {amount}"
        conditions:
          1:
            type: permission
            permission: group.vip
    buy-prices:
      1:
        economy-plugin: Vault
        amount: 150
        placeholder: '{amount}⛂'
```

为什么这样能行：

* 有商品存在，插件才有东西可处理
* `give-item: false` 让那个「假物品」不会被真的发出去
* `give-actions` 用命令给出真正的奖励

### 逻辑该放在物品上，还是放在单项上？

规则只属于某一条价格或奖励时，用单项逻辑。

好的例子：

* 若干奖励里只有一条是 VIP 专属
* 若干价格里只有一种季节价
* 某条命令奖励只该在一个分支里执行

规则是「不论选中哪个单项，整个商品都要遵守」时，用物品层级的 `buy-actions`、`sell-actions`、`buy-conditions`、`sell-conditions`。

### 最佳实践

* 除非确实需要高级行为，否则从 `CLASSIC_ALL` 起步。
* 用 `apply-conditions` 来挑分支。
* 用 `require-conditions` 来拦截交易。
* 命令商店用 `give-item: false`。
* 需要「必须有但不能被消耗」时用 `take: false`。
* 先把价格和奖励写得小而明确，再加动态公式。
* 用了动态值就配上 `min-amount` 和 `max-amount`，避免算出离谱的结果。

### 一个简单的判断口诀

拿不准该用哪个选项时：

* 「这条分支该不该被忽略？」→ 用 `apply-conditions`
* 「这笔交易该不该失败？」→ 用 `require-conditions`
* 「插件要不要真的给/扣物品？」→ 用 `give-item` 或 `take`
* 「用到这个单项时要不要执行命令？」→ 用 `give-actions` / `take-actions`

有这个判断框架，大多数商店都能配对。

## 在物品描述中自动显示价格

* 下载并安装 MythicChanger，见[此处](https://www.spigotmc.org/resources/mythicchanger-auto-match-change-drag-change-gui-change-in-1-plugin-1-14-1-21-8.98523/)。（该插件需要 packetevents）
* 在 `plugins/MythicChanger/rules` 下新建一个名为 `shop-display.yml` 的文件。
* 把下面的内容复制进去，然后重启服务器。

```yaml
weight: 15

only-in-player-inventory: true

fake-changes:
  add-price-lore:
    - '&fBuy Price: &6{buy-price} &7per unit'
    - '&fSell Price: &6{sell-price} &7per unit'
    - '&fWorth: &6{total-price}'
```

<figure><img src="../../.gitbook/assets/9b0906ace8ba0a265f6711301c6be2f7.png" alt=""><figcaption></figcaption></figure>
