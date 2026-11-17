# 🛒ItemFormat™ 说明

ItemFormat 是插件提供的一种配置格式，用多行 YAML 文本就能生成**Minecraft 里的物品**。配置文件里某项需要或支持 **ItemFormat** 时，我们都会标注出来。常见例子是 `buy-prices`：在里面写上 ItemFormat，所描述的那个物品就成了这件商品的购买价格。

## 物品参数可选值列表

**ItemFormat** 里有些物品参数不能随便填，只能填游戏里真实存在的值。比如 `enchantments` 只能填游戏中已有的附魔 ID（包括部分自定义附魔插件和数据包新增的附魔）。下面按参数列出可选值。

每个参数都会给一个带 `<version>` 的链接，把它换成你服务器的 Minecraft 版本即可，例如 `1.21.3`、`1.20.4`、`1.20.6`。注意 Paper 并不会为每个 Minecraft 版本都保留对应的 API 文档，所以打开后可能跳到一个没有你要的信息的页面。不用担心，换个版本号再试一次就好。&#x20;

## 物品标记（Flag）

<pre class="language-html"><code class="lang-html"><strong>https://jd.papermc.io/paper/&#x3C;version>/org/bukkit/inventory/ItemFlag.html
</strong></code></pre>

## 唱片音乐（Sound）

```markup
https://jd.papermc.io/paper/<version>/org/bukkit/JukeboxSong.html
```

## 物品稀有度

```markup
https://jd.papermc.io/paper/<version>/org/bukkit/inventory/ItemRarity.html
```

## 附魔

1.20.5 之后改用 Minecraft 原版附魔 ID，不再沿用 Spigot API 里的旧版 ID；如果你在用 1.20.5+，请查阅 Minecraft Wiki。

```markup
https://jd.papermc.io/paper/<version>/org/bukkit/enchantments/Enchantment.html
```

## 属性 ID

<pre class="language-html"><code class="lang-html"><strong>https://jd.papermc.io/paper/&#x3C;version>/org/bukkit/attribute/Attribute.html
</strong></code></pre>

## 装备槽位（Slot）

```markup
https://jd.papermc.io/paper/<version>/org/bukkit/inventory/EquipmentSlot.html
```

## 旗帜图案（Banner Pattern）

```markup
https://jd.papermc.io/paper/<version>/org/bukkit/block/banner/PatternType.html
```

## 染料颜色（含旗帜图案颜色）

```markup
https://jd.papermc.io/paper/<version>/org/bukkit/DyeColor.html
```

## 盔甲纹饰材料（Trim Material）

```markup
https://jd.papermc.io/paper/<version>/org/bukkit/inventory/meta/trim/TrimMaterial.html
```

## 盔甲纹饰图案（Trim Pattern）

```markup
https://jd.papermc.io/paper/<version>/org/bukkit/inventory/meta/trim/TrimPattern.html
```

## 伤害类型标签（Damage Type Tag）

```markup
https://jd.papermc.io/paper/<version>/org/bukkit/tag/DamageTypeTags.html
```

## 实体类型

```markup
https://jd.papermc.io/paper/<version>/org/bukkit/entity/EntityType.html
```

## 材质 / 实体类型标签

```markup
https://jd.papermc.io/paper/<version>/org/bukkit/Tag.html
```

## 药水基础效果

### 1.20.5 及以后

`WATER, MUNDANE, THICK, AWKWARD, NIGHT_VISION, LONG_NIGHT_VISION, INVISIBILITY, LONG_INVISIBILITY, LEAPING, LONG_LEAPING, STRONG_LEAPING, FIRE_RESISTANCE, LONG_FIRE_RESISTANCE, SWIFTNESS, LONG_SWIFTNESS, STRONG_SWIFTNESS, SLOWNESS, LONG_SLOWNESS, STRONG_SLOWNESS, WATER_BREATHING, LONG_WATER_BREATHING, HEALING, STRONG_HEALING, HARMING, STRONG_HARMING, POISON, LONG_POISON, STRONG_POISON, REGENERATION, LONG_REGENERATION, STRONG_REGENERATION, STRENGTH, LONG_STRENGTH, STRONG_STRENGTH, WEAKNESS, LONG_WEAKNESS, LUCK, TURTLE_MASTER, LONG_TURTLE_MASTER, STRONG_TURTLE_MASTER, SLOW_FALLING, LONG_SLOW_FALLING, WIND_CHARGED, WEAVING, OOZING, INFESTED`.

### 1.20.5 之前

`AWKWARD, FIRE_RESISTANCE, INSTANT_DAMAGE, INSTANT_HEAL, INVISIBILITY, JUMP, LUCK, MUNDANE, NIGHT_VISION, POISON, REGEN, SLOW_FALLING, SLOWNESS, SPEED, STRENGTH, THICK, TURTLE_MASTER, UNCRAFTABLE, WATER, WATER_BREATHING, WEAKNESS`.

## 乐器（Music）

```markup
https://jd.papermc.io/paper/<version>/org/bukkit/MusicInstrument.html
```
