# 📝ItemFormat™

{% hint style="info" %}
Mojang very like change material (item type), enchantment, potion and many other things's ID (or namespace key). For example, they changed sweeping enchantment ID to sweeping\_edge in 1.21. So, before you update your server version, you need carefully check that whether new versions have changed something, otherwise it will break your shop or menu configs.
{% endhint %}

{% hint style="info" %}
Hold a item then use command `/shop generateitemformat` to parse the ItemFormat of it. After execute this command, a new file called `generated-item-format.yml` wll be generated at `plugins/UltimateShop` folder. (or `plugins/<pluginNameYouAreUsing>`)
{% endhint %}

## Hook Item

This section tells you about the built-in custom item provider, you can use ItemBridge as custom item provider instead which supports more custom item plugins, click [here](../itembridge.md) to know more.

### Hook Plugin

Possible Value: `EcoItems, EcoArmor, MMOItems, ItemsAdder, Oraxen, MythicMobs, eco, NeigeItems, ExecutableItems, Nexo, CraftEngine`.&#x20;

```yaml
hook-plugin: MMOItems
```

### Hook Item

For `EcoItems, Oraxen, MythicMobs, Nexo`, you should write `item id`.

For `ItemsAdder, eco`, you should write `namespace:item id`.&#x20;

* `eco`'s namespace id plugin name, like `talismans`.  The `item id` follow below rules:&#x20;
  * Talisman: Just type talisman ID here.
  * EcoArmor:
    * Armor: `set_<setID>_<slotID>`&#x20;
    * Advanced Armor: `set_<setID>_<slotID>_advanced`&#x20;
    * Upgrade Crystal: `shard_<shardID>`
  * Reforges: `stone_<reforgeID>`

For `EcoArmor`, you should write `armor set id;;armor slot`. `armor slot` can be set to **BOOTS, CHESTPLATE, ELYTRA, HELMET, LEGGINGS**.

For `MMOItems`, you should write `item typeId;;item id`.

```yaml
hook-item: AXE;;TEST_AXE
```

For `CraftEngine`, you should write `namespace;;item id`.

{% hint style="info" %}
**Stop!** If you are just trying to get items from custom item plugins, `hook-plugin` and `hook-item` option should be enough for you, if your custom item plugins are not supported, please use command instead, all things below is suit for vanilla items, if you set them here, we will replace the related to item attributes and this will lead to the item you get here is different from the item should be in custom item plugins.
{% endhint %}

## Material

If you want to use saved item by plugin, just set this option value to saved item ID, like `material: superior_sword`.

If the value is empty or illegal, defaults to stone.

**Do not use this option when you have hook-plugin option, it won't work if you insist on it!**

```yaml
material: APPLE
```

## Amount

Support use PlaceholderAPI or math calculate. For example, `%player_health% * 5`.

```yaml
amount: 5
```

## Min Amount/Max Amount - Only support in price/products

When you use dynamic value in `amount` option, you can use `min-amount` and `max-amount` option to limit it's min value and max value. Useful for dynamic price.

```yaml
min-amount: 1
max-amount: 15
```

## Component (1.21.6+, Requrie Paper) <mark style="color:red;">- Premium</mark>

Use Paper's DataComponent API instead of Spigot's ItemMeta API to modify item easily, also all 1.21.5+ item attribute will only be available in `component` option, like **Weapon**. For more info, please view [this page](component-format.md).

You can found a option called `debuild-item-method` at `config.yml` file. You can set this option to **LEGACY** or **COMPONENT**. **COMPONENT** method only supports 1.21.6+ Paper servers, after set method set COMPONENT, we will parse item into this `component` option.

Start from 4.2.1 version, this feature require 1.21.6+ Paper servers.

```yaml
component:
  name: '<blue>A good sword'
  lore:
    - '<gray>This is really nice!'
  custom-model-data:
    float: # Custom Model Data Type
      - '1'
  max-stack: 6
  food:
    can-always-eat: true
    nutrition: 5
    saturation: 5
  tool:
    damage-per-block: 5
    mining-speed: 1.3
    destroy-blocks-in-creative: true
    rules:
      # blocks, speed, correctForDrops
      - 'stone, 1.4, true'
  song: otherside
  hide-tooltip:
    - 'lore'
  enchants:
    mending: 1
  glow: true
  attributes:
    max_health: 
      name: 'GENERIC_MAX_HEALTH' # Attribute ID
      amount: 5
      operation: ADD_NUMBER
      slot: MAINHAND
```

## Custom Name/Display Name

According to your configuration file, there are two formats, one is the old version color code used before version 1.9, or the Text Component used in later versions. The former uses a color code format we created, while the latter uses Mini Message format, as detailed [here](https://docs.advntr.dev/minimessage/format.html). Mini Message format require your server core is Paper.

```yaml
name: '&fA smart sword'
```

## Item Name (1.20.5+)

Item Name is a new item attribute added in 1.20.5, different from display name, item name can not be changed in anvil.

According to your configuration file, there are two formats, one is the old version color code used before version 1.9, or the Text Component used in later versions. The former uses a color code format we created, while the latter uses Mini Message format, as detailed [here](https://docs.advntr.dev/minimessage/format.html). Mini Message format require your server core is Paper.

```yaml
item-name: '&bRare Sword'
```

## Lore

You can use `\n` to represent line breaks.

According to your configuration file, there are two formats, one is the old version color code used before version 1.9, or the Text Component used in later versions. The former uses a color code format we created, while the latter uses Mini Message format, as detailed [here](https://docs.advntr.dev/minimessage/format.html). Mini Message format require your server core is Paper.

```yaml
lore:
  - '&fLine 1'
  - '&fLine 2'
```

## Max Stack (1.20.5+)

```yaml
max-stack: 99
```

## Food (1.20.5+)

Effects format: Potion Type ID, Duration, Amplifier, Ambient, Particles, Icon, Chance.

For `Ambient, Particles, Icon` is boolean type arg, and for `Chance` is a number from 0 to 1.

For `convert` is Item Format that the item will return to player after eaten this food (Require 1.21+, and this is optional).

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

## Tool (1.21+)

```yaml
tool:
  mining-speed: 1
  damage-per-block: 1
  rules:
    # Blocks, Speed, Drop after mine
    - 'stone, coal_ore, 1, true'
    # Support replace Blocks to Tag.
```

## Jukebox Playable (1.21+)

```yaml
song: CAT
show-song: false
```

## Fire Resistant (1.20.5+)

```yaml
fire-resistant: true
```

## Hide Tool Tip (1.20.5+)

```yaml
hide-tool-tip: true
```

## Unbreakable

```yaml
unbreakable: true
```

## Ratity (1.20.5+)

```yaml
rarity: COMMON
```

## Flags

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

## Enchants

Config section format is: `Enchant ID: Enchant Level`.

For enchantment book: You maybe need use `stored-enchants` instead of `enchants`.

For custom enchantments: Some enchantments plugins are not registered their enchantment into game, so this won't work for them.

**You should use Minecraft enchantment ID instead of Spigot's after 1.20.5.**

```yaml
enchants:
  MENDING: 1
```

## Glow (1.20.5+)

```yaml
glow: true
```

## Custom Model Data

```yaml
custom-model-data: 15
```

## Attributes

Please note that 1.20.5 has changed attribute format, if you generated item before 1.20.5, you have to reconfig them into new format.

Config section format is `Attribute ID` and then has multi sub options.&#x20;

```yaml
attributes:
  GENERIC_ATTACK_DAMAGE: 
    name: generic.attack_damage 
    amount: 12
    operation: ADD_NUMBER # ADD_NUMBER, ADD_SCALAR, MULTIPLY_SCALAR_1
    slot: HAND 
```

## Damageable

Represents an item that has durability and can take damage.

### Damage

```yaml
damage: 5
```

### Max Damage (1.20.5+)

Similar to max durability.

```yaml
max-damage: 1500
```

## Enchantment Storage

This is specific to items that can _store_ enchantments, as opposed to being enchanted. [`Material.ENCHANTED_BOOK`](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html#ENCHANTED_BOOK) is an example of an item with enchantment storage.

### Stored Enchants

Similar to [Enchants](./#enchants).

```yaml
stored-enchants:
  MENDING: 1
```

## Banner

### Patterns

Config section format is: `Pattern ID: Pattern Color`.

```yaml
patterns:
  BASE: WHITE
```

## Potion

Represents a potion or item that can have custom effects.

### Base Effect (1.20.5+)

```yaml
base-effect: 'WATER'
```

### Base Effect (Before 1.20.5)

Format: Potion Type ID, Extended, Upgraded.

For `Extended` and `Upgraded` arg is boolean type.

```yaml
base-effect: 'WATER, true, false'
```

### Effects / Custom Effects

Effects format: `Potion Type ID, Duration, Amplifier, Ambient, Particles, Icon`.

For `Ambient, Particles, Icon` is boolean type arg.

<pre class="language-yaml"><code class="lang-yaml"><strong>effects:
</strong>  - 'SPEED, 100, 1, true, true, false'
</code></pre>

### Color

Color has 2 formats:

* Red, Green, Blue
* One number (like 0xff0000)

```yaml
color: '5'
```

## Armor

Represents armor that an entity can equip.

### Trim (1.20+)

Config section format is 2 sub options.

```yaml
trim:
  material: IRON
  pattern: TIDE
```

## Leather Armor

### Color

Color has 2 formats:

* Red, Green, Blue
* One number (like 0xff0000)

```yaml
color: '0xff0000'
```

## Axolotl Bucket (1.17+)

### Color

Possible value: `LUCY, WILD, GOLD, CYAN, BLUE`.

```yaml
color: LUCY
```

## Tropical Fish Bucket (1.14+)

### Color

Possible value: `WHITE, ORANGE, MAGENTA, LIGHT_BLUE, YELLOW, LIME, PINK, GRAY, LIGHT_GRAY, CYAN, PURPLE, BLUE, BROWN, GREEN, RED, BLACK`.

```yaml
color: WHITE
```

### Pattern Color

Similar to `Color` above.

```yaml
pattern-color: WHITE
```

### Pattern

Possible value: `KOB, SUNSTREAK, SNOOPER, DASHER, BRINELY, SPOTTY, FLOPPER, STRIPEY, GLITTER, BLOCKFISH, BETTY, CLAYFISH`.

```yaml
pattern: KOB
```

## Skull&#x20;

Support 2 formats:

* Base64: Like example below, only support 1.19+.
* Player Name: Need online mode, support all versions. <mark style="color:red;">UltimateShop-Premium supports use PlaceholderAPI's placeholder here.</mark>

```yaml
skull: eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2YzZmVkMTZmZDU1MTkwOWZhNWUyOWNkZDY5N2VlMzQ2ZTYzMzkwYjM4M2E0MzAwYTY2MmE4MGI2NGQ5ZWIxNyJ9fX0=
```

## Firwork

### Power&#x20;

```yaml
power: 1
```

### Firework Effect

Config section format is multi sub options.

For `type` possible value: `BALL, BALL_LARGE, STAR, BURST, CREEPER`.

```yaml
firework:
  1: 
    flicker: true
    trial: true
    colors:
      base:
        - 5
      fade:
        - 666666
    type: BALL
```

## Firework Star

### Firework Effect

For `type` possible value: `BALL, BALL_LARGE, STAR, BURST, CREEPER`.

```yaml
firework:
  flicker: true
  trial: true
  colors:
    base:
      - 5
    fade:
      - 666666
  type: BALL
```

## Suspicious Stew (1.14+)

### Effects

Effects format: `Potion Type ID, Duration, Amplifier, Ambient, Particles, Icon`.

```yaml
effects:
  - 'SPEED, 100, 1, true, true, false'
```

## Bundle (1.17+)/Shulker

### Contents

Config section format is slot ID and then with item format.

```yaml
contents:
  1:
    material: STONE
  2:
    material: APPLE
    amount: 5
```

## Brushable Blocks (1.20+)

### Content

The loot item inside brushable blocks. Want custom brushable block? Consider buy our CustomArcheology plugin [here](https://www.spigotmc.org/resources/customarcheology-become-an-archaeologist-in-the-game-old-chunk-support-1-20.114142/)!

```yaml
content:
  material: APPLE
```

## Spawner

Only `spawner` option is required if you want spawner has mobs inside. Other options are optional.

```yaml
spawner: ZOMBIE
min-delay: 200
max-delay: 800
max-entities: 6
player-range: 16
spawn-range: 30
```

## Ominous Bottle (1.20.5+)

### Power

```yaml
power: 3
```

## Music Instrument (1.18+)

### Music

```yaml
music: PONDER_GOAT_HORN # Music
```

## Repairable

```yaml
repair-cost: 15
```

## Enchantable (1.21.2+)

```yaml
enchantable: 15
```

## Glider (1.21.2+)

```yaml
glider: true
```

## Item Model (1.21.2+)

```yaml
item-model: 'mycustom:itemmodel'
```

## Tooltip Style (1.21.2+)

```yaml
tootip-style: 'mycustom:tooltip'
```

## Use Cooldowns (1.21.2+)

```yaml
use-cooldown:
  cooldown-group: 'minecraft:custom_weapon'
  cooldown-seconds: 1.5
```

## Equippable (1.21.2+) <mark style="color:red;">- Premium</mark>

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

## Damage Resistant

```yaml
damage-resistant: is_fall # Damage Type Tag
```

## Extra Item Format Options

Some additional **ItemFormat** options can be found on the [Compatibility](../../info/compatibility.md) page. Their purpose is to hook with other plugins based on ItemFormat, so that other plugins can also participate in item generation.

Supported plugins at the moment:

* MythicChanger
* AdvancedEnchantments <mark style="color:red;">- Premium</mark>
* NBTAPI - <mark style="color:red;">Premium</mark>
