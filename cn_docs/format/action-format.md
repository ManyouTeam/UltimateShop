# 🎬 动作格式

动作格式由若干选项组成。

{% hint style="info" %}
**动作格式**示例中的 `actions` 仅表示从该处开始的**动作格式**。具体选项名称请参考对应功能的页面说明，例如 `buy-actions`。
{% endhint %}

## 常规选项

#### 应用次数

仅当玩家已购买或出售指定次数的商品时，该动作才会执行。

可选；如果不设置，则每次都会执行该动作。

* start-apply：该动作从第几次开始应用。**可选，默认为 0。**
* end-apply：该动作应用到的最后一次。**可选，默认为无限。**
* apply：该动作在哪些次数上应用，格式：`[1,2,3,4]`。**可选，默认使用 start-apply 选项的值。**

```yaml
    actions:
      1:
        apply: [1,2,3,4,5]
        start-apply: 1
        end-apply: 5
```

#### 全部出售只执行一次 / 批量购买只执行一次

当一次出售多个数量或多个商品时，添加该选项表示只执行第一个商品的动作。这对声音类动作特别有用；如果不添加，每个商品的声音动作都会执行。

```yaml
    actions:
      1:
        sell-all-once: true # In sell all
        multi-once: true # In buy more menu
```

#### 仅打开一次

仅对菜单的 open-actions 选项生效。启用后，只有玩家首次打开的菜单才会触发该动作；也就是说，如果打开的菜单是从另一个菜单跳转而来，则该动作不会被触发。

```yaml
    actions:
      1:
        open-once: true
```

#### 点击类型

仅当玩家使用该点击类型激活动作时才会执行。对菜单配置中的 `open-actions`、`close-action`，以及商品配置中的 `buy-actions`、`sell-actions` 均无效。

可选；如果不设置，该动作对每种点击类型都会执行。

```yaml
    actions:
      1:
        click-type: LEFT 
```

### 仅限 Java 版/仅限基岩版 <mark style="color:red;">- 付费版</mark>

仅当玩家是 Java 版或基岩版玩家时才执行该动作。

可选；如果不设置，Java 版和基岩版玩家都会执行该动作。

```yaml
    actions:
      1:
        java-only: true
        bedrock-only: true
```

### 失败类型

仅支持商品配置中的 `fail-actions`。

支持的失败类型：

* ERROR
* PERMISSION
* PLAYER\_MAX
* SERVER\_MAX
* REQUIRE\_CONDITION\_NOT\_MEET
* NOT\_ENOUGH
* INVENTORY\_FULL
* API\_CANCEL

```yml
    fail-actions:
      1:
        fail-type: 'PLAYER_MAX'
        type: message
        message: 'Hello'
```

## 可用占位符

* {world}
* {amount}

### 物品层级的 `buy-actions` 和 `sell-actions`

在物品本身上使用 `buy-actions` 或 `sell-actions` 时，`{amount}` 表示购买或出售流程为该物品层级准备的交易数量。

实际表现如下：

* 如果 `display-item.calculate-amount` 为 `false`，物品动作层的 `{amount}` 始终为 `1`
* 如果 `display-item.calculate-amount` 为 `true`，`{amount}` 为交易数量乘以展示物品数量

示例：

* 展示物品数量：`1`
* 玩家购买 `5`
* 物品的 `buy-actions` 中 `{amount} = 5`

再看一个例子：

* 展示物品数量：`64`
* 玩家购买 `5`
* 物品的 `buy-actions` 中 `{amount} = 320`

### 单项的 `give-actions` 和 `take-actions`

在单项上使用 `give-actions` 或 `take-actions` 时，`{amount}` 同样表示该单项实际最终的发放或扣除数量。

* {player\_x}
* {player\_y}
* {player\_z}
* {player\_pitch}
* {player\_yaw}
* {player}
* {item} - 商品 ID
* {item-name} - 商品显示名称
* {shop} - 商店 ID
* {shop-name} - 商店显示名称
* {shop-menu} - 商店的菜单 ID

## 声音

向玩家播放声音。

```yaml
    actions:
      1:
        type: sound
        sound: 'ui.button.click'
        volume: 1
        pitch: 1
```

## 消息

向玩家发送消息，支持颜色代码。

```yaml
    actions:
      1:
        type: message
        message: 'Hello!'
```

## 标题

向玩家发送标题，支持颜色代码。

```yaml
    actions:
      1:
        type: title
        main-title: 'Good day'
        sub-title: 'Not bad'
        fade-in: 10
        stay: 70
        fade-out: 30
```

## 粒子

```yaml
    actions:
      1: 
        type: particle
        particle: HEART
        count: 20
        offset-x: 0.3
        offset-y: 1.0
        offset-z: 0.3
        speed: 0.01
```

## 公告

向所有在线玩家发送消息，支持颜色代码。&#x20;

```yaml
    actions:
      1:
        type: announcement
        message: 'Hello!'
```

## 药水效果

给予玩家药水效果。

```yaml
    actions:
      1:
        type: effect
        potion: BLINDNESS
        duration: 60
        level: 1
        ambient: true # Optional
        particles: true # Optional
        icon: true # Optional
```

## 传送

将玩家传送到指定位置。

```yaml
    actions:
      1:
        type: teleport
        world: LobbyWorld
        x: 100
        y: 30
        z: 300
        pitch: 90 # Optional
        yaw: 0 # Optional
```

## 玩家命令

让玩家执行一条命令。

```yaml
    actions:
      1:
        type: player_command
        command: 'tell Hello!'
```

## OP 命令

以 OP 身份让玩家执行一条命令。

```yaml
    actions:
      1:
        type: op_command
        command: 'tell Hello!'
```

## 控制台命令

让控制台执行一条命令。

```yaml
    actions:
      1:
        type: console_command
        command: 'op {player}'
```

## 生成原版生物

生成原版生物。

```yaml
    actions:
      1:
        type: entity_spawn
        entity: ZOMBIE
        world: LOBBY # Optional
        x: 100.0 # Optional
        y: 2.0 # Optional
        z: -100.0 # Optional
```

## 生成 MythicMobs 生物

需要安装 MythicMobs。

```yaml
    actions:
      1:
        type: mythicmobs_spawn
        entity: Super_Skeleton
        level: 1 # Optional
        world: LOBBY # Optional
        x: 100.0 # Optional
        y: 2.0 # Optional
        z: -100.0 # Optional
```

## 打开通用菜单

打开指定的通用菜单。

```yaml
    actions:
      1:
        type: open_menu
        menu: main
```

## 打开商店菜单

```yaml
    actions:
      1:
        type: shop_menu
        shop: farming
```

## 打开批量购买菜单

```yaml
    actions:
      1:
        type: buy_more_menu
        shop: farming
        item: A
```

## 使用自定义批量购买菜单设置打开批量购买菜单 <mark style="color:red;">- 付费版</mark>

```yaml
    actions:
      1:
        type: buy_more_menu
        shop: farming
        item: A
        buy-more-menu:
          menu: buy-more-buy
          max-amount: 128
```

## 打开全部出售菜单

```yaml
    actions:
      1:
        type: sell_all_menu
```

## 购买商品

`amount: '{amount}'` 会解析为动作上下文中的数量。在自定义商品点击事件中，它使用 `default-buy-amount`，包括子按钮的覆盖值。显式指定的数量保持不变；省略 `amount` 时默认为 1。解析得到的数量必须是 1 到 9999 之间的整数。

```yaml
    actions:
      1:
        type: buy
        shop: food
        item: A
        amount: 5 # Optional
```

## 出售商品

`amount: '{amount}'` 会解析为动作上下文中的数量。在自定义商品点击事件中，它使用 `default-sell-amount`，包括子按钮的覆盖值，该值可以与默认购买数量不同。显式指定的数量保持不变；省略 `amount` 时默认为 1。`sell-all: true` 会保留现有的最大出售量行为。

```yaml
    actions:
      1:
        type: sell
        shop: food
        item: A
        amount: 5 # Optional
        sell-all: true # Optional
```

## 关闭

关闭物品栏。

```yaml
    actions:
      1:
        type: close
```

## 延迟 <mark style="color:red;">- 付费版</mark>

让动作在 X 个游戏刻后执行。

```yaml
    actions:
      1:
        type: delay
        time: 50
        wait-for-player: true
        actions:
          1:
            type: entity_spawn
            entity: ZOMBIE
```

## 概率 <mark style="color:red;">- 付费版</mark>

设置该动作的执行概率，最大为 100。50 表示该动作有 50% 的概率执行。

```yaml
    actions:
      1:
        type: chance
        rate: 50
        actions:
          1:
            type: entity_spawn
            entity: ZOMBIE
```

## 任意 <mark style="color:red;">- 付费版</mark>

随机选择指定数量的动作来执行。

```yaml
    actions:
      1:
        type: any
        amount: 2
        actions:
          1:
            type: entity_spawn
            entity: ZOMBIE
          2:
            type: entity_spawn
            entity: SKELETON
          3:
            type: entity_spawn
            entity: WITHER
```

## 条件 <mark style="color:red;">- 付费版</mark>

只有满足此处所设条件的玩家才能执行该动作。

```yaml
    actions:
      1:
        type: conditional
        conditions:
          1: 
            type: world
            world: lobby
        actions:
          1:
            type: entity_spawn
            entity: ZOMBIE
```

## 连接 <mark style="color:red;">- 付费版</mark>

需要在 config.yml 中启用 `bungeecord-sync.enabled` 选项，并正确配置 BungeeCord。更多信息请查看[多服务器同步](../features/multi-server-sync-premium.md)页面。

```yaml
    actions:
      1:
        type: connect
        server: 'lobby'
```

## 更新 GUI

更新已打开 GUI 中的所有按钮，但不会更新 GUI 标题。

```yaml
    actions:
      1:
        type: update_gui
```

## 更新 GUI 标题 <mark style="color:red;">- 付费版</mark>

需要服务器启用标题更新功能，更多信息请查看[本页面](../menus/general-menus.md#title-update-premium)。

```yaml
    actions:
      1:
        type: update_title
```

## 添加收藏 <mark style="color:red;">- 付费版</mark>

将商品添加到收藏列表。

<pre class="language-yaml"><code class="lang-yaml"><strong>    actions:
</strong>      1:
        type: add_favourite
        menu: favourite # The favourite menu.
        shop: '{shop}'
        item: '{item}'
</code></pre>

## 移除收藏 <mark style="color:red;">- 付费版</mark>

将该商品从收藏列表中移除。

```yaml
   actions:
      1:
        type: remove_favourite
        menu: favourite # The favourite menu.
        shop: '{shop}'
        item: '{item}'
```

## 切换收藏 <mark style="color:red;">- 付费版</mark>

切换商品的收藏状态：如果已收藏则移除，如果未收藏则添加。

```yaml
   actions:
      1:
        type: toggle_favourite
        menu: favourite # The favourite menu.
        shop: '{shop}'
        item: '{item}'
```

## 提示 <mark style="color:red;">- 付费版</mark>

要求玩家先在聊天栏输入内容，然后才继续执行后续动作。

```yml
    actions:
      1:
        type: prompt
        description: '&eEnter a note'
        reopen-on-submit: true
        reopen-on-cancel: true
        actions:
          1:
            type: message
            message: '&aSaved note: &f{arg}'
        cancel-actions:
          1:
            type: message
            message: '&cYou cancelled the prompt'
```

**`{arg}`**

完整的原始输入字符串。

如果玩家输入：

```
diamond sword 64
```

那么：

```
{arg} -> diamond sword 64
```

**`{arg_1}`, `{arg_2}`, `{arg_3}` ...**

按空白字符拆分后的各个参数。

如果玩家输入：

```
diamond sword 64
```

那么：

```
{arg_1} -> diamond
{arg_2} -> sword
{arg_3} -> 64
```

## 返回&#x20;

返回上一个菜单；如果上一个菜单不存在，则打开 menu 选项中指定的菜单。

```yaml
    actions:
      1:
        type: back
        menu: main
```

```yaml
    actions:
      1:
        type: back
        shop: 'flowers'
```
