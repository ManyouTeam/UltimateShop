# 测试 Paper 组件

请使用一台运行目标 Paper 版本并安装 UltimateShop 付费版的服务器进行测试。配置能够正常解析、插件能够成功构建，并不等于游戏内行为已经验证通过。物品数据、实际显示效果以及保存/重建的完整流程，需要分别检查。

## 选择与版本匹配的 ItemFormat

以下每个示例都是一份完整的 ItemFormat。下载或复制其原始 YAML 内容，保存为 `plugins/UltimateShop/items/pct-all.yml`：

| 服务器版本 | 示例 |
| --- | --- |
| Paper 1.21.5 | [1.21.5 组件测试](examples/paper-1.21.5-components.yml) |
| Paper 1.21.6 | [1.21.6 组件测试](examples/paper-1.21.6-components.yml) |
| Paper 1.21.11 | [1.21.11 组件测试](examples/paper-1.21.11-components.yml) |
| Paper 26.1 | [26.1 组件测试](examples/paper-26.1-components.yml) |
| Paper 26.2 | [26.2 组件测试](examples/paper-26.2-components.yml) |
| Paper 26.3 | [26.3 组件测试](examples/paper-26.3-components.yml) |

这些示例把大量受支持的组件集中到同一个物品上，便于检查数据。它们有意省略了头颅档案（head profile）、嵌套的捆绑包/弹射物/剩余物测试以及部分工具提示选项，以便将这些内容单独验证。示例覆盖的是插件支持的配置，而非 Minecraft 原生的每一个组件或每一种可能取值。

只安装与服务器版本匹配的那一个示例，并使用上述文件名。在较低版本的服务器上加载较新的示例，不受支持的选项会被直接跳过而不生效，切勿把这种情况当成这些组件测试通过。

重载插件并给自己发放该物品：

```text
/shop reload
/shop givesaveitem pct-all
```

若从控制台执行，需要额外指定一名在线玩家的名称：

```text
shop givesaveitem pct-all PlayerName 1
```

发放命令需要 `ultimateshop.givesaveitem` 权限。物品 ID 就是去掉 `.yml` 后缀的文件名，命令中不要带上 `items/` 路径。

## 检查生成的数据

将物品拿在主手，执行：

```text
/data get entity @s SelectedItem.components
```

该命令需要以拥有原版 `data` 命令权限的玩家身份执行。原生组件名使用下划线，而插件的 YAML 配置通常使用连字符。例如 `custom-model-data` 最终会写入 `minecraft:custom_model_data`。

要核对具体组件的取值，而不只是确认物品是否存在。某些组件即使已经正确写入，在示例所用的 `DIAMOND_SWORD` 材质上也未必能看出可见效果。

这种集中式的示例并不适合一次性验证所有交互行为。穿戴、格挡和进食会争抢同一个使用动作。示例中还设置了 `unbreakable: true`，因此不能用来测试正常的耐久度消耗。做行为测试时，请另建一个物品，选用合适的材质，只保留待测组件及其前置条件。

## 保存并重建物品

在 `config.yml` 中设置：

```yaml
debuild-item-method: COMPONENT
```

重载插件，手持测试物品并执行：

```text
/shop generateitemformat
```

该命令会生成 `plugins/UltimateShop/generated-item-format.yml`，需要 `ultimateshop.generateitemformat` 权限，并且每次执行都会覆盖同一个文件。因此在测试下一个物品之前，请先把本次结果复制保存下来。

将生成的文件复制为 `plugins/UltimateShop/items/pct-roundtrip.yml`，然后执行：

```text
/shop reload
/shop givesaveitem pct-roundtrip
```

手持重建后的物品，再次查看 `SelectedItem.components`，对比组件取值、数量和行为。YAML 键的顺序、显式写出的默认值以及生成的玩家档案 UUID 都可能有所不同，但这并不改变物品的预期属性。

也可以直接保存到 `items` 文件夹：

```text
/shop saveitem pct-roundtrip itemformat
/shop givesaveitem pct-roundtrip
```

保存操作需要 `ultimateshop.saveitem` 权限。`itemformat` 保存方式会遵循 `debuild-item-method` 的设置；`bukkit` 保存方式走的是另一套物品序列化流程，不会经过由组件写出 ItemFormat 的逻辑。

## 重点检查

| 测试 | 预期结果 |
| --- | --- |
| 自定义模型数据 | 每个配置的浮点值、标志、字符串和颜色各出现一次，并在保存、重建之后保持原有顺序 |
| 消耗品效果 | 每个带编号的效果只会添加其配置的类型；效果的持续时间、等级、粒子和图标标志在保存/重建后依然保留 |
| 可疑的炖菜 | `effect ID,duration` 形式的条目无需第三个字段即可生效 |
| 格挡 | 保存后的 `block-delay-seconds` 仍为数字类型，且不受 `block-sound` 影响 |
| 伤害抗性，Paper 26.1+ | 标签字符串与逐个伤害 ID 组成的列表，都能各自保持原有格式 |
| 属性，Paper 1.21.6+ | 显式指定 `RESET`、`HIDDEN` 和 `OVERRIDE` 模式时，工具提示的显示符合预期 |
| 武器组件，Paper 1.21.11+ | 检查配置的攻击距离、蓄力、动能/穿刺武器和使用效果数据；各项行为分别测试 |
| 动画兼容性，Paper 26.3 | `swing-animation` 同时提供两种原生动画；显式写出的 attack/interact 配置只覆盖各自对应的那一侧 |
| 告示牌，Paper 26.3 | 放置后的告示牌保留配置的正面/背面文本、染料颜色、发光状态和打蜡状态 |

测试消耗品效果时，请使用 [消耗品 - 1.21.5+](component-format.md#consumables) 中的完整苹果示例。它应当施加 200 游戏刻的速度 I，并播放升级音效；除非确实配置了对应类型，否则不应同时传送玩家或移除效果。请在生存模式下测试。

## 嵌套物品回归示例

将每个示例分别保存为独立的 `items/<ID>.yml` 文件，再用 `/shop givesaveitem <ID>` 获取对应物品，然后对每个物品执行一遍保存/重建流程。

### 捆绑包内容

保存为 `pct-bundle.yml`：

```yaml
material: BUNDLE
amount: 1

component:
  bundle-contents:
    '1':
      material: DIAMOND
      amount: 2
      component:
        name: '<aqua>Diamond inside bundle'
    '2':
      material: EMERALD
      amount: 3
```

捆绑包中应当装有 2 颗钻石和 3 颗绿宝石，且钻石保留其自定义名称。嵌套条目不得变成石头，也不能丢失数量。

### 已装填的弩

保存为 `pct-projectile.yml`：

```yaml
material: CROSSBOW
amount: 1

component:
  charged-projectiles:
    '1':
      material: ARROW
      amount: 1
```

弩中应当装有一支箭，并且在重建后仍保持已装填状态。射击测试请与保存后的数据检查分开进行。

### 消耗后返还的物品

保存为 `pct-remainder.yml`：

```yaml
material: APPLE
amount: 1

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
      name: '<yellow>Bottle returned after use'
```

在生存模式下吃掉苹果，应当返还一个带自定义名称的玻璃瓶。将未使用的苹果保存并重建后，它仍应返还同样的物品。保存下来的 `use-reminder` 中必须直接包含嵌套的 ItemFormat，不能再额外套一层 `item:`。

## 资源包与问题反馈

自定义模型数据、自定义物品模型和自定义工具提示样式都需要资源包中对应的素材才能真正改变外观。缺少视觉素材并不能证明组件写入失败。请使用测试服务器上确实存在的注册表 ID，包括该服务器加载的任何数据包所提供的 ID。

反馈问题时，请附上准确的 Paper 构建号、UltimateShop 版本、原始 ItemFormat、生成的 ItemFormat、相关物品的组件数据以及控制台中出现的任何异常，并说明问题发生在发放、使用、保存还是重建物品的环节。如果只是解析了配置文件，就不要声称六个版本都已测试。
