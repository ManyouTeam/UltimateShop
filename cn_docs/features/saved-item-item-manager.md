# 💾保存物品（物品管理器）

{% hint style="info" %}
**3.8.4** 之前存在一个问题：某些情况下，需要**重载**插件才能加载已保存的物品。
{% endhint %}

## 创建物品

在插件的 `items` 目录中创建 **yml** 文件，按照 [ItemFormat](../format/itemformat-tm/) 填写内容，文件名即为物品 ID。

创建或修改 ItemFormat 文件后，执行 `/shop reload`，再在游戏中使用 `/shop givesaveitem <itemID>`。控制台用法为 `shop givesaveitem <itemID> <player> [amount]`，所需权限为 `ultimateshop.givesaveitem`。

Paper 1.21.5+ 配合 UltimateShop 付费版时，可以在这些文件中嵌套 Paper `component` 配置。完整的分版本示例见[组件格式](../format/itemformat-tm/component-format.md)和[组件测试指南](../format/itemformat-tm/component-testing.md)。

## 保存物品

使用 `/shop saveitem <saveItemID> <saveItemMethod>` 保存手持物品，支持两种方式：

* `Bukkit`
  * 使用 Spigot 版 UltimateShop 时，通过 Bukkit API 保存物品，只支持原版数据和 Bukkit API 存储的持久化数据，无法保存其他插件的自定义 NBT。
  * 使用 Paper 版 UltimateShop 时，通过 Paper API 保存，可完整保留物品数据。**（仅 Paper 1.17.1+）**
* `ItemFormat`：将物品解析为 [ItemFormat](../format/itemformat-tm/)，遵循 `config.yml` 的 `debuild-item-method`。设为 `COMPONENT` 时，在 Paper 1.21.5+ 与付费版中会将受支持属性写入 `component`；`LEGACY` 使用旧版写入方式。

Paper 版中使用 Bukkit 保存方式的物品文件示例：

```yaml
item: !!binary |-
  H4sIAAAAAAAA/21RzW4TMRCeJd0oWVB/1IIQXILEG/TGpSDxCKhXa2LPbkxsz8qepUlPPAonXoBn6nsw27TaViBZljzz/XmmAZjBy68oeE25eE4Ap3cLeOEdnEWfyGZs5ZPzGDm5GdSWhyQAMG+gsRx7TpSkNHA8gVtmRS7TINnLvSLUNTQFZcg4Fj7/Aqjg2GIyGG5wXwyhVJrj7SQScWeKoN2a4m9pNFw+9QicqdFqtRgj6QuOuoz7BRwJ7QQuvm18WenJhCHsV8lb+qCeHycFSnaDSaLGN13wevMPytk7qhp4819cmT2diVacT90YAhp4PTXsUISjSRhpSrcOAz2me/Vl1emMVuWGs1Pu+3+4kR0F43QrS5i3gVFKPRpd/dR7Ae8mwvdhS2vemT7gHteB4HzqsWzGnTp6th5hDjM4dRixI9NTNuvAdnvY0oWjFocgRvH6OVN6Inf1u22XUOchUHkY+vyeU+Bk0tXgiSo4s5wzWTEtZ+My96WqoT7o/Lm8VDr8BZMVpHJ0AgAA
```

Paper 版中使用 ItemFormat 保存方式的物品文件示例：

```yaml
material: DIAMOND
amount: 6
name: <blue>A good sword
lore:
  - <gray>This is really nice!
custom-model-data: 1
max-stack: 6
food:
  nutrition: 5
  saturation: 5.0
tool:
  damage-per-block: 5
  mining-speed: 1.3
  rules:
    - STONE, 1.4, true
song: minecraft:otherside
glow: true
enchants:
  mending: 1

```

## 使用已保存物品

可以在 [ItemFormat](../format/itemformat-tm/) 中使用保存的物品。`material` 默认填写原版物品 ID，也可以填写已保存物品的 ID，让插件直接读取保存结果，而不是重新生成该材质的物品。

```yaml
display-item:
  material: superior_sword # If saved item id is 'superior_sword'
```

已保存物品加载后会持续缓存在内存中，避免反复读取文件带来的性能开销。代价是保存的物品越多，占用的内存也会相应增加。
