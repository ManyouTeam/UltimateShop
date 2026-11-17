# 📝ItemFormat™

{% hint style="info" %}
Mojang 很爱改动物品类型（material）、附魔、药水等大量内容的 ID（命名空间键）。例如在 1.21 中，横扫之刃（Sweeping Edge）的 ID 被改成了 `sweeping_edge`。所以在升级服务端版本前，务必先确认新版本是否改动了相关 ID，否则你的商店或菜单配置会直接失效。
{% endhint %}

{% hint style="info" %}
手持一件物品，执行 `/shop generateitemformat` 即可解析出它的 ItemFormat。执行后会在 `plugins/UltimateShop` 目录下生成 `generated-item-format.yml`（若为其它衍生版本，则为 `plugins/<你使用的插件名>`）。
{% endhint %}

## 挂钩物品（Hook Item）

本节介绍内置的自定义物品提供器。你也可以改用 ItemBridge 作为自定义物品提供器，它支持的自定义物品插件更多，详见[此处](../itembridge.md)。

### 挂钩插件（hook-plugin）

可能值：`EcoItems, EcoArmor, MMOItems, ItemsAdder, Oraxen, MythicMobs, eco, NeigeItems, ExecutableItems, Nexo, CraftEngine`.&#x20;

```yaml
hook-plugin: MMOItems
```

### hook-item 的写法

`EcoItems`、`Oraxen`、`MythicMobs`、`Nexo` 填“物品 ID”。

`ItemsAdder`、`eco` 填 `命名空间:物品 ID`。&#x20;

* `eco` 的命名空间就是插件名，例如 `talismans`；物品 ID 遵循下列规则：&#x20;
  * Talisman：直接填护身符 ID。
  * EcoArmor:
    * 盔甲：`set_<setID>_<slotID>`&#x20;
    * 进阶盔甲：`set_<setID>_<slotID>_advanced`&#x20;
    * 升级水晶：`shard_<shardID>`
  * Reforges: `stone_<reforgeID>`

`EcoArmor` 填 `盔甲套 ID;;装备槽位`，槽位可取 **BOOTS、CHESTPLATE、ELYTRA、HELMET、LEGGINGS**。

`MMOItems` 填 `类型 ID;;物品 ID`。

```yaml
hook-item: AXE;;TEST_AXE
```

`CraftEngine` 填 `命名空间;;物品 ID`。

{% hint style="info" %}
**先停一下！** 如果你只是要从自定义物品插件里取物品，光靠 `hook-plugin` 和 `hook-item` 就够了。若你的自定义物品插件没有被支持，请改用对应命令。下面这些选项都是给原版物品用的：在这里重复设置会覆盖物品相关属性，结果得到的物品就和自定义物品插件里的那个不一样了。
{% endhint %}

## 材质（material）

想使用插件保存过的物品时，把该选项设为保存物品的 ID 即可，例如 `material: superior_sword`。

值为空或非法时，默认使用石头。

**已经配了 `hook-plugin` 就不要再写这个选项，写了也不会生效！**

```yaml
material: APPLE
```

## 数量（amount）

支持 PlaceholderAPI 占位符与数学计算，例如 `%player_health% * 5`。

```yaml
amount: 5
```

## min-amount / max-amount（仅价格与商品配置可用）

当 `amount` 使用动态值时，可用 `min-amount` 与 `max-amount` 限定取值上下限，动态价格场景尤为实用。

```yaml
min-amount: 1
max-amount: 15
```

## 组件（1.21.5+，需要 Paper） <mark style="color:red;">- 付费版</mark>

`component` 段通过 Paper 的 DataComponent API 设置物品属性，需要 Paper 1.21.5+ 与 UltimateShop 付费版。越新的组件要求越新的服务端版本，版本对照表与支持的键见[组件格式](component-format.md)。

`material` 与 `amount` 仍写在 ItemFormat 根层级；组件值会在根级选项之后应用：

```yaml
material: DIAMOND_SWORD
amount: 1

component:
  name: '<aqua>Component sword'
  lore:
    - '<gray>Built with Paper components'
  custom-model-data:
    float:
      - 101.0
    flag:
      - true
  glow: true
  damage: 25
  max-damage: 2048
```

`config.yml` 中的 `debuild-item-method` 可取 `LEGACY` 或 `COMPONENT`，决定 `/shop generateitemformat` 与 `/shop saveitem <ID> itemformat` 以何种形式保存物品。设为 `COMPONENT` 时，在 Paper 1.21.5+ 上受支持的属性会存入 `component` 段。若物品本身已带该段，构建时无需这个设置。

各版本的完整示例以及保存/重建校验，见[测试 Paper 组件](component-testing.md)。

## 显示名称（name / display-name）

按你的配置文件不同，有两种写法：1.9 之前使用的传统颜色代码，或较新版本使用的文本组件（Text Component）。前者用本插件自创的颜色代码格式，后者用 MiniMessage 格式，详见[此处](https://docs.advntr.dev/minimessage/format.html)。MiniMessage 格式要求服务端为 Paper。

```yaml
name: '&fA smart sword'
```

## 物品名称 (1.20.5+)

物品名称（item name）是 1.20.5 新增的属性，与显示名称不同，它无法在铁砧中修改。

按你的配置文件不同，有两种写法：1.9 之前使用的传统颜色代码，或较新版本使用的文本组件（Text Component）。前者用本插件自创的颜色代码格式，后者用 MiniMessage 格式，详见[此处](https://docs.advntr.dev/minimessage/format.html)。MiniMessage 格式要求服务端为 Paper。

```yaml
item-name: '&bRare Sword'
```

## 物品描述（lore）

可以用 `\n` 表示换行。

按你的配置文件不同，有两种写法：1.9 之前使用的传统颜色代码，或较新版本使用的文本组件（Text Component）。前者用本插件自创的颜色代码格式，后者用 MiniMessage 格式，详见[此处](https://docs.advntr.dev/minimessage/format.html)。MiniMessage 格式要求服务端为 Paper。

```yaml
lore:
  - '&fLine 1'
  - '&fLine 2'
```

## 最大堆叠数（max-stack，1.20.5+）

```yaml
max-stack: 99
```

## 食物（food，1.20.5+）

效果格式：药水类型 ID、持续时间、等级、是否环境效果、是否显示粒子、是否显示图标、触发概率。

其中 `Ambient`、`Particles`、`Icon` 为布尔值，`Chance` 为 0 到 1 之间的数字。

`convert` 是一个 ItemFormat，表示吃完后返还给玩家的物品（需 1.21+，可选项）。

```yaml
food:
  eat-seconds: 0.25 # Removed in 1.21.2
  can-always-eat: true 
  nutrition: 5
  saturation: 0.5
  convert: # Removed in 1.21.2
    material: BREAD
  effects: # Removed in 1.21.2
    - 'SPEED, 100, 1, true, true, false, 0.5'
```

## 工具 (1.21+)

```yaml
tool:
  mining-speed: 1
  damage-per-block: 1
  rules:
    # Blocks, Speed, Drop after mine
    - 'stone, coal_ore, 1, true'
    # Support replace Blocks to Tag.
```

## 可放入唱片机（song，1.21+）

```yaml
song: CAT
show-song: false
```

## 抗火（fire-resistant，1.20.5+）

```yaml
fire-resistant: true
```

## 隐藏提示框（hide-tool-tip，1.20.5+）

```yaml
hide-tool-tip: true
```

## 无法破坏（unbreakable）

```yaml
unbreakable: true
```

## 稀有度（rarity，1.20.5+）

```yaml
rarity: COMMON
```

## 物品标记（flags）

```yaml
flags:
  - HIDE_ENCHANTS
  - HIDE_ATTRIBUTES
  - HIDE_UNBREAKABLE
  - HIDE_DESTROYS
  - HIDE_PLACED_ON
  - HIDE_ADDITIONAL_TOOLTIP
  - HIDE_DYE
  - HIDE_ARMOR_TRIM
```

<a id="enchants"></a>

## 附魔

配置格式为 `附魔 ID: 等级`。

附魔书要用 `stored-enchants` 而不是 `enchants`。

自定义附魔：部分附魔插件不会把附魔注册进游戏，这种情况无法生效。

**1.20.5 之后请使用 Minecraft 的附魔 ID，而不是 Spigot 的。**

```yaml
enchants:
  MENDING: 1
```

## 发光（glow，1.20.5+）

```yaml
glow: true
```

## 自定义模型数据（custom-model-data）

```yaml
custom-model-data: 15
```

## 属性（attributes）

注意：1.20.5 改了属性格式。若物品是在 1.20.5 之前生成的，需要按新格式重新配置。

列表里每一项是一个属性修饰符，`type` 为属性 ID。条目按列表顺序应用，同一属性类型可以出现多次。

```yaml
attributes:
  - type: MAX_HEALTH
    name: generic.attack_damage 
    amount: 12
    operation: ADD_NUMBER # ADD_NUMBER, ADD_SCALAR, MULTIPLY_SCALAR_1
    slot: HAND 
```

## 可损耗物品（damageable）

表示带耐久、可承受损伤的物品。

### 损伤值（damage）

```yaml
damage: 5
```

### 最大伤害 (1.20.5+)

相当于最大耐久度。

```yaml
max-damage: 1500
```

## 附魔存储（enchantment storage）

该属性专用于“存储”附魔的物品，而不是自身被附魔。[`Material.ENCHANTED_BOOK`](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html#ENCHANTED_BOOK) 就是带附魔存储的物品示例。

### 存储的附魔（stored-enchants）

与[附魔](./#enchants)类似。

```yaml
stored-enchants:
  MENDING: 1
```

## 旗帜（banner）

### 花纹（pattern）

图案按列表顺序应用，同一图案类型可以出现多次。

```yaml
patterns:
  - 'RHOMBUS: CYAN'
  - 'BORDER: LIGHT_GRAY'
  - 'BORDER: BLACK'
```

## 药水（potion）

表示药水，或可附带自定义效果的物品。

### 基础效果（base-effect，1.20.5+）

```yaml
base-effect: 'WATER'
```

### 基础效果（1.20.5之前）

格式：药水类型 ID、是否延长、是否强化。

`Extended` 与 `Upgraded` 均为布尔值。

```yaml
base-effect: 'WATER, true, false'
```

### 效果 / 自定义效果（effects）

效果格式：`药水类型 ID, 持续时间, 等级, 环境效果, 粒子, 图标`。

`Ambient`、`Particles`、`Icon` 为布尔值。

<pre class="language-yaml"><code class="lang-yaml"><strong>effects:
</strong>  - 'SPEED, 100, 1, true, true, false'
</code></pre>

### 颜色

颜色有两种写法：

* 红、绿、蓝三个数值
* 一个整数（如 `0xff0000`）

```yaml
color: '5'
```

## 盔甲（armor）

表示实体可穿戴的盔甲。

### 盔甲纹饰（trim，1.20+）

该配置段包含两个子选项。

```yaml
trim:
  material: IRON
  pattern: TIDE
```

## 皮革盔甲（leather armor）

### 颜色

颜色有两种写法：

* 红、绿、蓝三个数值
* 一个整数（如 `0xff0000`）

```yaml
color: '0xff0000'
```

## 美西螈桶（axolotl bucket，1.17+）

### 颜色

可能值：`LUCY, WILD, GOLD, CYAN, BLUE`。

```yaml
color: LUCY
```

## 热带鱼桶（tropical fish bucket，1.14+）

### 颜色

可能值：`WHITE, ORANGE, MAGENTA, LIGHT_BLUE, YELLOW, LIME, PINK, GRAY, LIGHT_GRAY, CYAN, PURPLE, BLUE, BROWN, GREEN, RED, BLACK`。

```yaml
color: WHITE
```

### 花纹颜色（pattern-color）

与上面的 `color` 写法相同。

```yaml
pattern-color: WHITE
```

### 花纹（pattern）

可能值：`KOB, SUNSTREAK, SNOOPER, DASHER, BRINELY, SPOTTY, FLOPPER, STRIPEY, GLITTER, BLOCKFISH, BETTY, CLAYFISH`。

```yaml
pattern: KOB
```

## 玩家头颅（skull）&#x20;

支持两种写法：

* Base64：如下例，仅支持 1.19+。
* 玩家名称：需要正版（online-mode）验证，支持所有版本。<mark style="color:red;">UltimateShop 付费版可在此使用 PlaceholderAPI 占位符。</mark>

```yaml
skull: eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2YzZmVkMTZmZDU1MTkwOWZhNWUyOWNkZDY5N2VlMzQ2ZTYzMzkwYjM4M2E0MzAwYTY2MmE4MGI2NGQ5ZWIxNyJ9fX0=
```

## 烟花火箭（firework）

### 飞行时间（power）&#x20;

```yaml
power: 1
```

### 烟花效果（firework）

该配置段包含多个子选项。

`type` 可选值：`BALL、BALL_LARGE、STAR、BURST、CREEPER`。

```yaml
firework:
  1: 
    flicker: true
    trail: true
    colors:
      base:
        - 5
      fade:
        - 666666
    type: BALL
```

## 烟花之星（firework star）

### 烟花效果（firework）

`type` 可选值：`BALL、BALL_LARGE、STAR、BURST、CREEPER`。

```yaml
firework:
  flicker: true
  trail: true
  colors:
    base:
      - 5
    fade:
      - 666666
  type: BALL
```

## 迷之炖菜（suspicious stew，1.14+）

### 效果

效果格式：`药水类型 ID, 持续时间, 等级, 环境效果, 粒子, 图标`。

```yaml
effects:
  - 'SPEED, 100, 1, true, true, false'
```

## 收纳袋（1.17+）/ 潜影盒（shulker）

### 内容（contents）

配置格式为“槽位 ID: 物品格式”。

```yaml
contents:
  1:
    material: STONE
  2:
    material: APPLE
    amount: 5
```

## 可刷扫方块（brushable blocks，1.20+）

### 藏物（content）

可刷扫方块中藏着的战利品物品。想要自定义可刷扫方块？可以看看我们的 CustomArcheology 插件，详见[此处](https://www.spigotmc.org/resources/customarcheology-become-an-archaeologist-in-the-game-old-chunk-support-1-20.114142/)。

```yaml
content:
  material: APPLE
```

## 刷怪笼（spawner）

想让刷怪笼里有怪，只写 `spawner` 一个选项就够，其余选项都是可选的。

```yaml
spawner: ZOMBIE
min-delay: 200
max-delay: 800
max-entities: 6
player-range: 16
spawn-range: 30
```

## 不祥之瓶（ominous bottle，1.20.5+）

### 持续时间等级（power）

```yaml
power: 3
```

## 乐器（music instrument，1.18+）

### 曲目（music）

```yaml
music: PONDER_GOAT_HORN # Music
```

## 可修复（repairable）

```yaml
repair-cost: 15
```

## 可附魔（enchantable，1.21.2+）

```yaml
enchantable: 15
```

## 滑翔（glider，1.21.2+）

```yaml
glider: true
```

## 物品模型（item-model，1.21.2+）

```yaml
item-model: 'mycustom:itemmodel'
```

## 提示框样式（tooltip-style，1.21.2+）

```yaml
tooltip-style: 'mycustom:tooltip'
```

## 使用冷却（use-cooldown，1.21.2+）

```yaml
use-cooldown:
  cooldown-group: 'minecraft:custom_weapon'
  cooldown-seconds: 1.5
```

## 可装备 (1.21.2+) <mark style="color:red;">- 付费版</mark>

```yaml
equippable:
  entities: 
    - 'zombie' # Entity Type ID
    - 'skeletons' # Entity Type Tag
  dispensable: true
  swappable: true
  damage-on-hurt: true
  camera-overlay: misc/pumpkinblur
  sound: ambient.basalt_deltas.mood
  model: mycustomarmor
  slot: head
```

## 伤害免疫（damage-resistant）

```yaml
damage-resistant: is_fall # Damage Type Tag
```

## 额外的 ItemFormat 选项

另有一些 **ItemFormat** 选项列在[兼容性](../../info/compatibility.md)页面。它们用于与其它基于 ItemFormat 的插件对接，让这些插件也能参与物品生成。

目前支持以下插件：

* MythicChanger
* AdvancedEnchantments <mark style="color:red;">- 付费版</mark>
* NBTAPI - <mark style="color:red;">付费版</mark>
