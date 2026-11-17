# 🌉ItemBridge

本插件支持把 [jhqwqmc 的 ItemBridge](https://github.com/jhqwqmc/ItemBridge) 作为自定义物品提供者，它能支持的自定义物品插件比我们多。

## 支持的插件列表

* [AzureFlow](https://www.minebbs.com/resources/9673)
* [Baikiruto](https://github.com/YsGqHY/Baikiruto)
* [CraftEngine](https://github.com/Xiao-MoMi/craft-engine)
* [CustomFishing](https://github.com/Xiao-MoMi/Custom-Fishing)
* [DragonArmourers](https://archives.mcbbs.co/read.php?tid=951699)
* [EcoArmor](https://github.com/Auxilor/EcoArmor)
* [EcoCrates](https://github.com/Auxilor/EcoCrates)
* [EcoItems](https://github.com/Auxilor/EcoItems)
* [EcoMobs](https://github.com/Auxilor/EcoMobs)
* [EcoPets](https://github.com/Auxilor/EcoPets)
* [EcoScrolls](https://github.com/Auxilor/EcoScrolls)
* [ExecutableItems](https://modrinth.com/plugin/executableitems)
* [HeadDatabase](https://www.spigotmc.org/resources/14280)
* [HMCCosmetics](https://github.com/HibiscusMC/HMCCosmetics)
* [ItemsAdder](https://www.spigotmc.org/resources/73355)
* [MagicGem](https://liyi2015.gitbook.io/magicgem/)
* [MMOItems](https://gitlab.com/phoenix-dvpmt/mmoitems)
* [MythicMobs](https://mythiccraft.io/index.php?resources/1)
* [NeigeItems](https://github.com/ankhorg/NeigeItems-Kotlin)
* [Nexo](https://polymart.org/product/6901)
* [Nova](http://github.com/xenondevs/Nova)
* [Oraxen](http://github.com/oraxen/oraxen)
* [PxRpg](https://www.pxpmc.com/a/pxrpgfree.html)
* [Ratziel](https://github.com/TheFloodDragon/Ratziel-Beta)
* [Reforges](https://github.com/Auxilor/Reforges)
* [Sertraline](https://github.com/zzzyyylllty/Sertraline-Hydrochloride)
* [Slimefun](https://github.com/Slimefun/Slimefun4)
* [StatTrackers](https://github.com/Auxilor/StatTrackers)
* [SX-Item](https://github.com/Saukiya/SX-Item)
* [Talismans](https://github.com/Auxilor/Talismans)
* [Zaphkiel](https://github.com/TabooLib/zaphkiel)

## 用 ItemBridge 作为自定义物品提供者

在 `config.yml` 中找到 `hook-item-method`，设为 `ITEMBRIDGE`，然后重启服务器。

```yaml
# Support value: DEFAULT or ITEMBRIDGE
hook-item-method: 'DEFAULT'
```

之后在 ItemFormat 里用 `hook-plugin` 和 `hook-item` 取得其他插件中的物品。

{% hint style="warning" %}
注意：ItemFormat 内置的默认提供者与 ItemBridge 的 `hook-item` 写法并不相同。例如：<br>

内置提供者下，CraftEngine 的写法是（命名空间;;id）：

```yaml
hook-plugin: 'CraftEngine'
hook-item: 'myemoji;;happy'
```

\
\
ItemBridge 提供者下，CraftEngine 的写法是（命名空间:id）：

```yaml
hook-plugin: 'CraftEngine'
hook-item: 'myemoji:happy'
```
{% endhint %}

不确定这两个选项该填什么时，可以手持该物品执行 `/shop generateitemformat`，让插件解析出它的 ItemFormat。执行后会在 `plugins/UltimateShop` 下生成 `generated-item-format.yml`（若你用的是别的插件，则在 `plugins/<你使用的插件名>`）。
