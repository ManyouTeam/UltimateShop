# 开发指南

{% hint style="info" %}
请注意：UltimateShop 不是传统的商店插件。它可以动态展示商品与价格（甚至精确到每一份的单价），而其他商店插件是一个 ItemStack 对应一个价格。
{% endhint %}

## 添加依赖 <a href="#user-content-get-shop-object" id="user-content-get-shop-object"></a>

{% hint style="info" %}
截至 2026 年 3 月 20 日，插件最新版本号为 **4.3.4**。如果这个日期已经过去很久，请自行确认最新版本号——此处给出的版本可能已过时或无法拉取。
{% endhint %}

```xml
<repositories>
    <repository>
        <id>repo-lanink-cn</id>
        <url>https://repo.lanink.cn/repository/maven-public/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>cn.superiormc.ultimateshop</groupId>
        <artifactId>plugin</artifactId>
        <version>[PLUGIN VERSION]</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

```graphql
repositories {
    maven {
        url "https://repo.lanink.cn/repository/maven-public/"
    }
}

dependencies {
    compileOnly group: 'cn.superiormc.ultimateshop', name: 'plugin', version: '[PLUGIN VERSION]'
}

```

```kts
repositories {
    maven("https://repo.lanink.cn/repository/maven-public/")
}

dependencies {
    compileOnly("cn.superiormc.ultimateshop:plugin:[PLUGIN VERSION]")
}
```

## UltimateShop 开发者集成指南

本文面向需要与 UltimateShop 对接的开发者；只想配置商店的普通服主不必细读。

以下内容基于当前 `4.3.4` 版本的源码，聚焦最常见的对接场景：

* 在你自己插件中依赖 UltimateShop
* 按商店 ID / 商品 ID 获取商店物品
* 查询玩家的购买/出售次数并预览价格
* 监听交易事件
* 触发批量出售
* 从你的插件打开商店，或触发快速购买/出售

下面的代码片段刻意省略了无关的监听器注册、类包装与部分基础 import，只保留与 UltimateShop 对接直接相关的部分。

### 1. 先弄清 UltimateShop 对外提供了什么

从源码看，最合适的对外入口主要是这几个：

* `cn.superiormc.ultimateshop.api.ShopHelper`
* `cn.superiormc.ultimateshop.api.ItemPreTransactionEvent`
* `cn.superiormc.ultimateshop.api.ItemFinishTransactionEvent`

此外还有一些 `public` 类也能调用，但它们更偏向内部实现细节：

* `cn.superiormc.ultimateshop.objects.ObjectShop`
* `cn.superiormc.ultimateshop.objects.buttons.ObjectItem`
* `cn.superiormc.ultimateshop.gui.inv.ShopGUI`
* `cn.superiormc.ultimateshop.methods.Product.BuyProductMethod`
* `cn.superiormc.ultimateshop.methods.Product.SellProductMethod`

实践中的取舍是：

* 优先使用 `api` 包
* 只读查询物品/商店时用 `objects` 包
* 只有需要更直接的控制时才动 `gui` / `methods`，并接受它们可能在版本升级中发生变化

### 2. 在插件中声明依赖

#### `plugin.yml`

如果你的插件只在 UltimateShop 存在时才对接它，至少加上 `softdepend`：

```yaml
softdepend:
  - UltimateShop
```

如果你的插件离开 UltimateShop 就无法运行，改用 `depend`。

### 3. 运行时检查 UltimateShop 是否存在

```java
Plugin plugin = Bukkit.getPluginManager().getPlugin("UltimateShop");
if (plugin == null || !plugin.isEnabled()) {
    return;
}
```

如果对接逻辑只在 UltimateShop 存在时才启用，这段检查最好放在 `onEnable()` 里。

### 4. 最常用的入口：按 ID 获取物品

最稳妥的方式是用商店 ID 和商品 ID 直接取到目标物品：

```java
import cn.superiormc.ultimateshop.api.ShopHelper;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;

ObjectItem item = ShopHelper.getItemFromID("blocks", "diamond_block");
if (item == null) {
    return;
}

String shopId = item.getShop();
String productId = item.getProduct();
String displayName = item.getDisplayName(player);
```

推荐这种方式，因为结果是确定的。

`ObjectItem` 上常用的只读方法有：

* `getShop()`：所属商店 ID
* `getProduct()`: 商品 ID
* `getDisplayName(Player)`：显示名称
* `getBuyPrice()` / `getSellPrice()`：价格定义
* `getReward()`: 商品奖励定义
* `getPlayerBuyLimit(Player)` / `getPlayerSellLimit(Player)`：单个玩家的限购/限售
* `getServerBuyLimit(Player)` / `getServerSellLimit(Player)`：全局限制

### 5. 查询玩家当前的使用次数

如果想在自己的插件里显示“这名玩家还能买几次”之类的信息，直接用 `ShopHelper`：

```java
int buyTimes = ShopHelper.getBuyUseTimes(item, player);
int sellTimes = ShopHelper.getSellUseTimes(item, player);

int playerBuyLimit = item.getPlayerBuyLimit(player);
int playerSellLimit = item.getPlayerSellLimit(player);
```

典型用法：

```java
int used = ShopHelper.getBuyUseTimes(item, player);
int limit = item.getPlayerBuyLimit(player);

if (limit != -1) {
    int remain = Math.max(0, limit - used);
    player.sendMessage("You can still buy " + remain + " more times");
}
```

注意：

* `-1` 表示不限次数
* 若使用次数缓存尚不存在，`ShopHelper` 会自动创建

### 6. 预览购买费用

如果只是要在自己的 GUI 或消息里显示“这批物品值多少”，用 `ShopHelper` 的预览方法即可。

```java
ItemStack[] items = new ItemStack[]{player.getInventory().getItemInMainHand()};
String buyPriceDisplay = ShopHelper.getBuyPricesDisplay(items, player, 3);
if (buyPriceDisplay != null) {
    player.sendMessage("Buying 3 times costs: " + buyPriceDisplay);
}
```

### 7. 预览出售收益

```java
ItemStack[] items = new ItemStack[]{player.getInventory().getItemInMainHand()};
String sellPriceDisplay = ShopHelper.getSellPricesDisplay(items, player, 1);
if (sellPriceDisplay != null) {
    player.sendMessage("Selling gives: " + sellPriceDisplay);
}
```

如果需要的是结果对象而不是格式化后的字符串：

```java
ItemStack[] items = new ItemStack[]{player.getInventory().getItemInMainHand()};
TakeResult buyCost = ShopHelper.getBuyPrices(items, player, 3);
GiveResult sellReward = ShopHelper.getSellPrices(items, player, 1);
```

这些结果对象适合用于展示、记录或二次校验。

### 8. 监听交易事件

UltimateShop 提供两个交易事件：

* `ItemPreTransactionEvent`
* `ItemFinishTransactionEvent`

#### 8.1 `ItemPreTransactionEvent`

该事件在实际交易落定之前触发，适合用于：

* 统计
* 外部日志记录
* webhook 推送
* 旁路数据同步

示例：

```java
import cn.superiormc.ultimateshop.api.ItemPreTransactionEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class ShopListener implements Listener {

    @EventHandler
    public void onPreTransaction(ItemPreTransactionEvent event) {
        String action = event.isBuyOrSell() ? "BUY" : "SELL";
        String shopId = event.getShop().getShopName();
        String productId = event.getItem().getProduct();
        int amount = event.getAmount();

        event.getPlayer().sendMessage(
                "About to execute " + action + " -> " + shopId + ":" + productId + " x" + amount
        );
    }
}
```

该事件还会给出两个关键结果对象：

* `getTakeResult()`：本次交易要扣除什么
* `getGiveResult()`：本次交易要发放什么

通常可以这样理解：

* 购买时：`takeResult` 一般是玩家付出的价格，`giveResult` 一般是商品奖励
* 出售时：`takeResult` 一般是玩家交出的物品，`giveResult` 一般是出售收益

#### 8.2 `ItemFinishTransactionEvent`

该事件在交易成功完成后触发，适合用于：

* 成功统计
* 成就解锁
* 任务/进度系统对接
* 成功后的提示

```java
@EventHandler
public void onFinish(ItemFinishTransactionEvent event) {
    Player player = event.getPlayer();
    player.sendMessage("Transaction completed: " + event.getItem().getProduct());
}
```

### 9. 批量出售：`ShopHelper.sellAll`

如果想让自己的插件调用 UltimateShop 内置的批量出售逻辑，用：

```java
Map<AbstractSingleThing, BigDecimal> result =
        ShopHelper.sellAll(player, player.getInventory(), 1.0D);
```

第三个参数 `multiplier` 是出售收益的乘数。

例如：

* `1.0D`：原价
* `2.0D`：双倍出售收益
* `0.5D`：减半的出售收益

#### 这个方法已经真正完成了交易

这是最常见的误用点。

`sellAll(...)` 内部会直接跑一遍出售流程，也就是说它会：

* 匹配对应的商品
* 真正把已出售的物品扣掉
* 真正发放出售收益
* 执行出售动作

返回的 `Map<AbstractSingleThing, BigDecimal>` 更适合当成出售结果的汇总，而不是还需要再手动调一次 `giveThing(...)`。

错误示例：

```java
Map<AbstractSingleThing, BigDecimal> result = ShopHelper.sellAll(player, inv, 1.0D);
ShopHelper.giveThing(0, 0, player, 1.0D, result); // duplicates rewards
```

正确理解：

* `sellAll` 已完成交易
* 返回值仅用于显示、记录或统计

### 10. 手动执行发放 / 扣除

如果手上已有 `TakeResult` 或 `GiveResult` 并想自己执行，可以用结果对象自身的方法，也可以用 `ShopHelper` 上的辅助方法。

#### 执行扣除

```java
TakeResult takeResult = ShopHelper.getBuyPrices(player.getInventory(), player, 1);
if (takeResult != null && takeResult.getResultBoolean()) {
    takeResult.take(0, 1, player.getInventory(), player);
}
```

#### 用结果表执行发放

```java
GiveResult giveResult = ShopHelper.getSellPrices(player.getInventory(), player, 1);
if (giveResult != null) {
    boolean success = ShopHelper.giveThing(0, 1, player, 1.0D, giveResult.getResultMap());
}
```

#### 用结果表执行扣除

```java
ShopHelper.takeThing(0, 1, player.getInventory(), player, takeResult.getResultMap());
```

自己搭建自定义业务流时适合这样写；如果 UltimateShop 已经执行过这笔交易，就不要再重复执行一次。

### 11. 从自己的插件打开商店

#### 方案 A：走命令，兼容性最好

不想依赖内部 GUI 类的话，最稳妥的做法是直接派发内置命令：

```java
Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ultimateshop menu blocks Steve");
```

按目前的命令实现，常见写法是：

* 玩家自己打开商店：`/shop menu <shop>`
* 控制台替玩家打开商店：`/shop menu <shop> <player>`

这种做法的好处：

* 不直接依赖内部 GUI 类
* 日后内部实现变动时，兼容性更好

#### 方案 B：直接调用 `ShopGUI`

确实想走内部直连路径的话，可以这样写：

```java
import cn.superiormc.ultimateshop.api.ShopHelper;
import cn.superiormc.ultimateshop.gui.inv.ShopGUI;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;

ObjectItem item = ShopHelper.getItemFromID("blocks", "diamond_block");
if (item == null) {
    return;
}

ObjectShop shop = item.getShopObject();
ShopGUI.openGUI(player, shop, false, false);
```

参数含义：

* 第一个 `false`：是否绕过菜单条件
* 第二个 `false`：是否按“重新打开”处理

注意：`ShopGUI` 不在 `api` 包中。这属于直接调用内部类，比走命令更容易受版本变动影响。

### 12. 直接触发快速购买 / 出售

如果已经知道确切的商店和商品，也可以直接调用内部交易方法。

#### 直接购买

```java
import cn.superiormc.ultimateshop.methods.Product.BuyProductMethod;
import cn.superiormc.ultimateshop.methods.ProductTradeStatus;

ProductTradeStatus status = BuyProductMethod.startBuy(item, player, true, false, 3);
```

参数含义：

* `item`：目标商品
* `player`：玩家
* `true`：是否强制显示失败消息
* `false`：是否跳过实际成本
* `3`：交易数量

#### 直接出售

```java
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.methods.ProductTradeStatus;

ProductTradeStatus status = SellProductMethod.startSell(item, player, true, false, false, 3);
```

这几个布尔参数分别控制：

* 是否强制显示失败提示
* 是否跳过实际的成本 / 结算
* 是否启用“最大出售”逻辑

如果只是想让外部插件完成一次普通的买或卖，这是最直接的代码路径。

不过这属于内部 API，稳定性不如使用 `api` 包。

### 13. 实战示例：外部 GUI 入口 + 剩余次数显示

```java
ObjectItem item = ShopHelper.getItemFromID("blocks", "diamond_block");
if (item == null) {
    return;
}

int used = ShopHelper.getBuyUseTimes(item, player);
int limit = item.getPlayerBuyLimit(player);

if (limit != -1) {
    player.sendMessage("Remaining buy count: " + Math.max(0, limit - used));
}

ShopGUI.openGUI(player, item.getShopObject(), false, false);
```

这是非常常见的模式：

* 你自己的 NPC 或菜单插件只负责提供入口
* 商品逻辑、定价、限购与出售规则全部交给 UltimateShop

这样可以最大限度复用 UltimateShop 已有的机制。

### 14. 了解`TakeResult`、`GiveResult` 和 `AbstractSingleThing`

这三个类型放在一起最好理解：

* `AbstractThings` 表示一整个容器，比如完整的 `products`、`buy-prices` 或 `sell-prices` 段
* `AbstractSingleThing` 表示该容器中的单个条目
* `TakeResult` / `GiveResult` 表示一笔交易最终选中的条目，以及算出的最终数量

一个实用的理解模型：

* `AbstractThings` = 一组候选规则
* `AbstractSingleThing` = 一个候选分支
* `TakeResult` / `GiveResult` = 本次交易最终选中的分支

#### 14.1 `AbstractSingleThing` 是什么

`AbstractSingleThing` 是所有单条目的抽象基类，定义在 `AbstractSingleThing.java` 中。

概念上，它描述的是一份可以被检查、计算、发放或扣除的单元。

当前实现中常见的子类：

* `ObjectSingleProduct`：`products` 下的一条商品条目
* `ObjectSinglePrice`：`buy-prices` 或 `sell-prices` 下的一项价格条目

最关键的字段与职责：

* `type`：条目类型，由配置推断得出
* `singleSection`：该条目对应的配置节
* `applyCondition`：该分支是否参与筛选
* `requireCondition`：该分支被选中后是否允许继续完成
* `giveAction` / `takeAction`：真正发放或扣除时执行的动作
* `things`：所属的父级 `AbstractThings` 容器

支持的 `ThingType` 并不只有“物品”。判定逻辑在 `AbstractSingleThing.java` 中：

* `HOOK_ITEM`
* `MATCH_ITEM`
* `CUSTOM`
* `HOOK_ECONOMY`
* `VANILLA_ECONOMY`
* `VANILLA_ITEM`
* `FREE`
* `RESERVE`

所以要记住：`AbstractSingleThing` 不是“一个物品”，而是“一个可参与交易的单位”。

#### 14.1.1 开发人员如何判断单项内容是哪种类型

UltimateShop 不需要你手动声明 `ThingType`，它会从配置字段推断类型。

判定顺序很关键，实质上是“命中第一条规则即生效”。

按当前源码逻辑，规则是：

1. 如果`hook-plugin`和`hook-item`同时存在，则为`HOOK_ITEM`
2. 如果`match-item`存在且`MythicChanger`被加载，则为`MATCH_ITEM`
3. 如果`match-placeholder`存在并且这不是免费版本，则它是`CUSTOM`
4. 如果`economy-plugin`存在，则为`HOOK_ECONOMY`
5. 如果`economy-type`存在但`economy-plugin`不存在，则为`VANILLA_ECONOMY`
6. 如果`material`或`item`存在，则为`VANILLA_ITEM`
7. 如果只有`amount`式的数值定义且以上都不匹配，则为`RESERVE`
8. 如果以上都不匹配，则为`FREE`

可以记这张速查表： 仅

* `hook-plugin` + `hook-item` -> `HOOK_ITEM`
* `match-item` -> `MATCH_ITEM`
* `match-placeholder` -> `CUSTOM`
* `economy-plugin` -> `HOOK_ECONOMY`
* 只有 `economy-type` -> `VANILLA_ECONOMY`
* `material` 或 `item` -> `VANILLA_ITEM`
* 只有 `amount` -> `RESERVE`
* 什么都没定义 -> `FREE`

几个常见例子：

下面是一条 `Vault` 经济条目：

```yaml
buy-prices:
  1:
    economy-plugin: Vault
    amount: 100
    placeholder: '{amount}$'
```

要判断某条价格是否用了 Vault，可以这样写：

```java
Map<AbstractSingleThing, BigDecimal> resultMap = takeResult.getResultMap();
for (AbstractSingleThing singleThing : resultMap.keySet()) {
   if (singleThing.getSingleSection().getString("economy-plugin", "").equals("Vault") {
       return "This price includes Vault";
   }
}
```

#### 14.2 `AbstractSingleThing` 在交易期间做什么

它主要负责四件事：

1. 判断自己是否参与
2. 读取玩家当前拥有的数量
3. 检查玩家是否付得起，或是否有足够的空间接收
4. 执行真正的发放 / 扣除

关键方法有：

* `getApplyCondition(...)`：检查`apply-conditions`
* `getRequireCondition(...)`：检查`require-conditions`
* `playerHasAmount(...)`：读取已拥有的数量
* `playerHasEnough(...)`：检查是否足够，并可选地直接执行扣除
* `playerCanGive(...)`：检查该条目能否被发放

这也是为什么要分清这两类条件：

* `apply-conditions` 决定是否选择分支
* `require-conditions` 决定是否允许选定的分支继续

#### 14.3 `TakeResult` 是什么

`TakeResult` 表示“本次交易要扣除什么”，定义在 `TakeResult.java` 中。

它最核心的内部结构是：

```java
Map<AbstractSingleThing, BigDecimal> resultMap
```

也就是说：

* 键：被选中的条目
* 值：该条目在本次交易中最终算出的数量 / 成本

关键字段是：

* `resultBoolean`：玩家是否有足够的东西可付 / 可交
* `conditionBoolean`：所有被选条目是否都通过了 `require-conditions`
* `empty`：结果是否为空

最重要的区别是：

* `resultBoolean` 回答的是“够不够 / 有没有”
* `conditionBoolean` 回答的是“允不允许”

`TakeResult.addResultMapElement(...)` 会在添加条目的过程中检查 `require-conditions`。

真正的执行发生在 `take(...)` 里。

这里的“执行”并不只是删物品，它会：

* 对每个被选条目调用 `playerHasEnough(..., true, cost)`
* 再执行该条目的 `takeAction`

所以`TakeResult`最好理解为：

* 先回答“要扣什么”
* 再负责“执行扣除及相关动作”

#### 14.4 `GiveResult` 是什么

`GiveResult` 表示“本次交易要发放什么”，定义在 `GiveResult.java` 中。

与 `TakeResult` 一样，它也保存：

```java
Map<AbstractSingleThing, BigDecimal> resultMap
```

也就是说：

* 键：被选中的条目
* 值：该条目最终应发放的数量

它还会记录：

* `conditionBoolean`
* `empty`

但它没有 `resultBoolean` 字段。

因为“到底能不能发出去”在创建结果对象时还定不下来，要等到 `give(...)` 里把背包容量和实际投递一起评估后才知道。

`give(...)` 的流程是：

1. 遍历 `resultMap`
2. 对每个 `AbstractSingleThing` 调用 `playerCanGive(...)`
3. 收集所有 `GiveItemStack`
4. 只要有任一条目无法发放，就返回 `false`
5. 所有条目都能发放时，才真正发放

因此 `GiveResult` 应当这样理解：

* 它描述的是“应该发放什么”
* 能否真正送到玩家手上，要到 `give(...)` 时才见分晓

#### 14.5 它们在完整交易流程中的位置

购买流程可以这样理解：

1. `ObjectPrices.take(...)` 计算成本并返回`TakeResult`
2. `ObjectProducts.give(...)` 计算奖励并返回`GiveResult`
3. 交易逻辑检查`TakeResult.getResultBoolean()`
4. 然后检查 `TakeResult.getConditionBoolean()` 和 `GiveResult.getConditionBoolean()`
5. 然后执行`GiveResult.give(...)`
6. 最终执行`TakeResult.take(...)`

二者共用的容器抽象是 `AbstractThings.java`，它定义了：

* `give(...) -> GiveResult`
* `take(...) -> TakeResult`

因此对外最稳定的理解模型是：

* `AbstractThings` 从候选集合中挑出最终分支
* `AbstractSingleThing` 负责单个分支的逻辑
* `TakeResult` / `GiveResult` 保存并执行最终选中的结果

### 15. Hook 对接：经济来源与物品来源

UltimateShop 通过 `HookManager` 统一管理经济挂钩与物品挂钩。

内置实现会在启动时自动注册，例如：

* 经济：Vault、PlayerPoints、CoinsEngine、UltraEconomy 等
* 物品：ItemsAdder、Oraxen、MMOItems、EcoItems、Nexo、CraftEngine等

`HookManager` 也对外公开了注册方法：

* `registerNewEconomyHook(...)`
* `registerNewItemHook(...)`

也就是说，其他开发者可以从自己的插件注册自定义的经济或物品来源。

#### 15.1 自定义经济挂钩

抽象基类是`AbstractEconomyHook.java`。

需要实现的核心方法：

* `getEconomy(Player, currencyID)`：读取余额
* `takeEconomy(Player, value, currencyID)`：扣除
* `giveEconomy(Player, value, currencyID)`：存入

可选：

* `isEnabled()`：底层提供方尚未就绪时返回 `false`

`currencyID` 参数来自配置字段 `economy-type`：

```yaml
buy-prices:
  1:
    economy-plugin: MyEconomy
    economy-type: gems
    amount: 100
    placeholder: '{amount} Gems'
```

Vault 这类单货币挂钩通常会忽略 `currencyID`，而 CoinsEngine 这类多货币挂钩会用它确定具体货币。

最小示例：

```java
public final class MyEconomyHook extends AbstractEconomyHook {

    public MyEconomyHook() {
        super("MyEconomy");
    }

    @Override
    public double getEconomy(Player player, String currencyID) {
        return MyEconomyApi.balance(player.getUniqueId(), currencyID);
    }

    @Override
    public void takeEconomy(Player player, double value, String currencyID) {
        MyEconomyApi.withdraw(player.getUniqueId(), currencyID, value);
    }

    @Override
    public void giveEconomy(Player player, double value, String currencyID) {
        MyEconomyApi.deposit(player.getUniqueId(), currencyID, value);
    }
}
```

注册：

```java
HookManager.hookManager.registerNewEconomyHook("MyEconomy", new MyEconomyHook());
```

要点：

* 注册名必须与配置里的 `economy-plugin` 完全一致
* 若支持多种货币，就把 `economy-type` 当作你的 `currencyID`
* `AbstractEconomyHook.checkEconomy(...)` 的默认实现会先检查余额，需要时再调用 `takeEconomy(...)`

#### 15.2 自定义物品来源挂钩

抽象基类是`AbstractItemHook.java`。

需要实现两个核心方法：

* `getHookItemByID(Player, itemID)`：按配置的 ID 构建物品
* `getIDByItemStack(ItemStack)`：把物品反查回来源 ID

对应到配置就是：

```yaml
products:
  1:
    hook-plugin: MyItems
    hook-item: sword_of_flame
```

最小示例：

```java
public final class MyItemHook extends AbstractItemHook {

    public MyItemHook() {
        super("MyItems");
    }

    @Override
    public ItemStack getHookItemByID(Player player, String itemID) {
        ItemStack item = MyItemsApi.build(itemID);
        return item == null ? returnNullItem(itemID) : item;
    }

    @Override
    public String getIDByItemStack(ItemStack hookItem) {
        return MyItemsApi.findId(hookItem);
    }
}
```

注册：

```java
HookManager.hookManager.registerNewItemHook("MyItems", new MyItemHook());
```

要点：

* 注册名称必须与配置中的`hook-plugin`完全匹配
* `hook-item` 的实际格式完全由你的挂钩定义
* 若 `getIDByItemStack(...)` 无法解析出 ID，UltimateShop 仍能按 `hook-item` 直接构建物品，但反向识别物品来源的功能会失效

#### 15.3 值得参考的内置实现

想照着现成代码写，最值得参考的内置实现是：

* 经济挂钩：
  * `EconomyVaultHook`：单货币，基于 services-manager
  * `EconomyCoinsEngineHook`：多货币，显式使用 `currencyID`
* 物品挂钩：
  * `ItemItemsAdderHook`：简单 ID -> ItemStack 和 ItemStack -> ID
  * `ItemMMOItemsHook`：使用 `TYPE;;ID` 的复合 ID 格式

这几种实现基本体现了 UltimateShop 期望的挂钩写法。

