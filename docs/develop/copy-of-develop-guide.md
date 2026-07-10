---
hidden: true
---

# Copy of Develop Guide

{% hint style="info" %}
Please note that UltimateShop is not a traditional shop plugin. It can dynamically display products and prices (and even the each single price amount), unlike other shop plugins where one ItemStack corresponds to one price.
{% endhint %}

## Add as dependency <a href="#user-content-get-shop-object" id="user-content-get-shop-object"></a>

{% hint style="info" %}
As of January 15, 2026, the latest plugin version number is **4.2.3**. If this date is too far away, then you should check the latest plugin version number yourself, as the provided plugin version may be outdated or unavailable.
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

## Get shop object <a href="#user-content-get-shop-object" id="user-content-get-shop-object"></a>

```java
ConfigManager.configmanager.shopConfigs.get(shopID);
```

## Get product object <a href="#user-content-get-product-object" id="user-content-get-product-object"></a>

```java
ObjectShop shop = ConfigManager.configmanager.shopConfigs.get(shopID);
if (shop == null) {
  return;
}
ObjectItem item = shop.getProduct("TEST");
List<ObjectItem> items = shop.getProductList();
```

## Start buy a product <a href="#user-content-stat-buy-a-product" id="user-content-stat-buy-a-product"></a>

```java
BuyProductMethod.startBuy(Inventory inventory, String shop, String product, Player player, boolean quick, boolean test, int multi);
```

* `inventory` is Bukkit inventory object, for player's inventory, use player.getInventory() method.
* `shop` is shop ID.
* `product` is product ID.
* `quick` is whether send message after buy (will still send if you enable send-message-after-buy option in config.yml)
* `test` is whether take money or items from player, set it to true if you just want to know whether player has enough money or items.
* `multi` is buy amount in one time, default set to 1.

## Start sell a product <a href="#user-content-start-sell-a-product" id="user-content-start-sell-a-product"></a>

```java
SellProductMethod.startSell(Inventory inventory, String shop, String product, Player player, boolean quick, boolean test, boolean ableMaxSell, int multi);
```

* `ableMaxSell` is whether if player don't have enough money or items for now multi(amount) value, we will try to get max amount that player able to sell. Use for sell all command.

## **Get player cache object**

<pre class="language-java"><code class="lang-java"><strong>CacheManager.cacheManager.playerCacheMap.get(player);
</strong></code></pre>

Can get player's buy times, sell times data and so on.

## **Get server cache object**

```java
CacheManager.cacheManager.serverCache;
```

## Get price from ItemStack

```java
ShopHelper.getBuyPrices(items, player, 1);
ShopHelper.getSellPrices(items, player, 1);
```

## Get whether the price is Vault

All price/product configs follows [EconomyFormat](../format/economyformat-tm.md) or [ItemFormat](../format/itemformat-tm/).

```java
Map<AbstractSingleThing, BigDecimal> resultMap = takeResult.getResultMap();
for (AbstractSingleThing singleThing : resultMap.keySet()) {
   if (singleThing.getSingleSection().getString("economy-plugin", "").equals("Vault") {
       return "This price includes Vault";
   }
}
```

## Give GiveResult

{% hint style="info" %}
This will not follow buy/sell limits, conditions check and so on. If you want to plugin check out whether player can buy or sell this item, you need use BuyProductMethod or SellProductMethod.
{% endhint %}

```java
int sellUseTimes = ShopHelper.getSellUseTimes(item, player);
GiveResult giveResult = ShopHelper.getSellPrices(items, player, 1);
giveResult.give(sellUseTimes, 1, player, 1.01);
```

## Take TakeResult

{% hint style="info" %}
This will not follow buy/sell limits, conditions check and so on. If you want to plugin check out whether player can buy or sell this item, you need use BuyProductMethod or SellProductMethod.
{% endhint %}

```java
int buyUseTimes = ShopHelper.getBuyUseTimes(item, player);
TakeResult takeResult = ShopHelper.getBuyPrices(items, player, 1);
if (!takeResult.getResultBoolean) return "Your money not enough";
takeResult.take(sellUseTimes, 1, player.getInventory(), player);
```

## Get TakeResult from which product

```java
ObjectItem item = takeResult.getThings().getItem();
```
