# Component Format

Paper components are configured inside the `component` section of an [ItemFormat](README.md). This feature requires **Paper 1.21.5 or later** and **UltimateShop Premium**. Components introduced in newer Minecraft versions require the corresponding server version.

Keep `material` and `amount` at the ItemFormat root. Put Paper component options under `component`:

```yaml
material: DIAMOND_SWORD
amount: 1

component:
  name: '<aqua>Component sword'
  lore:
    - '<gray>Built with Paper components'
  glow: true
  damage: 25
  max-damage: 2048
```

Component options are applied after the root ItemFormat options. When both configure the same property, the component value is applied last. `debuild-item-method` controls how items are **saved to ItemFormat**; you do not need to change it to build an item from a `component` section.

Use numbers and booleans as YAML numbers and booleans, without quotes. Use lowercase namespaced registry IDs such as `minecraft:sharpness` and `minecraft:attack_damage`. Enum values such as `EPIC`, `EAT` and `ADD_NUMBER` use the spelling shown in the examples.

## Server version support

| Minimum Paper version | Components and options |
| --- | --- |
| 1.21.5 | Base components documented below, including food, tools, equipment, consumables, damage resistance and blocking |
| 1.21.6 | Attribute display modes; `equippable.can-be-sheared` and `equippable.shear-sound` |
| 1.21.11 | Attack range, damage type, kinetic weapons, minimum attack charge, piercing weapons, swing animation and use effects |
| 26.1 | Damage resistance and blocking also accept lists of damage type IDs |
| 26.2 | Sulfur cube content |
| 26.3 | Attack/interact animations, waxed signs, cushion color, villager food, pottery patterns, mob visibility and sign text |

Options for an unregistered component are not a compatibility fallback: they will not be applied on an older server. Use the examples for your server version. For a practical checklist and complete sample items, see [Testing Paper Components](component-testing.md).

The version suffix on each component heading is the minimum Paper version supported by the plugin's component adapter. Options added later have their own version heading.

## Custom Name - 1.21.5+

Configuration key: `component.name`.

Sets the custom display name. MiniMessage formatting is supported.

```yaml
component:
  name: <aqua>Paper component name test
```

## Item Name - 1.21.5+

Configuration key: `component.item-name`.

Sets the item name component independently of the custom display name.

```yaml
component:
  item-name: <gold>Paper component item name
```

## Lore - 1.21.5+

Configuration key: `component.lore`.

Sets the lore lines in their configured order. MiniMessage formatting is supported.

```yaml
component:
  lore:
    - <gray>First lore line
    - <green>Second lore line
```

## Custom Model Data - 1.21.5+

Configuration key: `component.custom-model-data`.

Each type is a separate list. `float` entries must be numbers and `flag` entries must be booleans. Values should remain in the same order and should not multiply when the item is saved and rebuilt.

```yaml
component:
  custom-model-data:
    float:
      - 101.0
      - 202.5
    flag:
      - true
      - false
    string:
      - test-a
      - test-b
    color:
      - '#FF5555'
      - '#55FF55'
```

Custom model data does not create a model. A resource pack must define how these values change the item's appearance.

## Max Stack Size - 1.21.5+

Configuration key: `component.max-stack`.

Accepts 1–99. Use a stack size of 1 for damageable items.

```yaml
component:
  max-stack: 16
```

## Damage - 1.21.5+

Configuration key: `component.damage`.

Sets the amount of durability already used. Use a non-negative integer; this is not the remaining durability.

```yaml
component:
  damage: 37
```

## Max Damage - 1.21.5+

Configuration key: `component.max-damage`.

Sets the maximum durability to a positive integer. Use `max-stack: 1` for a damageable item.

```yaml
component:
  max-damage: 2048
```

## Enchantment Glint - 1.21.5+

Configuration key: `component.glow`.

Overrides the enchantment glint. Use `true` to show it or `false` to hide it.

```yaml
component:
  glow: true
```

## Unbreakable - 1.21.5+

Configuration key: `component.unbreakable`.

Use `true` to prevent normal durability loss. Omit this option when testing durability loss and item breakage.

```yaml
component:
  unbreakable: true
```

## Rarity - 1.21.5+

Configuration key: `component.rarity`.

Accepts `COMMON`, `UNCOMMON`, `RARE` or `EPIC`.

```yaml
component:
  rarity: EPIC
```

## Repair Cost - 1.21.5+

Configuration key: `component.repair-cost`.

Sets the prior-work penalty used by an anvil. Use a non-negative integer.

```yaml
component:
  repair-cost: 7
```

## Enchantable - 1.21.5+

Configuration key: `component.enchantable`.

Sets the enchantability value. Use a non-negative integer.

```yaml
component:
  enchantable: 20
```

## Break Sound - 1.21.5+

Configuration key: `component.break-sound`.

Sets the namespaced sound key played when the item breaks. Test with a damageable item without `unbreakable: true`.

```yaml
component:
  break-sound: minecraft:block.glass.break
```

## Enchantments - 1.21.5+

Configuration key: `component.enchants`.

Maps enchantment registry IDs to levels. These are the item’s active enchantments.

```yaml
component:
  enchants:
    sharpness: 5
    unbreaking: 3
```

## Stored Enchantments - 1.21.5+

Configuration key: `component.stored-enchants`.

Maps enchantment registry IDs to stored levels, as used by enchanted books.

```yaml
component:
  stored-enchants:
    sharpness: 4
    mending: 1
```

## Hidden Tooltip Components - 1.21.5+

Configuration key: `component.hide-tooltip`.

Lists native component registry IDs to hide in the tooltip. Hiding the tooltip does not remove the data or disable its behavior.

```yaml
component:
  hide-tooltip:
    - minecraft:enchantments
    - minecraft:attribute_modifiers
```

## Attribute Modifiers - 1.21.5+

Configuration key: `component.attributes`.

Attribute `type` and modifier `name` are namespaced keys. `slot` uses an equipment slot group such as `mainhand`, `offhand`, `hand`, `head`, `chest`, `legs`, `feet`, `armor` or `any`.

```yaml
component:
  attributes:
    - type: minecraft:attack_damage
      name: ultimateshop:test_damage
      amount: 8.0
      operation: ADD_NUMBER
      slot: mainhand
    - type: minecraft:attack_speed
      name: ultimateshop:test_speed
      amount: -1.5
      operation: ADD_NUMBER
      slot: mainhand
```

Use `ADD_NUMBER`, `ADD_SCALAR` or `MULTIPLY_SCALAR_1` for `operation`.

### Attribute Display - 1.21.6+

Each entry can also use `display-mode: RESET`, `HIDDEN` or `OVERRIDE`. `DEFAULT` is an alias for `RESET`; `TEXT` is an alias for `OVERRIDE`. When no mode is specified, the 1.21.6+ adapter hides the modifier's tooltip display. `OVERRIDE` requires `display-text`:

```yaml
component:
  attributes:
    - type: minecraft:attack_damage
      name: ultimateshop:custom_damage
      amount: 8.0
      operation: ADD_NUMBER
      slot: mainhand
      display-mode: OVERRIDE
      display-text: '<red>Custom attack bonus'
```

## Food - 1.21.5+

Configuration key: `component.food`.

Sets nutrition, saturation and whether the item can be eaten at full hunger. Use `consumable` to configure its use animation, duration and consume effects.

```yaml
component:
  food:
    nutrition: 6
    saturation: 0.6
    can-always-eat: true
```

<a id="consumables"></a>

## Consumables - 1.21.5+

Configuration key: `component.consumable`.

`food` sets nutrition and saturation. `consumable` controls the use animation, duration, sound and consume effects. Include `can-always-eat: true` when testing food at full hunger.

```yaml
material: APPLE
amount: 1
name: '&bTest consumable'
component:
  food:
    nutrition: 2
    can-always-eat: true
  consumable:
    consume-seconds: 1.25
    animation: EAT
    sound: minecraft:entity.generic.eat
    effects:
      '1':
        type: apply-effect
        probability: 1.0
        effects:
          - minecraft:speed,200,0,false,true,false
      '2':
        type: play-sound
        sound: minecraft:entity.player.levelup
```

Consume effects are entries inside `consumable.effects`. Each entry has a `type` and its own settings:

| Type | Settings |
| --- | --- |
| `apply-effect` | `probability` from 0.0 to 1.0, plus an `effects` list |
| `play-sound` | `sound`, a namespaced sound key |
| `random-teleport` | `diameter`, the teleport diameter; old `chance` input is also accepted |
| `clear-effect` | No additional settings; removes all status effects |

A potion effect string is `effect ID,duration in ticks,amplifier,ambient,particles,icon`. Amplifiers start at 0. The first three fields are required; omitted boolean fields default to true. Effects are applied in the configured order. For example, put `clear-effect` before `apply-effect` if the new status effects should remain.

```yaml
component:
  consumable:
    consume-seconds: 1.0
    animation: EAT
    effects:
      '1':
        type: clear-effect
      '2':
        type: apply-effect
        probability: 1.0
        effects:
          - 'minecraft:speed,200,0,false,true,false'
      '3':
        type: random-teleport
        diameter: 4.0
```

Each effect entry adds only its own effect. Saving a consumable uses this same numbered-entry structure.

## Use Cooldown - 1.21.5+

Configuration key: `component.use-cooldown`.

`cooldown-seconds` accepts a non-negative number of seconds, including fractional values such as `1.5`. `cooldown-group` is a namespaced key shared by items in the same cooldown group. Test on a usable item, for example an apple with `food` and `consumable` configured.

```yaml
component:
  use-cooldown:
    cooldown-seconds: 3
    cooldown-group: ultimateshop:component_test
```

## Use Remainder - 1.21.5+

Configuration key: `component.use-reminder`.

The existing configuration key is **`use-reminder`**. Keep that spelling even though the native component is called `use_remainder`. Its value is a complete nested ItemFormat, directly under `use-reminder`, without an extra `item:` wrapper. Material, amount and nested component values are preserved when saved and rebuilt.

```yaml
material: APPLE
amount: 1
name: '&bTest use-reminder'
component:
  food:
    nutrition: 1
    can-always-eat: true
  consumable:
    consume-seconds: 1.0
    animation: EAT
    sound: minecraft:entity.generic.eat
  use-reminder:
    material: GLASS_BOTTLE
    amount: 1
    component:
      name: <yellow>Bottle returned after use
```

## Tool - 1.21.5+

Configuration key: `component.tool`.

Each rule string is `block ID[,additional block IDs],speed,correct-for-drops`. Use `true`, `false` or `null` for the last field. Use namespaced block IDs.

```yaml
component:
  tool:
    damage-per-block: 1
    mining-speed: 4.0
    destroy-blocks-in-creative: true
    rules:
      - minecraft:stone,minecraft:deepslate,12.0,true
      - minecraft:dirt,6.0,false
```

## Equippable - 1.21.5+

Configuration key: `component.equippable`.

```yaml
material: LEATHER_CHESTPLATE
amount: 1
name: '&bTest equippable'
component:
  equippable:
    slot: CHEST
    equip-sound: minecraft:item.armor.equip_leather
    asset-id: minecraft:leather
    allowed-entities:
      - minecraft:player
    dispensable: true
    swappable: true
    damage-on-hurt: true
    equip-on-interact: false
```

### Shearing Options - 1.21.6+

These additional equipment options require Paper 1.21.6+:

```yaml
component:
  equippable:
    slot: CHEST
    can-be-sheared: true
    shear-sound: minecraft:entity.sheep.shear
```

## Glider - 1.21.5+

Configuration key: `component.glider`.

Adds gliding behavior when `true`. Use an appropriate wearable item with `equippable.slot: CHEST` when testing.

```yaml
component:
  glider: true
```

## Dyed Color - 1.21.5+

Configuration key: `component.leather-color`.

Sets the leather dye color. Accepts a quoted hex string such as `'#55AAFF'`, an RGB string such as `'85,170,255'`, or an RGB integer string.

```yaml
component:
  leather-color: '#55AAFF'
```

## Armor Trim - 1.21.5+

Configuration key: `component.trim`.

Sets armor trim material and pattern using namespaced registry IDs. Both IDs must exist on the server.

```yaml
component:
  trim:
    material: minecraft:diamond
    pattern: minecraft:sentry
```

## Damage Resistance - 1.21.5+

Configuration key: `component.damage-resistant`.

A string value refers to a damage type tag without a leading `#`, such as `minecraft:is_fire`.

```yaml
component:
  damage-resistant: minecraft:is_fire
```

### Damage Type List - 26.1+

A list contains individual damage type IDs instead of a tag string. These are different formats: `minecraft:is_fire` is a tag, while `minecraft:in_fire` is an individual damage type.

```yaml
component:
  damage-resistant:
    - minecraft:in_fire
    - minecraft:on_fire
```

## Blocks Attacks - 1.21.5+

Configuration key: `component.blocks-attacks`.

Configures blocking behavior. `block-delay-seconds` is a numeric delay in seconds; `block-sound` is a separate namespaced sound key. A string `bypassed-by` value refers to a damage type tag without a leading `#`.

```yaml
component:
  blocks-attacks:
    block-delay-seconds: 0.25
    disable-cooldown-scale: 1.0
    block-sound: minecraft:item.shield.block
    bypassed-by: minecraft:bypasses_shield
```

### Bypassed Damage Type List - 26.1+

Use a list of individual damage type IDs for `bypassed-by` on Paper 26.1+.

```yaml
component:
  blocks-attacks:
    block-delay-seconds: 0.25
    bypassed-by:
      - minecraft:arrow
      - minecraft:trident
```

## Weapon - 1.21.5+

Configuration key: `component.weapon`.

Configures durability damage per attack and how long the weapon disables blocking.

```yaml
component:
  weapon:
    damage-per-attack: 2
    disable-blocking-seconds: 3.0
```

## Bundle Contents - 1.21.5+

Configuration key: `component.bundle-contents`.

Each named or numbered entry is a complete ItemFormat with `material`, `amount` and an optional `component` section. Saving and rebuilding must retain the nested item types, amounts and component data.

```yaml
material: BUNDLE
amount: 1
name: '&bTest bundle-contents'
component:
  bundle-contents:
    '1':
      material: DIAMOND
      amount: 2
      component:
        name: <aqua>Diamond inside bundle
    '2':
      material: EMERALD
      amount: 3
```

## Charged Projectiles - 1.21.5+

Configuration key: `component.charged-projectiles`.

Each named or numbered child is a projectile ItemFormat. Do not put a single `material` directly under `charged-projectiles`. A saved and rebuilt crossbow should retain its configured projectile.

```yaml
material: CROSSBOW
amount: 1
name: '&bTest charged-projectiles'
component:
  charged-projectiles:
    '1':
      material: ARROW
      amount: 1
```

## Banner Patterns - 1.21.5+

Configuration key: `component.banner-patterns`.

Each list entry uses `pattern registry ID: DYE_COLOR`. Patterns are applied in the configured order.

```yaml
component:
  banner-patterns:
    - 'minecraft:stripe_bottom: RED'
    - 'minecraft:cross: BLUE'
    - 'minecraft:border: BLACK'
```

## Potion Contents - 1.21.5+

Configuration key: `component.potion`.

`base-effect` uses a `PotionType` enum value. Custom effects use `effect ID,duration in ticks,amplifier,ambient,particles,icon`; the effect ID is namespaced and amplifiers start at 0.

```yaml
component:
  potion:
    base-effect: SWIFTNESS
    color: '#55FFFF'
    name: component_test
    effects:
      - minecraft:strength,600,1,false,true,true
      - minecraft:regeneration,200,0,false,true,false
```

## Potion Duration Scale - 1.21.5+

Configuration key: `component.potion-duration-scale`.

Multiplies the duration of potion effects. Test on an item with potion contents.

```yaml
component:
  potion-duration-scale: 2.0
```

## Suspicious Stew Effects - 1.21.5+

Configuration key: `component.suspicious-stew-effects`.

Each entry needs only `effect ID,duration in ticks`. Older entries with additional fields remain readable; those extra fields do not change the stew effect.

```yaml
component:
  suspicious-stew-effects:
    - minecraft:night_vision,200
    - minecraft:saturation,20
```

## Fireworks - 1.21.5+

Configuration key: `component.firework`.

Use `trail`, not `trial`. Flight `duration` is inside `firework`, alongside the numbered effect entries; it accepts 0–255.

```yaml
component:
  firework:
    duration: 2
    '1':
      type: STAR
      flicker: true
      trail: true
      colors:
        base:
          - '#FF5555'
          - '#55FF55'
        fade:
          - '#5555FF'
```

## Axolotl Variant - 1.21.5+

Configuration key: `component.axolotl-variant`.

Sets the axolotl bucket variant using an enum value such as `BLUE` or `LUCY`. Test with `AXOLOTL_BUCKET`.

```yaml
component:
  axolotl-variant: BLUE
```

## Tropical Fish Base Color - 1.21.5+

Configuration key: `component.tropical-fish-base-color`.

Sets the tropical fish bucket’s body dye color. Test with `TROPICAL_FISH_BUCKET`.

```yaml
component:
  tropical-fish-base-color: ORANGE
```

## Tropical Fish Pattern Color - 1.21.5+

Configuration key: `component.tropical-fish-pattern-color`.

Sets the tropical fish bucket’s pattern dye color. Test with `TROPICAL_FISH_BUCKET`.

```yaml
component:
  tropical-fish-pattern-color: BLUE
```

## Tropical Fish Pattern - 1.21.5+

Configuration key: `component.tropical-fish-pattern`.

Sets the tropical fish bucket’s pattern enum, such as `KOB`.

```yaml
component:
  tropical-fish-pattern: KOB
```

## Player Profile - 1.21.5+

Configuration key: `component.skull`.

`skull` (or `skull-meta`) is a base64 texture property in this Paper component format, rather than a player-name lookup. Texture display also depends on the client being able to load the texture. Test with `PLAYER_HEAD`.

```yaml
component:
  skull: eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2YzZmVkMTZmZDU1MTkwOWZhNWUyOWNkZDY5N2VlMzQ2ZTYzMzkwYjM4M2E0MzAwYTY2MmE4MGI2NGQ5ZWIxNyJ9fX0=
```

## Instrument - 1.21.5+

Configuration key: `component.music`.

Sets a registered instrument using its namespaced ID. Test with `GOAT_HORN`.

```yaml
component:
  music: minecraft:ponder_goat_horn
```

## Jukebox Playable - 1.21.5+

Configuration key: `component.song`.

Sets a registered jukebox song using its namespaced ID. Test by inserting the item into a jukebox.

```yaml
component:
  song: minecraft:cat
```

## Ominous Bottle Amplifier - 1.21.5+

Configuration key: `component.ominous-bottle-amplifier`.

Accepts 0–4. Test with `OMINOUS_BOTTLE`.

```yaml
component:
  ominous-bottle-amplifier: 2
```

## Item Model - 1.21.5+

Configuration key: `component.item-model`.

Sets the namespaced item model key. Custom models require matching resource pack assets; the key alone does not create a model.

```yaml
component:
  item-model: minecraft:diamond_sword
```

## Tooltip Style - 1.21.5+

Configuration key: `component.tooltip-style`.

Sets a custom tooltip style key. The spelling is `tooltip-style`, not `tootip-style`. A matching resource pack asset is required to change its appearance.

```yaml
component:
  tooltip-style: minecraft:component_test
```

## Attack Range - 1.21.11+

Configuration key: `component.attack-range-key`.

The configuration key is **`attack-range-key`**. Reach values accept 0–64, `hitbox-margin` accepts 0–1, and `mob-factor` accepts 0–2.

```yaml
component:
  attack-range-key:
    min-reach: 0.0
    max-reach: 4.0
    min-creative-reach: 0.0
    max-creative-reach: 5.0
    hitbox-margin: 0.3
    mob-factor: 1.0
```

## Damage Type - 1.21.11+

Configuration key: `component.damage-type`.

Sets the weapon’s damage type using an individual damage type registry ID, rather than a damage type tag.

```yaml
component:
  damage-type: minecraft:player_attack
```

## Kinetic Weapon - 1.21.11+

Configuration key: `component.kinetic-weapon`.

Configures kinetic weapon contact cooldown, delay, movement, damage multiplier, sounds and conditions. Condition durations are measured in ticks. Test separately from other components that change the same attack interaction.

```yaml
component:
  kinetic-weapon:
    contact-cooldown-ticks: 10
    delay-ticks: 2
    forward-movement: 0.3
    damage-multiplier: 1.5
    sound: minecraft:entity.player.attack.sweep
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
```

## Minimum Attack Charge - 1.21.11+

Configuration key: `component.minimum-attack-charge`.

Accepts a value from 0 to 1 for the minimum attack charge.

```yaml
component:
  minimum-attack-charge: 0.65
```

## Piercing Weapon - 1.21.11+

Configuration key: `component.piercing-weapon`.

Configures piercing weapon knockback, dismount behavior and sounds.

```yaml
component:
  piercing-weapon:
    deals-knockback: true
    dismounts: true
    sound: minecraft:entity.player.attack.sweep
    hit-sound: minecraft:entity.player.attack.strong
```

## Swing Animation - 1.21.11+

Configuration key: `component.swing-animation`.

Configures the legacy swing animation. On Paper 1.21.11 through 26.2 this supplies the swing component. On Paper 26.3+ it supplies both attack and interact animations, except where an explicit `attack-animation` or `interact-animation` section overrides its respective side.

```yaml
component:
  swing-animation:
    type: NONE
    duration: 7
```

## Use Effects - 1.21.11+

Configuration key: `component.use-effects`.

Configures sprinting, interaction vibrations and movement speed while using the item. `speed-multiplier` accepts 0–1. Test on a usable item with a use duration, for example an apple with `food` and `consumable` configured.

```yaml
component:
  use-effects:
    can-sprint: true
    interact-vibrations: false
    speed-multiplier: 0.4
```

## Sulfur Cube Content - 26.2+

Configuration key: `component.sulfur-cube-content`.

The content is a complete nested ItemFormat.

```yaml
component:
  sulfur-cube-content:
    material: DIAMOND
    amount: 1
```

## Attack Animation - 26.3+

Configuration key: `component.attack-animation`.

Sets the attack animation independently. Takes precedence over `swing-animation` for attack behavior.

```yaml
component:
  attack-animation:
    type: NONE
    duration: 9
```

## Interact Animation - 26.3+

Configuration key: `component.interact-animation`.

Sets the interaction animation independently. Takes precedence over `swing-animation` for interaction behavior.

```yaml
component:
  interact-animation:
    type: NONE
    duration: 5
```

## Sign Text Front - 26.3+

Configuration key: `component.sign-text-front`.

Configures the front side of a sign. Use four lines, a dye color and `glowing` as needed. Test by placing an `OAK_SIGN` item.

```yaml
component:
  sign-text-front:
    lines:
      - Front line 1
      - Front line 2
      - Front line 3
      - Front line 4
    color: RED
    glowing: true
```

## Sign Text Back - 26.3+

Configuration key: `component.sign-text-back`.

Configures the back side of a sign. Use four lines, a dye color and `glowing` as needed. Test by placing an `OAK_SIGN` item.

```yaml
component:
  sign-text-back:
    lines:
      - Back line 1
      - Back line 2
      - Back line 3
      - Back line 4
    color: BLUE
    glowing: true
```

## Waxed - 26.3+

Configuration key: `component.waxed`.

Use `true` to add the waxed state to a sign item. Check whether the placed sign is sealed against editing.

```yaml
component:
  waxed: true
```

## Cushion Color - 26.3+

Configuration key: `component.cushion-color`.

Sets the cushion dye color using a dye color enum such as `RED`. Use an appropriate material and interaction to check the visible result.

```yaml
component:
  cushion-color: RED
```

## Villager Food - 26.3+

Configuration key: `component.villager-food`.

Sets the item’s villager food value. Test the relevant villager interaction with an appropriate item material.

```yaml
component:
  villager-food: 4
```

## Provides Pottery Pattern - 26.3+

Configuration key: `component.provides-pottery-pattern`.

Sets a decorated pot pattern using its namespaced registry ID. The pattern must exist on the server.

```yaml
component:
  provides-pottery-pattern: minecraft:angler
```

## Mob Visibility - 26.3+

Configuration key: `component.mob-visibility`.

Applies a visibility factor to the listed entity types. `visibility` accepts 0–10. Entity IDs must exist in the server registry.

```yaml
component:
  mob-visibility:
    entities:
      - minecraft:zombie
      - minecraft:skeleton
    visibility: 0.5
```

## Saving and testing

Set `debuild-item-method: COMPONENT` in `config.yml` and reload the plugin to save supported item properties inside a `component` section. This setting does not change how Minecraft stores the item itself.

For file installation, give commands, round-trip checks and version-specific examples, continue to [Testing Paper Components](component-testing.md).
