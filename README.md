# UltimateShop

Welcome to use UltimateShop project, this is a shop plugin for Spigot.

Consider respect my work and buy the plugin here, you can get free support, subbmit suggestion service. [Click to buy](https://www.spigotmc.org/resources/ultimateshop-premium-menu-dynamic-price-limits-apply-settings-sell-all-and-more-1-17-1-20.113069/)

You can also get free version here. [Click to download](https://www.spigotmc.org/resources/ultimateshop-menus-limits-apply-settings-10-directly-hook-and-more-1-17-1-20.110601/)

I am the copyright owner of this project, and the prerequisite for granting you a GPL v3 free license is that you have followed the following actions:
- Anyone is free to share a complete copy of this plugin, provided that you do not share it in any inappropriate places, such as pirated websites, leaked websites, etc. and/or their contact group, like Discord, QQ, Telegram, unless such websites are not profitable. As long as the website has advertising, paid membership, and other content, I consider it profitable.
- The author of UltimateShop is PQguanfang. When distributing copies, you must prominently display **THIS** copyright information: The author of UltimateShop is PQguanfang, and I am not the author of this plugin. However, all issues with this distribution version need to be addressed to me, and the original author is not responsible for the distribution version.

对于国内某些践踏他人劳动果实，不尊重作者辛苦付出的某些人，特地中文标识：
我是该项目的版权所有者，我向您赋予GPL v3自由许可证的前提是，您遵循了如下行为：
- 任何人都可以自由的分享此插件的完整副本，但前提是您不能分享到我认为不合时宜的地方，例如盗版网站、泄露网站等，以及/或者它们的联系群 Discord，QQ，Telegram，除非这种网站不带有盈利性质，只要网站有广告、付费会员等内容，我就认为它是盈利性质的。
- UltimateShop 的作者是PQguanfang，您在分发副本时，必须在显著位置提示 **此** 版权信息: UltimateShop 的作者是PQguanfang，我不是该插件作者，但此分发版本的一切问题都需要找我联系，原作者对于分发版本不负任何责任。

## Update Plan
- Sell All click action (DONE)
- /shop save command to make mainhand item as product (DONE)
- GUI Editor (WIP)
  I found it's too hard for me to add it becuase UltimateShop's config has much options and sections.
  For example, we have 15+ item or economy plugin hook, I need create 15 different type product create GUI for them.
  I have no time and effect for it.
- Dynamic Price based on players buy and sell (DONE)
- Rotate Shop (DONE)
- BungeeCord Sync (DONE)
- 2.0.0 RELEASE: After finish GUI Editor, we will publish v2 version, this version won't have any big change, we just make everyone know UltimateShop has finished all planned work and now should be stable to use.
## Develop
### Get shop object
```java
ConfigManager.configmanager.shopConfigs.get(shopID);
```
### Get product object
```java
ObjectShop shop = ConfigManager.configmanager.shopConfigs.get(shopID);
if (shop == null) {
  return;
}
ObjectItem item = shop.getProduct("TEST");
List<ObjectItem> items = shop.getProductList();
```

### Start buy a product
```java
BuyProductMethod.startBuy(Inventory inventory, String shop, String product, Player player, boolean quick, boolean test, int multi);
```
- inventory is Bukkit inventory object, for player's inventory, use player.getInventory() method.
- shop is shop ID.
- product is product ID.
- quick is whether send message after buy (will still send if you enable send-message-after-buy option in config.yml)
- test is whether take money or items from player, set it to true if you just want to know whether player has enough money or items.
- multi is buy amount in one time, default set to 1.

### Start sell a product
```java
SellProductMethod.startSell(Inventory inventory, String shop, String product, Player player, boolean quick, boolean test, boolean ableMaxSell, int multi);
```
- ableMaxSell is whether if player don't have enough money or items for now multi(amount) value, we will try to get max amount that player able to sell. Use for sell all command.
