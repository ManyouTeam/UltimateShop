# 🔗兼容性

插件兼容性主要包括对**物品插件**和**经济插件**的对接。与同类插件不同，UltimateShop 可以通过**直接兼容**或**间接兼容**两种方式支持不同形式的物品和经济系统。

## 直接兼容

直接兼容指在 **ItemFormat** 或 **EconomyFormat** 中直接使用其他插件的物品或货币。这是最简单的兼容方式，并由官方提供支持。

### <mark style="color:red;">直接</mark>支持的物品插件

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

也可以使用 ItemBridge 作为自定义物品提供器，以支持更多物品插件，详见[这里](../format/itembridge.md)。

### <mark style="color:red;">直接</mark>支持的经济插件

* PlayerPoints
* CoinsEngine
* UltraEconomy
* EcoBits
* PEconomy
* RedisEconomy
* RoyaleEconomy
* VotingPlugin

以下示例在 **ItemFormat** 中直接获取 **ItemsAdder** 物品，并在 **EconomyFormat** 中使用 **Vault** 经济：

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

### <mark style="color:red;">直接</mark>支持的区域保护插件 <a href="#directly-supported-protection-plugins-list-premium" id="directly-supported-protection-plugins-list-premium"></a>

{% hint style="info" %}
即使保护插件不在此列表中，只要它能够阻止玩家与对应容器交互，UltimateShop 通常也不会触发出售棒。具体行为取决于该保护插件的实现方式。
{% endhint %}

对于以下保护插件，如果玩家无权在受保护区域打开容器，UltimateShop 可以阻止其在该区域使用出售棒。

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

### <mark style="color:red;">直接</mark>支持的全息显示插件 <a href="#directly-supported-protection-plugins-list-premium-1" id="directly-supported-protection-plugins-list-premium-1"></a>

可创建全息文本，用于展示出售箱信息。

* CMI
* DecentHolograms
* FancyHolograms

## 间接兼容

间接兼容指灵活组合插件已有功能，与其他插件实现联动。

* [保存物品](../features/saved-item-item-manager.md)：[命令](commands-and-permissions.md)页面介绍了 `/shop saveitem`。保存后，在[物品格式](../format/itemformat-tm/)的 `material` 中填写保存的物品 ID 即可使用。
* 购买动作：[商店](../shops/shops.md)中的 `buy-actions` 支持[动作](../format/action-format.md)系统，而动作支持执行命令。因此可以直接在购买动作中执行其他插件的物品发放命令。
* 给予动作：[单项内容](../shops/products-config-single-thing/)中的此功能与**购买动作**类似，该页面也提供了示例。

### 示例：将未直接支持的物品插件用于商品

先在展示物品选项中填写 **ItemFormat**，描述其他插件的物品外观，让玩家可以在菜单中看到它。

然后在 `products` 中使用[自定义出售匹配](../features/custom-item-match-method.md)，通过 `contains-lore` 等规则灵活识别待售物品。再使用 `give-actions` 执行物品发放命令，使玩家购买后获得对应物品。

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

如果服务器使用 Paper，且每次生成的物品完全相同，也可以使用保存物品功能：先执行 `/shop saveitem`，再把保存的物品 ID 填入 **ItemFormat** 的 `material` 中。

### 示例：将未直接支持的经济插件用于价格

此示例组合了不同的单项内容类型以及 `give-actions`、`take-actions`，相关功能见[商品](../shops/products.md)。购买时，价格中的 `match-placeholder` 用于判断玩家余额是否充足。满足 `buy-prices` 后，玩家获得 `products` 中的苹果，同时执行购买价格中的 `take-actions` 扣除货币。出售时，玩家获得出售价格所表示的奖励，其 `give-actions` 会执行，从而发放货币。

{% hint style="info" %}
需要 UltimateShop <mark style="color:red;">**付费版**</mark>！
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

## MythicChanger：扩展物品格式

此对接可在 [ItemFormat](../format/itemformat-tm/) 的基础上进一步修改物品，使其符合所需效果。

服务器必须安装 **MythicChanger**：

**免费版：** [点击下载](https://www.spigotmc.org/resources/mythicchanger-match-and-modify-all-your-items-without-trouble-1-14-1-21.98523/)

**付费版：** [点击下载](https://www.spigotmc.org/resources/mythicchanger-premium-match-and-modify-all-your-items-without-trouble-1-14-1-21.115913/)

`change-item` 的配置方法见 [MythicChanger Wiki](https://mythicchanger.superiormc.cn/)。注意：某些修改规则需要的是<mark style="color:red;">**MythicChanger 付费版，而非 UltimateShop 付费版！**</mark>

<pre class="language-yaml"><code class="lang-yaml"><strong>change-item:
</strong>  set-name: '&#x26;fGood Diamond Sword'
</code></pre>

## AdvancedEnchantments：扩展物品格式 <mark style="color:red;">- 付费版</mark>

此对接可在 [ItemFormat](../format/itemformat-tm/) 中为物品添加 AdvancedEnchantments 的自定义附魔。

`EcoEnchants, ExcellentEnchants` 等插件使用类似原版附魔的方式，直接将附魔 ID 填入 [ItemFormat](../format/itemformat-tm/#enchants) 的 `enchants` 即可。

对于插件附魔，可通过 `plugin-enchants` 添加。

```yaml
plugin-enchants:
  PLANTER: 5 # A AdvancedEnchantments enchantment
```

## NBTAPI：扩展物品格式 <mark style="color:red;">- 付费版</mark>

配置格式如下：

```yaml
nbt:
  <NBT Type>:
    <NBT Key>: <NBT Value>
```

支持的 NBT 类型：

* byte
* short
* int
* long
* float
* double
* string

例如：

```yaml
nbt:
  string: 
    customNBT: 'Hello!'
  int:
    anotherNBTComponent.theNBTKey: 5
```

## MythicChanger：自定义物品匹配

[自定义物品匹配方式](../features/custom-item-match-method.md)功能需要 MythicChanger。

## PlaceholderAPI：扩展占位符 <a href="#placeholderapi-extra-placeholders" id="placeholderapi-extra-placeholders"></a>

UltimateShop 向 PlaceholderAPI 提供了额外占位符，详见[此页面](../placeholders/built-in-placeholders.md#placeholderapi-support)。
