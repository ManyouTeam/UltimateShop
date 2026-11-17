# 💹价格修改器

{% hint style="danger" %}
该功能以早期实验功能的形式提供，将在 5.0.0 版本之后正式上线。

该功能仍在测试中，正式发布前可能还会调整。
{% endhint %}

价格修改器让 UltimateShop 依据**实际被出售的物品**的属性，调整商品的出售收益。

例如，你可以：

* 降低已损耗物品的出售价格。
* 从物品描述中读取数值，并用它作为出售价格。
* 从 NBT 数据中读取数值，并用它作为出售价格。
* 按 MythicChanger 的 `match-item` 规则套用不同的乘数。

价格修改器在全局配置中定义：

```yaml
sell:
  price-modifier:
```

不过，只有在商品中启用下面这个选项后，该商品才会用上这些修改器：

```yaml
price-modifier: true
```

商品各项选项的说明见[商品](products.md#general-options)。

***

## 物品匹配与价格修改器

{% hint style="warning" %}
**物品匹配和价格修改器是两个不同的系统。**

物品匹配决定：

> **这件物品能不能作为该商品出售？**

价格修改器决定：

> **这件实际物品值多少钱？**
{% endhint %}

简化后的出售流程如下：

```
Actual item in player inventory
        ↓
Find a matching product
        ↓
Check whether the item matches the product
        ↓
Calculate normal sell price
        ↓
Apply conditional sell multiplier
        ↓
Apply Price Modifier using the actual ItemStack
        ↓
Round the calculated result
        ↓
Give the final sell reward
```

当价格修改器依赖的物品数据会随物品变化时，这个区别尤其重要。

举例来说，某个商店收购并出售钻石剑。

商品配置里的物品是：

```
Diamond Sword
Damage: 0
```

而玩家拿来出售的是：

```
Diamond Sword
Damage: 500
```

如果这两件物品过不了出售匹配检查，那把受损的剑根本走不到耐久价格修改器这一步。

价格修改器**不会**自动让损坏程度、名称、附魔或其它改动不同的物品匹配到同一个商品。

***

### Bukkit 与 ItemFormat 两种匹配方式

UltimateShop 支持两种全局物品匹配方式：

```yaml
sell:
  sell-method: Bukkit
```

以及：

```yaml
sell:
  sell-method: ItemFormat
```

使用 `Bukkit` 时，UltimateShop 采用 Bukkit 自身的物品相似度检查。耐久、物品描述、附魔、名称、组件或 NBT 等数据的差异，都可能让物品不再匹配配置的商品。

`ItemFormat` 的可控性更强，因为匹配时可以忽略指定的物品属性。

示例：

```yaml
sell:
  sell-method: ItemFormat

  item-format:
    require-same-key: false
    ignore-key:
      - 'damage'
```

在上面的示例中，耐久不同不会导致物品匹配失败。

{% hint style="info" %}
在 `item-format.ignore-key` 中忽略某个属性，只影响**物品匹配**。

它**不会**把这条信息从实际物品上删掉。

例如即使匹配时忽略了 `damage`，耐久价格修改器依然能读到被出售物品的真实损伤值。
{% endhint %}

物品匹配的更多说明见[自定义物品匹配方式](../features/custom-item-match-method.md)。

你也可以手持物品执行：

```
/shop generateitemformat
```

查看它的 ItemFormat 数据，据此确认正确的键路径。

***

## 基本配置

完整示例如下：

```yaml
sell:
  # Item matching method.
  #
  # ItemFormat is recommended when a Price Modifier depends on item
  # properties that are expected to differ between otherwise sellable items.
  sell-method: ItemFormat

  item-format:
    require-same-key: false
    ignore-key:
      # Allow different durability values to match the same product.
      - 'damage'

  # Global Price Modifier configuration.
  price-modifier:

    item-sell-menu:
      # Products with `price-modifier: true` open this menu when
      # the player chooses a sell action.
      enabled: true
      menu: item-sell

    durability:
      type: durability
      enabled: true

      # Losing 1% durability deducts 1% from the price.
      deduction-coefficient: 1

      # Final modifier cannot become lower than this value.
      minimum-multiplier: 0.1

      # You can also set a minimum numeric price.
      # minimum-price: 1

    lore:
      type: lore
      enabled: false

      # SET or MULTIPLY.
      operation: SET

      # Example:
      # Item Value: 500
      pattern: 'Item Value[：:]\s*([+-]?(?:\d+(?:\.\d+)?|\.\d+))'

      value-group: 1
      strip-color: true
      case-sensitive: true

      minimum-value: 0
      maximum-value: 1000000
      maximum-number-length: 64

    nbt:
      type: nbt
      enabled: false

      # Requires NBTAPI.
      key: item_value

      # AUTO, BYTE, SHORT, INT, LONG, FLOAT, DOUBLE or STRING.
      value-type: AUTO

      # SET or MULTIPLY.
      operation: SET

      minimum-value: 0
      maximum-value: 1000000
      maximum-number-length: 64

    mythic-changer:
      type: match_item
      enabled: false

      # MAX, MIN or STACK.
      mode: STACK

      rules:
        named-item:
          operation: MULTIPLY
          value: 1.2

          match-item:
            has-name: true

        special-lore:
          operation: ADD
          value: 0.1

          match-item:
            contains-lore:
              - 'Special'
```

各修改器按配置中的先后顺序依次处理。

***

## 为商品启用价格修改器

商品默认不启用价格修改器。

在商品配置中加入下面一行即可启用：

```yaml
price-modifier: true
```

示例：

```yaml
items:
  diamond-sword:
    price-modifier: true

    products:
      1:
        material: DIAMOND_SWORD
        amount: 1

    sell-prices:
      1:
        economy-plugin: Vault
        amount: 100
        placeholder: '{amount} Coins'
```

这里配置的出售价格仍然是该商品的**基础出售价格**。

价格修改器会依据实际被出售的物品，在这个基础价上再做调整。

***

## 耐久修改器

耐久修改器按物品已损耗的耐久比例降低出售价格。

示例：

```yaml
sell:
  price-modifier:
    durability:
      type: durability
      enabled: true
      deduction-coefficient: 1
      minimum-multiplier: 0.1
```

基本公式为：

```
1 - (lost durability ratio × deduction coefficient)
```

例如：

```
Item durability lost: 30%
deduction-coefficient: 1

Multiplier:
1 - (0.30 × 1)
= 0.70
```

如果基础出售价格为：

```
100
```

调整后的价格就是：

```
100 × 0.70
= 70
```

不可损耗耐久的物品，乘数按 `1` 处理。

***

### minimum-multiplier

可以设一个下限，避免受损物品几乎卖不出价钱：

```yaml
minimum-multiplier: 0.1
```

例如即使耐久算出来是：

```
0.03
```

最终乘数仍会取：

```
0.1
```

***

### minimum-price

也可以直接指定最低价格：

```yaml
minimum-price: 10
```

这样算出的价格就不会低于该值。

`minimum-price` 和 `minimum-multiplier` 可以单独使用，也可以一起使用。

***

### 耐久相关的物品匹配

{% hint style="warning" %}
如果受损与完好的物品应当作为同一件商品出售，它们的损伤差异就不能妨碍物品匹配。
{% endhint %}

推荐配置：

```yaml
sell:
  sell-method: ItemFormat

  item-format:
    require-same-key: false
    ignore-key:
      - 'damage'
```

这样会得到下面的行为：

```
Shop item:
Diamond Sword
Damage: 0

Player item:
Diamond Sword
Damage: 500

        ↓

ItemFormat matching
damage is ignored

        ↓

Item matches product

        ↓

Durability Price Modifier
reads actual Damage: 500

        ↓

Sell price is reduced
```

`damage` 只在判断物品能否匹配商品时被忽略；修改器拿到的仍然是那件已受损的 ItemStack。

***

## 物品描述（Lore）数值修改器

Lore 修改器从物品描述中读取数值。

示例物品：

```
Legendary Sword
Damage: 25
Item Value: 500
```

配置：

```yaml
sell:
  price-modifier:
    lore-value:
      type: lore
      enabled: true

      operation: SET

      pattern: 'Item Value[：:]\s*([+-]?(?:\d+(?:\.\d+)?|\.\d+))'

      value-group: 1
      strip-color: true
      case-sensitive: true

      minimum-value: 0
      maximum-value: 1000000
      maximum-number-length: 64
```

以第一条匹配到的物品描述行为准。

***

### SET

具有：

```yaml
operation: SET
```

捕获到的值会成为每一个出售单位的价格。

例如：

```
Base sell price: 100
Lore:
Item Value: 500
```

结果：

```
Final unit price: 500
```

一次卖出多个单位时，UltimateShop 会让结果与逐个出售保持一致。

***

### MULTIPLY

具有：

```yaml
operation: MULTIPLY
```

捕获的值乘以当前价格。

例如：

```
Current price: 100

Lore:
Price Multiplier: 1.5
```

结果：

```
100 × 1.5
= 150
```

***

### 数字安全选项

下列选项用于拦掉无效或离谱的数值：

```yaml
minimum-value: 0
maximum-value: 1000000
maximum-number-length: 64
```

超出设定范围的数值会被忽略。

长度超过 `maximum-number-length` 的数值同样会被忽略。

数值缺失、无效或未匹配时，当前价格保持不变。

不支持的操作会作为配置错误上报，对应的修改器被跳过。

***

### Lore 相关的物品匹配

如果修改器读取的物品描述在各物品之间会有差异，这个差异就不能妨碍物品匹配商品。

例如：

```
Sword A:
Item Value: 100

Sword B:
Item Value: 500
```

如果两者都要作为同一件商品出售，可以这样配置：

```yaml
sell:
  sell-method: ItemFormat

  item-format:
    require-same-key: false
    ignore-key:
      - 'lore'
```

{% hint style="info" %}
匹配时忽略 `lore`，并不会影响 Lore 价格修改器读取被售物品的真实描述。
{% endhint %}

但忽略整个 `lore` 属性，意味着出售匹配时会一并忽略**所有**描述差异。

只有当该商品能容忍这些描述差异时才这么做。

需要更精确的匹配时，建议改用商品级的 `match-item` 规则。

***

## NBT 数值修改器

NBT 修改器从物品的 NBT 数据中读取数值。

{% hint style="warning" %}
此修改器需要安装 **NBTAPI** 插件。
{% endhint %}

示例：

```yaml
sell:
  price-modifier:
    nbt-value:
      type: nbt
      enabled: true

      key: item_value
      value-type: AUTO

      operation: SET

      minimum-value: 0
      maximum-value: 1000000
      maximum-number-length: 64
```

支持嵌套的 NBT 路径：

```yaml
key: custom.price
```

***

### value-type

可选值：

```
AUTO
BYTE
SHORT
INT
LONG
FLOAT
DOUBLE
STRING
```

`AUTO` 会自动尝试取出可用的数值。

数字形式的字符串同样会被接受。

***

### SET 和 MULTIPLY

NBT 修改器支持与 Lore 修改器相同的操作。

#### SET

```yaml
operation: SET
```

以 NBT 值作为单价。

#### MULTIPLY

```yaml
operation: MULTIPLY
```

用 NBT 值乘以当前价格。

***

### 无效值

出现下列情况时，修改器会保持当前价格不变：

* 不存在。
* 无效。
* 不是数字。
* 不是有限值。
* 落在 `minimum-value` 与 `maximum-value` 之外。
* 长度超过 `maximum-number-length`。

***

### NBT 相关的物品匹配

如果作为动态价格的 NBT 值在各物品之间不同，ItemFormat 匹配时可能也需要把它忽略掉。

例如先检查物品：

```
/shop generateitemformat
```

可能会看到类似这样的键：

```
nbt.string.item_value
```

于是可以这样配置：

```yaml
sell:
  sell-method: ItemFormat

  item-format:
    require-same-key: false
    ignore-key:
      - 'nbt.string.item_value'
```

{% hint style="warning" %}
只要条件允许，就只忽略那个会变化的 NBT 键。

除非确实想忽略全部 NBT 差异，否则不要忽略整个 `nbt` 段。
{% endhint %}

例如：

```yaml
ignore-key:
  - 'nbt'
```

这会让大量本来不同的物品被当成同一件商品。

用 `/shop generateitemformat` 查出你的物品可用的最具体的 ItemFormat 键。

***

## 物品匹配修改器（match_item）

`match_item` 修改器使用 MythicChanger 的 `match-item` 规则。

{% hint style="warning" %}
此修改器需要安装 **MythicChanger**。

MythicChanger 不可用时，该修改器不生效。
{% endhint %}

示例：

```yaml
sell:
  price-modifier:
    mythic-changer:
      type: match_item
      enabled: true

      mode: STACK

      rules:
        named-item:
          operation: MULTIPLY
          value: 1.2

          match-item:
            has-name: true

        special-lore:
          operation: ADD
          value: 0.1

          match-item:
            contains-lore:
              - 'Special'
```

每条规则包含：

```yaml
operation:
value:
match-item:
```

`match-item` 部分沿用 MythicChanger 的物品匹配系统。

***

### MULTIPLY

```yaml
operation: MULTIPLY
value: 1.2
```

得到的乘数为：

```
1.2
```

也就是当前价格的：

```
120%
```

***

### ADD

```yaml
operation: ADD
value: 0.1
```

把该值加到乘数上。

例如：

```
1 + 0.1
= 1.1
```

也就是当前价格的：

```
110%
```

只要表达式合法，结果即使为 `0` 也照常接受。

无效值或不支持的操作会经由 UltimateShop 的错误管理器上报，受影响的规则被跳过。

***

## 多条匹配规则的组合

`mode` 决定多条规则同时命中时的处理方式。

可选模式：

```
MAX
HIGHEST
MIN
LOWEST
STACK
```

***

### MAX / HIGHEST

取所有结果中最高的乘数。

例如：

```
Rule A: 1.2
Rule B: 1.5
```

结果：

```
1.5
```

***

### MIN / LOWEST

取所有结果中最低的乘数。

例如：

```
Rule A: 1.2
Rule B: 1.5
```

结果：

```
1.2
```

***

### STACK

按配置顺序依次处理命中的规则。

`MULTIPLY` 的数值会乘到累积乘数上。

`ADD` 的数值会加到累积乘数上。

例如：

```
Initial multiplier:
1

Rule A:
MULTIPLY 1.5

1 × 1.5
= 1.5

Rule B:
ADD 0.2

1.5 + 0.2
= 1.7
```

最终乘数：

```
1.7
```

***

## 多个价格修改器并存

`sell.price-modifier` 下所有已启用的修改器：

```yaml
sell:
  price-modifier:
```

都会按配置顺序依次应用。

例如：

```yaml
price-modifier:

  durability:
    type: durability
    enabled: true

  lore:
    type: lore
    enabled: true
```

耐久修改器会先于 Lore 修改器处理。

前一个修改器的结果，会成为后一个修改器所用的当前价格。

因此调整配置顺序，最终结果也可能随之改变。

***

## 出售流程

启用了：

```yaml
price-modifier: true
```

的商品，必须能拿到实际被出售的 ItemStack。

因此，拿不到实际物品堆叠的出售流程，不会直接显示或执行常规出售价格。

当玩家选择：

```
sell
```

或：

```
sell-all
```

UltimateShop 会打开这里配置的物品出售菜单：

```yaml
sell:
  price-modifier:
    item-sell-menu:
      enabled: true
      menu: item-sell
```

购买操作、数量选择操作和自定义操作不受此行为影响。

Bedrock 表单菜单和 Dialog 商店列表会先打开商品信息界面，其中的购买与出售仍是两个独立操作。

启用了价格修改器的商品，即使商品中配置了：

```yaml
sell-all: false
```

也仍然可以通过物品出售菜单出售。

***

## 价格计算顺序

简化后的数值计算顺序：

```
Base sell price
        ↓
Conditional sell multiplier
        ↓
Price Modifier chain
        ↓
Configured numeric precision
        ↓
Final reward
```

例如：

```
Base price:
100

Conditional sell multiplier:
1.2

After conditional multiplier:
120

Durability modifier:
0.5

Final:
120 × 0.5
= 60
```

***

## 精度与舍入

施加乘数的过程按数学计算处理。

UltimateShop 对计算结果采用：

```
HALF_UP
```

并按下面的设置保留小数位：

```yaml
math:
  scale:
```

该规则适用于：

* 价格修改器。
* 全局出售倍率。
* 价格预览。
* 最终出售收益。

详见[数学计算格式](../format/math-calculate-format.md#result-precision)。

***

## 交易事件行为

实际交易中，事件结束后 `GiveResult` 里记录的出售价格才是最终权威。

如果其他插件通过下面的事件修改了出售价格：

```
ItemPreTransactionEvent
```

UltimateShop 会保留修改后的结果。

当多个实际 ItemStack 需要各自的修改器结果时，重新计算出的单价只用于在施加物品相关修改器之前，决定已确定的总金额如何在这些堆叠之间分配。

这样可以避免交易事件中的价格改动被第二次常规价格计算悄悄覆盖。

***

## 推荐配置

### 基于耐久的定价

如果唯一的动态属性是耐久：

```yaml
sell:
  sell-method: ItemFormat

  item-format:
    require-same-key: false
    ignore-key:
      - 'damage'

  price-modifier:
    durability:
      type: durability
      enabled: true
      deduction-coefficient: 1
      minimum-multiplier: 0.1
```

商品配置：

```yaml
price-modifier: true
```

***

### 基于物品描述的定价

如果存放在物品描述里的价格会变化：

```yaml
sell:
  sell-method: ItemFormat

  item-format:
    require-same-key: false
    ignore-key:
      - 'lore'

  price-modifier:
    lore-value:
      type: lore
      enabled: true
      operation: SET
      pattern: 'Item Value[：:]\s*([+-]?(?:\d+(?:\.\d+)?|\.\d+))'
      value-group: 1
      minimum-value: 0
      maximum-value: 1000000
```

商品配置：

```yaml
price-modifier: true
```

***

### 基于 NBT 的定价

先检查物品：

```
/shop generateitemformat
```

如有需要，再只忽略那个会变化的 ItemFormat 键：

```yaml
sell:
  sell-method: ItemFormat

  item-format:
    require-same-key: false
    ignore-key:
      - 'nbt.string.item_value'

  price-modifier:
    nbt-value:
      type: nbt
      enabled: true
      key: item_value
      value-type: AUTO
      operation: SET
      minimum-value: 0
      maximum-value: 1000000
```

商品配置：

```yaml
price-modifier: true
```

***

## 常见问题

### 受损物品卖不出去

如果你用的是：

```yaml
type: durability
```

但受损物品卖不出去，先检查全局的出售匹配方式。

使用：

```yaml
sell-method: Bukkit
```

不同的损伤值可能就匹配不上物品。

可以改用：

```yaml
sell-method: ItemFormat
```

具有：

```yaml
ignore-key:
  - 'damage'
```

***

### Lore 修改器没有生效

如果物品的描述与商品里的物品不同，它可能在进入修改器之前就被判为不匹配。

如果这个描述差异是有意为之，就要相应调整物品匹配配置。

***

### NBT 修改器没有生效

请分别排查两件事：

1. 实际物品能否匹配该商品？
2. NBT 修改器能否找到配置的 NBT 键？

可以用：

```
/shop generateitemformat
```

查看 ItemFormat 匹配数据。

价格修改器使用的 NBT 键与 `ignore-key` 里使用的 ItemFormat 键指向的是相关数据，但两者用途不同。

***

### 价格修改器完全没有效果

确认商品中包含：

```yaml
price-modifier: true
```

同时确认对应的全局修改器包含：

```yaml
enabled: true
```

例如：

```yaml
sell:
  price-modifier:
    durability:
      type: durability
      enabled: true
```

***

### 不同物品被误判为同一商品

检查：

```yaml
sell:
  item-format:
    ignore-key:
```

不要忽略不必要的物品数据。

例如：

```yaml
ignore-key:
  - 'nbt'
```

比下面这种范围大得多：

```yaml
ignore-key:
  - 'nbt.string.item_value'
```

尽量使用最具体的键。

***

## 价格修改器 API

全局的物品相关价格修改器由 `cn.superiormc.ultimateshop.managers` 包中的 `PriceModifierManager` 管理。它通过既有的 `ConfigManager` 读取 `sell.price-modifier`，并在插件重载时重建配置好的修改器链；自定义工厂在 `/shop reload` 之后依然保持注册。

自定义实现只需实现 `PriceModifier` 并注册对应工厂：

```java
import cn.superiormc.ultimateshop.managers.PriceModifierManager;

PriceModifierManager.priceModifierManager.registerNewPriceModifier(
        "my-modifier",
        MyPriceModifier::new
);
```

请在 UltimateShop 启用之后注册，并使用唯一的类型 ID。与动作管理器一致，注册已存在的 ID 不会改变原有类型；注册新类型会重建修改器链，因此配置中使用该类型的条目会立即生效。

每个修改器会收到：

* 玩家。
* 实际被出售的 `ItemStack`。
* 当前的数值价格。

并返回一个非负的乘数。

四参数重载还会额外传入本次交易的单位数量，供 `SET` 这类需要考虑数量的修改器使用。

配置在：

```yaml
sell:
  price-modifier:
```

下的修改器按配置顺序应用。

注册新工厂，或用相同类型替换现有工厂，都会立即重建价格修改器链。

无需重载服务器。
