# 🌉ItemBridge

This plugin supports use [ItemBridge by jhqwqmc](https://github.com/jhqwqmc/ItemBridge) as custom item provider which supports more custom item plugins than us.

## Supported Plugin List

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

## Use ItemBridge as custom item provider

Find `hook-item-method` option in `config.yml` file and set it to `ITEMBRIDGE`. Then, restart your server.

```yaml
# Support value: DEFAULT or ITEMBRIDGE
hook-item-method: 'DEFAULT'
```

And then, use `hook-plugin` and `hook-item` option in ItemFormat to obtain the item you want from other plugins.

{% hint style="warning" %}
Please note that built-in default custom item provider in ItemFormat has different `hook-item` format than ItemBridge. For example:<br>

In built-in custom item provider, CraftEngine hook use this `hook-item` format (namespace;;id):

```yaml
hook-plugin: 'CraftEngine'
hook-item: 'myemoji;;happy'
```

\
\
In ItemBridge custom item provider, CraftEngine hook use this `hook-item` format (namespace:id):

```yaml
hook-plugin: 'CraftEngine'
hook-item: 'myemoji:happy'
```
{% endhint %}

If you are unsure how to fill in the values for these two options, you can hold the corresponding item then use command `/shop generateitemformat` to parse the ItemFormat of it. After execute this command, a new file called `generated-item-format.yml` wll be generated at `plugins/UltimateShop` folder. (or `plugins/<pluginNameYouAreUsing>`)
