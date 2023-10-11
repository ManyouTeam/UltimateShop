# UltimateShop

Welcome to use UltimateShop project, this is a shop plugin for Spigot.

## How to use
View Wiki: https://ultimateshop.superiormc.cn

## Download jar
Spigot Link: https://www.spigotmc.org/resources/ultimateshop-menus-limits-apply-settings-10-directly-hook-and-more-1-17-1-20.110601/

## Update Plan
- Sell All click action (DONE)
- /shop save command to make mainhand item as product (Use NeigeItems or MythicMobs instead)
- GUI Editor (WON'T ADDED IN NEAR FUTURE)
  I found it's too hard for me to add it becuase UltimateShop's config has much options and sections.
  For example, we have 15+ item or economy plugin hook, I need create 15 different type product create GUI for them.
  I have no time and effect for it.
- Dynamic Price based on players buy and sell (DONE)

## Develop
# Get shop object
```java
ConfigManager.configmanager.shopConfigs.get(shopID);
```
# Get product object
```java
ObjectShop shop = ConfigManager.configmanager.shopConfigs.get(shopID);
if (shop == null) {
  return;
}
ObjectItem item = shop.getProduct("TEST");
List<ObjectItem> items = shop.getProductList();
```

# Stat buy a product
```java
BuyProductMethod.startBuy(Inventory inventory,
                                               String shop,
                                               String product,
                                               Player player,
                                               boolean quick,
                                               boolean test,
                                               int multi);
```
