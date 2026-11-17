# 组件格式

Paper 组件在 [ItemFormat](README.md) 的 `component` 部分内配置。该功能需要 **Paper 1.21.5 或更高版本** 以及 **UltimateShop 付费版**。较新 Minecraft 版本引入的组件，还需要服务端为对应版本才能生效。

`material` 与 `amount` 仍然写在 ItemFormat 根层级，Paper 组件选项则统一放在 `component` 下：

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

组件选项会在根级 ItemFormat 选项之后应用。若两处配置了同一属性，则以组件中的值为准，因为它最后写入。`debuild-item-method` 只决定物品**如何保存为 ItemFormat**；从 `component` 部分构建物品时不必改动它。

数字与布尔值请按 YAML 的数字、布尔值书写，不要加引号。注册表 ID 使用小写并带命名空间，例如 `minecraft:sharpness`、`minecraft:attack_damage`；`EPIC`、`EAT`、`ADD_NUMBER` 等枚举值按示例中的拼写书写。

## 服务端版本支持

| 最低 Paper 版本 | 组件与选项 |
| --- | --- |
| 1.21.5 | 下文所述的基础组件，包括食物、工具、装备、消耗品、伤害抗性与格挡 |
| 1.21.6 | 属性显示模式；`equippable.can-be-sheared` 与 `equippable.shear-sound` |
| 1.21.11 | 攻击范围、伤害类型、动能武器、最小攻击蓄力、穿刺武器、挥舞动画与使用效果 |
| 26.1 | 伤害抗性与格挡还可接受伤害类型 ID 列表 |
| 26.2 | 硫立方体内容 |
| 26.3 | 攻击/交互动画、打蜡告示牌、坐垫颜色、村民食物、陶器图案、生物可见性与告示牌文本 |

未注册组件的选项**不属于**兼容性回退：在较旧的服务端上它们根本不会生效。请按你的服务端版本选用对应示例。实用的检查清单与完整示例物品，见[测试 Paper 组件](component-testing.md)。

每个组件标题后的版本后缀，是插件组件适配器所支持的最低 Paper 版本。后续新增的选项会有各自的版本标题。

## 自定义名称 - 1.21.5+

配置键：`component.name`。

设置自定义显示名称，支持 MiniMessage 格式化。

```yaml
component:
  name: <aqua>Paper component name test
```

## 物品名称 - 1.21.5+

配置键：`component.item-name`。

单独设置物品名称组件，与自定义显示名称互不影响。

```yaml
component:
  item-name: <gold>Paper component item name
```

## 物品描述 - 1.21.5+

配置键：`component.lore`。

按配置顺序设置物品描述的每一行，支持 MiniMessage 格式化。

```yaml
component:
  lore:
    - <gray>First lore line
    - <green>Second lore line
```

## 自定义模型数据 - 1.21.5+

配置键：`component.custom-model-data`。

每种类型都是独立的列表。`float` 条目必须是数字，`flag` 条目必须是布尔值。保存并重建物品后，各值应保持原有顺序，且不应发生倍乘。

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

自定义模型数据本身不会创建模型。这些值如何改变物品外观，需要由资源包定义。

## 最大堆叠数量 - 1.21.5+

配置键：`component.max-stack`。

接受 1–99。可损耗耐久的物品请将堆叠数量设为 1。

```yaml
component:
  max-stack: 16
```

## 伤害 - 1.21.5+

配置键：`component.damage`。

设置物品已消耗的耐久度，取非负整数。注意这不是剩余耐久度。

```yaml
component:
  damage: 37
```

## 最大耐久度 - 1.21.5+

配置键：`component.max-damage`。

将最大耐久度设为正整数。可损耗耐久的物品请配合 `max-stack: 1` 使用。

```yaml
component:
  max-damage: 2048
```

## 附魔闪光 - 1.21.5+

配置键：`component.glow`。

覆盖附魔光效。`true` 显示，`false` 隐藏。

```yaml
component:
  glow: true
```

## 牢不可破 - 1.21.5+

配置键：`component.unbreakable`。

设为 `true` 可阻止正常的耐久损耗。若要测试耐久损耗与物品损坏，请不要启用此项。

```yaml
component:
  unbreakable: true
```

## 稀有度 - 1.21.5+

配置键：`component.rarity`。

接受 `COMMON`、`UNCOMMON`、`RARE` 或 `EPIC`。

```yaml
component:
  rarity: EPIC
```

## 维修费用 - 1.21.5+

配置键：`component.repair-cost`。

设置铁砧使用的先前工作惩罚值，取非负整数。

```yaml
component:
  repair-cost: 7
```

## 可附魔 - 1.21.5+

配置键：`component.enchantable`。

设置可附魔值（附魔能力），取非负整数。

```yaml
component:
  enchantable: 20
```

## 破碎声音 - 1.21.5+

配置键：`component.break-sound`。

设置物品破碎时播放的声音键（带命名空间）。测试时请使用可损耗耐久的物品，且不要加 `unbreakable: true`。

```yaml
component:
  break-sound: minecraft:block.glass.break
```

## 附魔 - 1.21.5+

配置键：`component.enchants`。

将附魔注册表 ID 映射到等级，这些是物品当前生效的附魔。

```yaml
component:
  enchants:
    sharpness: 5
    unbreaking: 3
```

## 储存的附魔 - 1.21.5+

配置键：`component.stored-enchants`。

将附魔注册表 ID 映射到存储等级，附魔书使用的就是这种形式。

```yaml
component:
  stored-enchants:
    sharpness: 4
    mending: 1
```

## 隐藏工具提示组件 - 1.21.5+

配置键：`component.hide-tooltip`。

列出要在工具提示中隐藏的原版组件注册表 ID。隐藏工具提示不会删除数据，也不会禁用对应行为。

```yaml
component:
  hide-tooltip:
    - minecraft:enchantments
    - minecraft:attribute_modifiers
```

## 属性修饰符 - 1.21.5+

配置键：`component.attributes`。

属性的 `type` 与修饰符的 `name` 都是带命名空间的键。`slot` 使用装备槽位组，例如 `mainhand`、`offhand`、`hand`、`head`、`chest`、`legs`、`feet`、`armor` 或 `any`。

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

`operation` 可使用 `ADD_NUMBER`、`ADD_SCALAR` 或 `MULTIPLY_SCALAR_1`。

### 属性显示 - 1.21.6+

每个条目还可使用 `display-mode: RESET`、`HIDDEN` 或 `OVERRIDE`。`DEFAULT` 是 `RESET` 的别名，`TEXT` 是 `OVERRIDE` 的别名。未指定模式时，1.21.6+ 适配器会隐藏该修饰符在工具提示中的显示。`OVERRIDE` 需要同时提供 `display-text`：

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

## 食物 - 1.21.5+

配置键：`component.food`。

设置营养值、饱和度，以及饥饿值已满时是否仍可食用。使用动画、持续时间与消耗效果请通过 `consumable` 配置。

```yaml
component:
  food:
    nutrition: 6
    saturation: 0.6
    can-always-eat: true
```

<a id="consumables"></a>

## 消耗品 - 1.21.5+

配置键：`component.consumable`。

`food` 负责营养值与饱和度，`consumable` 负责使用动画、持续时间、声音与消耗效果。若要在饥饿值已满时测试食物，请加上 `can-always-eat: true`。

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

消耗效果是写在 `consumable.effects` 下的条目。每个条目都有一个 `type` 和各自的设置：

| 类型 | 设置 |
| --- | --- |
| `apply-effect` | `probability` 取值 0.0 到 1.0，外加一个 `effects` 列表 |
| `play-sound` | `sound`，带命名空间的声音键 |
| `random-teleport` | `diameter`，传送直径；也兼容旧的 `chance` 输入 |
| `clear-effect` | 无额外设置；清除全部状态效果 |

药水效果字符串的格式为 `effect ID,duration in ticks,amplifier,ambient,particles,icon`。增幅等级从 0 起算，前三个字段必填，省略的布尔字段默认为 true。效果按配置顺序依次应用，因此若希望新加的状态效果保留下来，应把 `clear-effect` 放在 `apply-effect` 之前。

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

每个效果条目只添加自己那一项效果。保存消耗品时同样沿用这种编号条目结构。

## 使用冷却时间 - 1.21.5+

配置键：`component.use-cooldown`。

`cooldown-seconds` 接受非负秒数，可为小数，例如 `1.5`。`cooldown-group` 是一个带命名空间的键，处于同一冷却组的物品共享它。请用可使用的物品测试，例如配置了 `food` 与 `consumable` 的苹果。

```yaml
component:
  use-cooldown:
    cooldown-seconds: 3
    cooldown-group: ultimateshop:component_test
```

## 使用后返还 - 1.21.5+

配置键：`component.use-reminder`。

配置键就是 **`use-reminder`**。尽管原版组件名为 `use_remainder`，这里的拼写仍需保留为 `use-reminder`。它的值是一个完整的嵌套 ItemFormat，直接写在 `use-reminder` 下，不需要再套一层 `item:`。保存并重建后，材质、数量与嵌套组件值都会保留。

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

## 工具 - 1.21.5+

配置键：`component.tool`。

每条规则字符串的格式为 `block ID[,additional block IDs],speed,correct-for-drops`，最后一个字段可取 `true`、`false` 或 `null`。方块 ID 需要带命名空间。

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

## 可装备 - 1.21.5+

配置键：`component.equippable`。

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

### 剪切选项 - 1.21.6+

以下附加装备选项需要 Paper 1.21.6+：

```yaml
component:
  equippable:
    slot: CHEST
    can-be-sheared: true
    shear-sound: minecraft:entity.sheep.shear
```

## 滑翔机 - 1.21.5+

配置键：`component.glider`。

设为 `true` 即赋予滑翔行为。测试时请使用合适的可穿戴物品，并配置 `equippable.slot: CHEST`。

```yaml
component:
  glider: true
```

## 皮革染色 - 1.21.5+

配置键：`component.leather-color`。

设置皮革的染色颜色。接受带引号的十六进制字符串（例如 `'#55AAFF'`）、RGB 字符串（例如 `'85,170,255'`），或 RGB 整数字符串。

```yaml
component:
  leather-color: '#55AAFF'
```

## 盔甲纹饰 - 1.21.5+

配置键：`component.trim`。

用带命名空间的注册表 ID 设置盔甲纹饰的材料与图案，两个 ID 都必须存在于服务端。

```yaml
component:
  trim:
    material: minecraft:diamond
    pattern: minecraft:sentry
```

## 伤害抗性 - 1.21.5+

配置键：`component.damage-resistant`。

字符串值表示一个伤害类型标签，不带前导 `#`，例如 `minecraft:is_fire`。

```yaml
component:
  damage-resistant: minecraft:is_fire
```

### 伤害类型列表 - 26.1+

列表中是单个伤害类型 ID，而不是标签字符串。两者格式不同：`minecraft:is_fire` 是标签，`minecraft:in_fire` 才是具体的伤害类型。

```yaml
component:
  damage-resistant:
    - minecraft:in_fire
    - minecraft:on_fire
```

## 格挡攻击 - 1.21.5+

配置键：`component.blocks-attacks`。

配置格挡行为。`block-delay-seconds` 是以秒为单位的数值延迟，`block-sound` 是一个单独的声音键（带命名空间）。`bypassed-by` 取字符串时表示伤害类型标签，不带前导 `#`。

```yaml
component:
  blocks-attacks:
    block-delay-seconds: 0.25
    disable-cooldown-scale: 1.0
    block-sound: minecraft:item.shield.block
    bypassed-by: minecraft:bypasses_shield
```

### 可绕过伤害类型列表 - 26.1+

在 Paper 26.1+ 上，`bypassed-by` 可以写成单个伤害类型 ID 的列表。

```yaml
component:
  blocks-attacks:
    block-delay-seconds: 0.25
    bypassed-by:
      - minecraft:arrow
      - minecraft:trident
```

## 武器 - 1.21.5+

配置键：`component.weapon`。

配置每次攻击消耗的耐久度，以及武器使对方格挡失效的时长。

```yaml
component:
  weapon:
    damage-per-attack: 2
    disable-blocking-seconds: 3.0
```

## 收纳袋内容 - 1.21.5+

配置键：`component.bundle-contents`。

每个具名或编号的条目都是一个完整的 ItemFormat，包含 `material`、`amount` 与可选的 `component` 部分。保存并重建后，嵌套的物品类型、数量与组件数据都必须保留。

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

## 蓄能弹射物 - 1.21.5+

配置键：`component.charged-projectiles`。

每个具名或编号的子项都是一个弹射物 ItemFormat。不要把单个 `material` 直接写在 `charged-projectiles` 下。保存并重建后的弩应保留其配置的弹射物。

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

## 旗帜图案 - 1.21.5+

配置键：`component.banner-patterns`。

每个列表条目写作 `pattern registry ID: DYE_COLOR`，图案按配置顺序应用。

```yaml
component:
  banner-patterns:
    - 'minecraft:stripe_bottom: RED'
    - 'minecraft:cross: BLUE'
    - 'minecraft:border: BLACK'
```

## 药水内容 - 1.21.5+

配置键：`component.potion`。

`base-effect` 使用 `PotionType` 枚举值。自定义效果写作 `effect ID,duration in ticks,amplifier,ambient,particles,icon`，效果 ID 带命名空间，增幅等级从 0 起算。

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

## 药水持续时间倍率 - 1.21.5+

配置键：`component.potion-duration-scale`。

对药水效果的持续时间做倍乘。请在带药水内容的物品上测试。

```yaml
component:
  potion-duration-scale: 2.0
```

## 可疑的炖菜效果 - 1.21.5+

配置键：`component.suspicious-stew-effects`。

每个条目只需 `effect ID,duration in ticks`。带附加字段的旧条目仍可读取，但多出的字段不会影响炖菜的效果。

```yaml
component:
  suspicious-stew-effects:
    - minecraft:night_vision,200
    - minecraft:saturation,20
```

## 烟花 - 1.21.5+

配置键：`component.firework`。

字段名是 `trail`，不是 `trial`。飞行时间 `duration` 位于 `firework` 下，与各编号效果条目同级，取值 0–255。

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

## 美西螈变种 - 1.21.5+

配置键：`component.axolotl-variant`。

用枚举值（例如 `BLUE` 或 `LUCY`）设置美西螈桶的变种。请用 `AXOLOTL_BUCKET` 测试。

```yaml
component:
  axolotl-variant: BLUE
```

## 热带鱼底色 - 1.21.5+

配置键：`component.tropical-fish-base-color`。

设置热带鱼桶的身体染色。请用 `TROPICAL_FISH_BUCKET` 测试。

```yaml
component:
  tropical-fish-base-color: ORANGE
```

## 热带鱼图案颜色 - 1.21.5+

配置键：`component.tropical-fish-pattern-color`。

设置热带鱼桶的图案染色。请用 `TROPICAL_FISH_BUCKET` 测试。

```yaml
component:
  tropical-fish-pattern-color: BLUE
```

## 热带鱼图案 - 1.21.5+

配置键：`component.tropical-fish-pattern`。

设置热带鱼桶的图案枚举，例如 `KOB`。

```yaml
component:
  tropical-fish-pattern: KOB
```

## 玩家档案 - 1.21.5+

配置键：`component.skull`。

在此 Paper 组件格式中，`skull`（或 `skull-meta`）是一个 base64 纹理属性，而不是按玩家名查找。纹理能否显示，还取决于客户端能否加载该纹理。请用 `PLAYER_HEAD` 测试。

```yaml
component:
  skull: eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2YzZmVkMTZmZDU1MTkwOWZhNWUyOWNkZDY5N2VlMzQ2ZTYzMzkwYjM4M2E0MzAwYTY2MmE4MGI2NGQ5ZWIxNyJ9fX0=
```

## 乐器 - 1.21.5+

配置键：`component.music`。

用带命名空间的 ID 设置已注册的乐器。请用 `GOAT_HORN` 测试。

```yaml
component:
  music: minecraft:ponder_goat_horn
```

## 唱片机可播放 - 1.21.5+

配置键：`component.song`。

用带命名空间的 ID 设置已注册的唱片机曲目。把该物品放进唱片机即可测试。

```yaml
component:
  song: minecraft:cat
```

## 不祥之瓶增幅等级 - 1.21.5+

配置键：`component.ominous-bottle-amplifier`。

接受 0–4。请用 `OMINOUS_BOTTLE` 测试。

```yaml
component:
  ominous-bottle-amplifier: 2
```

## 物品模型 - 1.21.5+

配置键：`component.item-model`。

设置物品模型键（带命名空间）。自定义模型需要资源包中对应的资源，仅有这个键并不会创建模型。

```yaml
component:
  item-model: minecraft:diamond_sword
```

## 工具提示样式 - 1.21.5+

配置键：`component.tooltip-style`。

设置自定义工具提示样式键。拼写是 `tooltip-style`，不是 `tootip-style`。要改变它的外观，需要资源包中对应的资源。

```yaml
component:
  tooltip-style: minecraft:component_test
```

## 攻击范围 - 1.21.11+

配置键：`component.attack-range-key`。

配置键就是 **`attack-range-key`**。各 reach 值接受 0–64，`hitbox-margin` 接受 0–1，`mob-factor` 接受 0–2。

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

## 伤害类型 - 1.21.11+

配置键：`component.damage-type`。

用单个伤害类型注册表 ID 设置武器的伤害类型，而不是伤害类型标签。

```yaml
component:
  damage-type: minecraft:player_attack
```

## 动能武器 - 1.21.11+

配置键：`component.kinetic-weapon`。

配置动能武器的接触冷却、延迟、位移、伤害倍率、声音与条件。条件中的持续时间以游戏刻计。测试时请与其它会改变同一攻击交互的组件分开进行。

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

## 最小攻击蓄力 - 1.21.11+

配置键：`component.minimum-attack-charge`。

接受 0 到 1 之间的值，作为最小攻击蓄力。

```yaml
component:
  minimum-attack-charge: 0.65
```

## 穿刺武器 - 1.21.11+

配置键：`component.piercing-weapon`。

配置穿刺武器的击退、下马行为与声音。

```yaml
component:
  piercing-weapon:
    deals-knockback: true
    dismounts: true
    sound: minecraft:entity.player.attack.sweep
    hit-sound: minecraft:entity.player.attack.strong
```

## 挥舞动画 - 1.21.11+

配置键：`component.swing-animation`。

配置旧版的挥舞动画。在 Paper 1.21.11 至 26.2 上，它提供挥舞组件；在 Paper 26.3+ 上，它同时提供攻击与交互动画，除非显式写了 `attack-animation` 或 `interact-animation` 部分，各自覆盖对应的一侧。

```yaml
component:
  swing-animation:
    type: NONE
    duration: 7
```

## 使用效果 - 1.21.11+

配置键：`component.use-effects`。

配置使用物品期间的疾跑、交互振动与移动速度。`speed-multiplier` 接受 0–1。请用带使用时长的可使用物品测试，例如配置了 `food` 与 `consumable` 的苹果。

```yaml
component:
  use-effects:
    can-sprint: true
    interact-vibrations: false
    speed-multiplier: 0.4
```

## 硫立方体内容 - 26.2+

配置键：`component.sulfur-cube-content`。

内容是一个完整的嵌套 ItemFormat。

```yaml
component:
  sulfur-cube-content:
    material: DIAMOND
    amount: 1
```

## 攻击动画 - 26.3+

配置键：`component.attack-animation`。

单独设置攻击动画。在攻击行为上优先于 `swing-animation`。

```yaml
component:
  attack-animation:
    type: NONE
    duration: 9
```

## 交互动画 - 26.3+

配置键：`component.interact-animation`。

单独设置交互动画。在交互行为上优先于 `swing-animation`。

```yaml
component:
  interact-animation:
    type: NONE
    duration: 5
```

## 告示牌正面文本 - 26.3+

配置键：`component.sign-text-front`。

配置告示牌的正面。可按需设置四行文本、染色与 `glowing`。放置一个 `OAK_SIGN` 物品即可测试。

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

## 告示牌背面文本 - 26.3+

配置键：`component.sign-text-back`。

配置告示牌的背面。可按需设置四行文本、染色与 `glowing`。放置一个 `OAK_SIGN` 物品即可测试。

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

## 打蜡 - 26.3+

配置键：`component.waxed`。

设为 `true` 可为告示牌物品添加打蜡状态。检查放置后的告示牌是否已封蜡、无法编辑。

```yaml
component:
  waxed: true
```

## 坐垫颜色 - 26.3+

配置键：`component.cushion-color`。

用染料颜色枚举（例如 `RED`）设置坐垫的染色。请配合合适的材质与交互方式查看实际效果。

```yaml
component:
  cushion-color: RED
```

## 村民食物 - 26.3+

配置键：`component.villager-food`。

设置物品的村民食物值。请用合适的物品材质测试对应的村民交互。

```yaml
component:
  villager-food: 4
```

## 提供陶器图案 - 26.3+

配置键：`component.provides-pottery-pattern`。

用带命名空间的注册表 ID 设置饰纹陶罐的图案，该图案必须存在于服务端。

```yaml
component:
  provides-pottery-pattern: minecraft:angler
```

## 生物可见性 - 26.3+

配置键：`component.mob-visibility`。

对列出的实体类型应用可见性系数。`visibility` 接受 0–10，实体 ID 必须存在于服务端注册表中。

```yaml
component:
  mob-visibility:
    entities:
      - minecraft:zombie
      - minecraft:skeleton
    visibility: 0.5
```

## 保存与测试

在 `config.yml` 中设置 `debuild-item-method: COMPONENT` 并重载插件，即可把受支持的物品属性保存到 `component` 部分。该设置不会改变 Minecraft 存储物品本身的方式。

文件安装方式、发放命令、保存后重建的校验方法以及各版本示例，请继续阅读[测试 Paper 组件](component-testing.md)。
