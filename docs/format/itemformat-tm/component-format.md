# Component Format

{% hint style="info" %}
This feature is only available for **1.21.6+** **Paper** server users.
{% endhint %}

## Custom Name

```yaml
component:
  name: '<blue>A good sword'
```

## Item Name

```yaml
component:
  item-name: '<yellow>Not Bad sword'
```

## Lore

```yaml
component:
  lore:
    - '<gray>This is really nice!'
```

## Custom Model Data

```yaml
component:
  custom-model-data:
    float: # Custom Model Data Type
      - '1'
    flag:
      - 'true'
    string:
      - 'Let me just test it!'
    color:
      - '255, 255, 0'
```

## Max Stack

```yaml
component:
  max-stack: 5
```

## Food

```yaml
component:
  food:
    can-always-eat: true
    nutrition: 5
    saturation: 5
```

## Tool

```yaml
component:
  tool:
    damage-per-block: 5
    mining-speed: 1.3
    destroy-blocks-in-creative: true
    rules:
      # blocks, speed, correctForDrops
      - 'stone, 1.4, true'
```

## Jukebox

```yaml
component:
  song: otherside
```

## Glow

```yaml
component:
```

## Unbreakable

```yaml
component:
```

## Rarity

```yaml
component:
```

## Hide Tooltips

```yaml
component:
  hide-tooltip:
    - 'lore' # Data Type. For list of them, please view: https://jd.papermc.io/paper/1.21.5/io/papermc/paper/datacomponent/DataComponentTypes.html
```

## Enchants / Stored Enchants

```yaml
component:
  enchants:
    mending: 1 # Enchant ID: Level
  stored-enchants: # For enchantment book
    mending: 1
```

## Attributes

```yaml
component:
  attributes:
    max_health: # Attribute ID
      name: 'UltiamteShop'
      amount: 5
      operation: ADD_NUMBER
      slot: ANY
```

## Damage / Max Damage

```yaml
component:
  damage: 5
  max-damage: 15
```

## Misc

```yaml
component:
  banner-patterns:
    BASE: WHITE # Pattern ID: Pattern Color
  potion:
    base-effect: 'WATER'
    effects:
      - 'SPEED, 100, 1, true, true, false' # Potion Type ID, Duration, Amplifier, Ambient, Particles, Icon
    color: '255, 255, 0'
    name: 'GOOD'
  potion-duration-scale: 3
  trim:
    material: IRON
    pattern: TIDE
  leather-color: '255, 255, 0'
  firework:
    1: 
      flicker: true
      trial: true
      colors:
        base:
          - 255, 255, 0
        fade:
          - 0, 0, 0
      type: BALL
      duration: 155
  break-sound: 'namespace:name'
  suspicious-stew-effects:
    - 'SPEED, 100, 1, true, true, false'
  charged-projectiles:
    material: arrow # Item Format
  axolotl-variant: LUCY
  tropical-fish-base-color: WHITE
  tropical-fish-pattern-color: WHITE
  tropical-fish-pattern: KOB
  bundle-contents:
    1: 
      material: STONE # Item Format
    2:
      material: APPLE
      amount: 5
  ominous-bottle-amplifier: 3
  music: PONDER_GOAT_HORN
  repair-cost: 15
  enchantable: 15
  glider: true
  item-model: 'mycustom:itemmodel'
  tootip-style: 'mycustom:tooltip'
  use-cooldown:
  cooldown-group: 'minecraft:custom_weapon'
  cooldown-seconds: 1.5
  damage-resistant: is_fall
  equippable:
    slot: head
  blocks-attacks:
    block-delay-seconds: 3
    disable-cooldown-scale: 1.2
    block-sound: 'mycustom:sound'
    bypassed-by: 'arrow'
  use-reminder:
    material: arrow # Item Format
  consumable:
    consume-seconds: 1
    animation: DRINK
    sound: 'mycustom:sound'
    effects:
      random-teleport: 2
      clear-effect: true
      apply-effect:
        probability: 6
        effects:
          - 'SPEED, 100, 1, true, true, false' # Potion Type ID, Duration, Amplifier, Ambient, Particles, Icon
      play-sound: 'mycustom:sound'
  weapon:
    damage-per-attack: 5
    disable-blocking-seconds: 3
  skull: eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2YzZmVkMTZmZDU1MTkwOWZhNWUyOWNkZDY5N2VlMzQ2ZTYzMzkwYjM4M2E0MzAwYTY2MmE4MGI2NGQ5ZWIxNyJ9fX0=
```

## 1.21.11 newly added Components

```yaml
material: DIAMOND_SWORD
component:
  name: <aqua>Test Kinetic Sword
  lore:
    - <gray>This is a test weapon
    - <gray>With all possible 1.21 components
  damage-type: minecraft:player_attack
  kinetic-weapon:
    contact-cooldown-ticks: 10
    delay-ticks: 2
    forward-movement: 0.3
    damage-multiplier: 1.5
    sound: minecraft:item.axe.swing
    hit-sound: minecraft:entity.player.attack.knockback
    damage-conditions:
      max-duration-ticks: 20
      min-speed: 0.15
      min-relative-speed: 0.1
    knockback-conditions:
      max-duration-ticks: 30
      min-speed: 0.12
      min-relative-speed: 0.08
    dismount-conditions:
      max-duration-ticks: 15
      min-speed: 0.2
      min-relative-speed: 0.1
  minimum-attack-charge: 0.65
  piercing-weapon:
    deals-knockback: true
    dismounts: true
    sound: minecraft:item.crossbow.shoot
    hit-sound: minecraft:item.crossbow.hit
  swing-animation:
    type: NONE
    duration: 7
  use-effects:
    can-sprint: true
    interact-vibrations: false
    speed-multiplier: 0.4

```
