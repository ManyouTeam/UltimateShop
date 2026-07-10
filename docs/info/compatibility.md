# 🔗Compatibility

The compatibility of plugins mainly includes hooks for **item plugins** and **economy plugins**. Unlike other plugins of the same type, we can support them regardless of their appearance. There are two methods to support them: **direct compatibility** and **indirect compatibility**.

## **Direct compatibility**

Direct compatibility refers to the use of item plugins or the economy of economic plugins directly in **ItemFormat** or **EconomyFormat**. This compatibility method is the simplest and officially supported.

### <mark style="color:red;">Directly</mark> supported item plugins list

* ItemsAdder
* Oraxen
* EcoItems
* EcoArmor
* MMOItems
* MythicMobs
* eco
* NeigeItems
* ExecutableItems
* Nexo
* CraftEngine

You can use ItemBridge as custom item provider which supports more custom item plugins, click [here](../format/itembridge.md) to know more.

### <mark style="color:red;">Directly</mark> supported economy plugins list

* PlayerPoints
* CoinsEngine
* UltraEconomy
* EcoBits
* PEconomy
* RedisEconomy
* RoyaleEconomy
* VotingPlugin

The following provides an example of directly obtaining items from the **ItemsAdder** plugin through the direct compatibility feature in **ItemFormat** and using economy from **Vault** plugin in **EconomyFormat**:

```yaml
items:
  A:
    price-mode: CLASSIC_ALL
    product-mode: CLASSIC_ALL
    products:
      1: 
        hook-plugin: ItemsAdder # Item Format
        hook-item: fishing_pack:common_fishing_bait # Item Forma
    buy-prices:
      1:
        economy-plugin: Vault # Economy Format
        amount: 5
        start-apply: 0
        placeholder: '&65 Coins'
```

### <mark style="color:red;">Directly</mark> supported protection plugins list  <a href="#directly-supported-protection-plugins-list-premium" id="directly-supported-protection-plugins-list-premium"></a>

{% hint style="info" %}
Although the protection plugin you are using is not on this list, as long as the corresponding plugin can prevent the player from interacting with the corresponding container, UltimateShop will not trigger a sell stick, depending on how the author of the plugin you are using wrote their plugin.
{% endhint %}

If players do not have permission to open container within these protection plugins areas, UltimateShop can prevent players use sell stick in these areas.

* BentoBox
* Dominion
* GriefPrevention
* HuskTowns
* HuskClaims
* Lands
* PlotSquared
* Residence
* Towny
* WorldGuard
* SuperiorSkyblock2

### <mark style="color:red;">Directly</mark> supported hologram plugins list  <a href="#directly-supported-protection-plugins-list-premium" id="directly-supported-protection-plugins-list-premium"></a>

Support create hologram to display sell chest info.

* CMI
* DecentHolograms
* FancyHolograms

## **Indirect compatibility**

Indirect compatibility refers to the flexible use of various features of plugins to enable them to associate with these plugins.

* [Save Item](../features/saved-item-item-manager.md): We told you a command called `/shop saveitem` at [Commands](commands-and-permissions.md) page, we also told you can set `material` option in [Item Format](../format/itemformat-tm/)  to the save item ID you was set to use them.
* Buy Actions: We told you a option called `buy-actions` in shop configs at [Shops](../shops/shops.md) page. In [Actions](../format/action-format.md) page, we also told you we support use command in actions, so just use the give item command here, all is done.
* Give Actions: We told you this feature at [Single Things](../shops/products-config-single-thing/) page which is very similar to **Buy Actions**. What's more, we even give you an example at that page.

### Example: Use for not supported item plugins as products

In this example, we first fill in the **ItemFormat** through the display item option to describe the item from an incompatible plugin, so that players can see what the item looks like in the menu.

In the `products` option, we use the [Custom Sell Match](../features/custom-item-match-method.md) feature, which allows us to flexibly set the rules for selling matches for this item, such as `contains-lore`, etc. Then, we use `give-actions` format to execute the item give command, so that player can obtain this item after buy.

```yaml
    display-item:
      material: APPLE
      # You can hold the item and type command /shop generateitemformat to get the ItemFormat here.
    products:
      1:
        # Sell Match
        match-item:
          contains-lore:
            - 'test1'
        # Buy Give Command
        give-actions:
          1:
            multi-once: true
            type: console_command
            command: 'items give {player} {amount}'
          2:
            type: message
            message: 'test message'
        amount: 64
```

If you use the Paper server and the item is fixed (the items generated each time are identical), you can use the Save item function: you only need to use the `/shop saveitem` command, then use the `material` option in **ItemFormat**, and fill in the ID of the save item in this option.

### Example: Use for not supported economy plugins as prices.

In this example, we mainly flexibly implemented different types of single thing and `give-actions` and `take-actions` options, whose functions can be found on the [Products](../shops/products.md) page. Specifically, assuming the player purchases this product, the `match-placeholder` in buy options is used to determine if the player has enough economy. If it meets the `buy-prices` requirement, the player will receive an apple in `products` section and execute `take-actions` section in buy-prices section. Similarly, during selling, as the player obtains the sell price, the `give-actions` in the sell price will be executed, and therefore the player will receive economy.

{% hint style="info" %}
Require <mark style="color:red;">**PREMIUM**</mark> version of UltimateShop!
{% endhint %}

```yaml
    products:
      1:
        # The product
        material: APPLE
        amount: 64
    buy-prices:
      1:
        # Buy Match Placeholder
        match-placeholder: '%economy_now_balance_placeholder%'
        amount: 500
        # Buy Take Actions
        take-actions:
          1:
            multi-once: true
            type: console_command
            command: 'eco take {player} {amount}'
    sell-prices:
      1:
        # Sell Give Actions
        give-actions:
          1:
            multi-once: true
            type: 'console_command'
            command: 'eco give {player} {amount}'
        amount: 500
```

## MythicChanger: Extra Item Format option

Through this hook, based on the [ItemFormat](../format/itemformat-tm/), items can be further modified to their desired appearance.

This feature require your server must install **MythicChanger** plugin, please get it here:

**FREE:** [Click to download](https://www.spigotmc.org/resources/mythicchanger-match-and-modify-all-your-items-without-trouble-1-14-1-21.98523/)

**PREMIUM:** [Click to download](https://www.spigotmc.org/resources/mythicchanger-premium-match-and-modify-all-your-items-without-trouble-1-14-1-21.115913/)

For how to configure the `change-item` section, please read MythicChanger's wiki, [click here](https://mythicchanger.superiormc.cn/) to visit. Please note that some of the change rules require <mark style="color:red;">**PREMIUM version of MythicChanger, not PREMIUM version of UltimateShop**</mark><mark style="color:red;">!</mark>

<pre class="language-yaml"><code class="lang-yaml"><strong>change-item:
</strong>  set-name: '&#x26;fGood Diamond Sword'
</code></pre>

## AdvancedEnchantments: Extra Item Format option <mark style="color:red;">- Premium</mark>

Through this hook, based on the [ItemFormat](../format/itemformat-tm/), items can have custom enchantments from AdvancedmentEnchantments.

Plugin like `EcoEnchants, ExcellentEnchants` are vanilla enchants like plugin, you just need to put their enchantment ID to `enchants` option in [ItemFormat](../format/itemformat-tm/#enchants).

You can use `plugin-enchants` option to add plugin enchants for your item.

```yaml
plugin-enchants:
  PLANTER: 5 # A AdvancedEnchantments enchantment
```

## NBTAPI: Extra Item Format option <mark style="color:red;">- Premium</mark>

The format of this option is:

```yaml
nbt:
  <NBT Type>:
    <NBT Key>: <NBT Value>
```

Supported NBT Type: '

* byte
* short
* int
* long
* float
* double
* string

For example:

```yaml
nbt:
  string: 
    customNBT: 'Hello!'
  int:
    anotherNBTComponent.theNBTKey: 5
```

## MythicChanger: Custom Item Match

[Custom Item Match Method](../features/custom-item-match-method.md) feature requires MythicChanger.

## PlaceholderAPI: Extra placeholders <a href="#placeholderapi-extra-placeholders" id="placeholderapi-extra-placeholders"></a>

UltimateShop provides those new placeholders to PlaceholderAPI, for more info, please view [this page](../placeholders/built-in-placeholders.md#placeholderapi-support).
