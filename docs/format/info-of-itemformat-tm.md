# 🛒Info of ItemFormat™

ItemFormat is a configuration format provided by plugins that can generate **items in Minecraft games** through multi line YAML formatted text. If an option in a configuration file requires or supports **ItemFormat**, we will annotate it. A classic example is that you can fill in the ItemFormat in `buy-prices` options, so that the described item can be used as the buy price for the product.

## Item parameter possible value list

In **ItemFormat**, there are some item parameter values that are not arbitrarily filled in. You can only fill in the value of the item parameter that exists in the game. For example, in `enchantments`, you can only fill in the ID of the enchantment that exists in the game (including newly added enchantments in some custom enchantment plugins and data packs). The following provides possible values for various item parameters.

Each item parameters we will give you a link with `<version>` arg. Which you need is replace the `<version>` to your server's Minecraft version, like `1.21.3`, `1.20.4`, `1.20.6`. Please note that PaperAPI does not save each Minecraft version's Paper API, so you after open the link you are going to a strange site which not give you the information you need, don't worry, change the Minecraft version to another and try again.&#x20;

## Flag

<pre class="language-html"><code class="lang-html"><strong>https://jd.papermc.io/paper/&#x3C;version>/org/bukkit/inventory/ItemFlag.html
</strong></code></pre>

## Sound

```markup
https://jd.papermc.io/paper/<version>/org/bukkit/JukeboxSong.html
```

## Item Ratity

```markup
https://jd.papermc.io/paper/<version>/org/bukkit/inventory/ItemRarity.html
```

## Enchantments

After 1.20.5 version, we follow use Minecraft's vanilla enchantment ID instead of legacy one used in Spigot API, please go to Minecraft wiki site if you are using 1.20.5+ version.

```markup
https://jd.papermc.io/paper/<version>/org/bukkit/enchantments/Enchantment.html
```

## Attribute ID

<pre class="language-html"><code class="lang-html"><strong>https://jd.papermc.io/paper/&#x3C;version>/org/bukkit/attribute/Attribute.html
</strong></code></pre>

## Slot

```markup
https://jd.papermc.io/paper/<version>/org/bukkit/inventory/EquipmentSlot.html
```

## Banner Pattern

```markup
https://jd.papermc.io/paper/<version>/org/bukkit/block/banner/PatternType.html
```

## Dye Color (includes Banner Pattern Color)

```markup
https://jd.papermc.io/paper/<version>/org/bukkit/DyeColor.html
```

## Trim Material

```markup
https://jd.papermc.io/paper/<version>/org/bukkit/inventory/meta/trim/TrimMaterial.html
```

## Trim Pattern

```markup
https://jd.papermc.io/paper/<version>/org/bukkit/inventory/meta/trim/TrimPattern.html
```

## Damage Type Tag

```markup
https://jd.papermc.io/paper/<version>/org/bukkit/tag/DamageTypeTags.html
```

## Entity Type

```markup
https://jd.papermc.io/paper/<version>/org/bukkit/entity/EntityType.html
```

## Material / Entity Type Tag

```markup
https://jd.papermc.io/paper/<version>/org/bukkit/Tag.html
```

## Potion Base Effect

### After 1.20.5

`WATER, MUNDANE, THICK, AWKWARD, NIGHT_VISION, LONG_NIGHT_VISION, INVISIBILITY, LONG_INVISIBILITY, LEAPING, LONG_LEAPING, STRONG_LEAPING, FIRE_RESISTANCE, LONG_FIRE_RESISTANCE, SWIFTNESS, LONG_SWIFTNESS, STRONG_SWIFTNESS, SLOWNESS, LONG_SLOWNESS, STRONG_SLOWNESS, WATER_BREATHING, LONG_WATER_BREATHING, HEALING, STRONG_HEALING, HARMING, STRONG_HARMING, POISON, LONG_POISON, STRONG_POISON, REGENERATION, LONG_REGENERATION, STRONG_REGENERATION, STRENGTH, LONG_STRENGTH, STRONG_STRENGTH, WEAKNESS, LONG_WEAKNESS, LUCK, TURTLE_MASTER, LONG_TURTLE_MASTER, STRONG_TURTLE_MASTER, SLOW_FALLING, LONG_SLOW_FALLING, WIND_CHARGED, WEAVING, OOZING, INFESTED`.

### Before 1.20.5

`AWKWARD, FIRE_RESISTANCE, INSTANT_DAMAGE, INSTANT_HEAL, INVISIBILITY, JUMP, LUCK, MUNDANE, NIGHT_VISION, POISON, REGEN, SLOW_FALLING, SLOWNESS, SPEED, STRENGTH, THICK, TURTLE_MASTER, UNCRAFTABLE, WATER, WATER_BREATHING, WEAKNESS`.

## Music

```markup
https://jd.papermc.io/paper/<version>/org/bukkit/MusicInstrument.html
```
