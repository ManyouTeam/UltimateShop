---
hidden: true
---

# 开发指南副本

{% hint style="info" %}
请注意：UltimateShop 不是传统的商店插件。它可以动态展示商品与价格（甚至精确到每一份的单价），而其他商店插件是一个 ItemStack 对应一个价格。
{% endhint %}

## 添加依赖 <a href="#user-content-get-shop-object" id="user-content-get-shop-object"></a>

{% hint style="info" %}
截至 2026 年 1 月 15 日，插件最新版本号为 **4.2.3**。如果这个日期已经过去很久，请自行确认最新版本号——此处给出的版本可能已过时或无法拉取。
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

## 获取商店对象 <a href="#user-content-get-shop-object" id="user-content-get-shop-object"></a>

```java
ConfigManager.configmanager.shopConfigs.get(shopID);
```

## 获取商品对象 <a href="#user-content-get-product-object" id="user-content-get-product-object"></a>

```java
ObjectShop shop = ConfigManager.configmanager.shopConfigs.get(shopID);
if (shop == null) {
  return;
}
ObjectItem item = shop.getProduct("TEST");
List<ObjectItem> items = shop.getProductList();
```

## 开始购买商品 <a href="#user-content-stat-buy-a-product" id="user-content-stat-buy-a-product"></a>

```java
BuyProductMethod.startBuy(Inventory inventory, String shop, String product, Player player, boolean quick, boolean test, int multi);
```

* `inventory`：Bukkit 的 Inventory 对象；玩家自己的物品栏请用 player.getInventory() 获取。
* `shop`：商店 ID。
* `product`：商品 ID。
* `quick`：购买后是否发送消息（如果在 config.yml 中开启了 send-message-after-buy 选项，则仍会发送）。
* `test`：是否仅做检测而不实际扣除钱或物品；如果只想确认玩家是否有足额的钱或物品，把它设为 true。
* `multi`：单次购买数量，默认为 1。

## 开始出售商品 <a href="#user-content-start-sell-a-product" id="user-content-start-sell-a-product"></a>

```java
SellProductMethod.startSell(Inventory inventory, String shop, String product, Player player, boolean quick, boolean test, boolean ableMaxSell, int multi);
```

* `ableMaxSell`：当玩家当前不足以提供 multi（数量）对应的钱或物品时，是否改为尝试取玩家能够出售的最大数量。用于“全部出售”命令。

## **获取玩家缓存对象**

<pre class="language-java"><code class="lang-java"><strong>CacheManager.cacheManager.playerCacheMap.get(player);
</strong></code></pre>

可以获取玩家的购买次数、出售次数等数据。

## **获取服务器缓存对象**

```java
CacheManager.cacheManager.serverCache;
```

## 从 ItemStack 获取价格

```java
ShopHelper.getBuyPrices(items, player, 1);
ShopHelper.getSellPrices(items, player, 1);
```

## 判断价格是否为 Vault

所有价格与商品配置都遵循 [EconomyFormat](../format/economyformat-tm.md) 或 [ItemFormat](../format/itemformat-tm/)。

```java
Map<AbstractSingleThing, BigDecimal> resultMap = takeResult.getResultMap();
for (AbstractSingleThing singleThing : resultMap.keySet()) {
   if (singleThing.getSingleSection().getString("economy-plugin", "").equals("Vault") {
       return "This price includes Vault";
   }
}
```

## 发放 GiveResult

{% hint style="info" %}
此方法不会遵循购买/出售限制、条件检查等逻辑。如果希望由插件判断玩家能否买入或卖出该物品，请使用 BuyProductMethod 或 SellProductMethod。
{% endhint %}

```java
int sellUseTimes = ShopHelper.getSellUseTimes(item, player);
GiveResult giveResult = ShopHelper.getSellPrices(items, player, 1);
giveResult.give(sellUseTimes, 1, player, 1.01);
```

## 扣除 TakeResult

{% hint style="info" %}
此方法不会遵循购买/出售限制、条件检查等逻辑。如果希望由插件判断玩家能否买入或卖出该物品，请使用 BuyProductMethod 或 SellProductMethod。
{% endhint %}

```java
int buyUseTimes = ShopHelper.getBuyUseTimes(item, player);
TakeResult takeResult = ShopHelper.getBuyPrices(items, player, 1);
if (!takeResult.getResultBoolean) return "Your money not enough";
takeResult.take(sellUseTimes, 1, player.getInventory(), player);
```

## 查询 TakeResult 对应的商品

```java
ObjectItem item = takeResult.getThings().getItem();
```
