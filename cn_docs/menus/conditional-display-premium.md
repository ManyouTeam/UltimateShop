# 🔀条件显示 - 付费版

通过 `display-conditions` 决定按钮或商品能否出现。如果布局选择取决于玩家，请启用 `dynamic-layout: true`。点击与交易条件仍各自独立验证操作。

## 在两个条目之间选择

以下完整通用菜单向有权限的玩家显示 VIP 按钮，向其他玩家显示普通按钮：

```yaml
title: '&8Choose a shop'
size: 9
dynamic-layout: true
layout:
  - '000`A||B`00000'
dialog:
  enabled: false
bedrock:
  enabled: false
arcmenu:
  enabled: false
buttons:
  A:
    display-item:
      material: DIAMOND
      name: '&bVIP Shop'
    display-conditions:
      1:
        type: permission
        permission: shop.vip
    actions:
      1:
        type: shop_menu
        shop: vip
  B:
    display-item:
      material: EMERALD
      name: '&aRegular Shop'
    actions:
      1:
        type: shop_menu
        shop: normal
```

保存到 `menus/common_menus/`，创建目标商店并重载。标记 `` `A||B` `` 占一个槽位。`A` 的可见性和显示条件通过时选择 `A`，否则检查 `B`。如果第一个候选项没有条件，后续备选项就不会被采用；没有合格候选项时留空。

全局强制呈现方式仍适用于此示例。Ore/ArcMenu 条目还需合适的图形位置。

## 动态布局规则

- 在同一个反引号布局标记内，用 `||` 分隔备选项。
- 候选 ID 区分大小写，首尾空格会被移除。
- 候选项必须存在，并允许在当前呈现方式中显示。
- 商店布局按相同规则选择商品 ID 或普通按钮。
- 启用动态布局后，PlaceholderAPI 可以按玩家解析布局标记。

静态布局会缓存，无法可靠地按玩家选择备选项。`menu-visibility` 独立计算，不需要动态布局。

## 选择条件作用范围

| 设置 | 控制内容 |
| --- | --- |
| 菜单根级 `conditions` | 打开整个菜单 |
| 按钮 `conditions` / `fail-actions` | 点击资格与失败响应 |
| 按钮/商品 `display-conditions` | 条目是否显示 |
| 商品购买/出售条件 | 实际交易资格 |
| `menu-visibility` | 允许显示该条目的呈现方式 |
| 条件展示物品变体 | 使用哪个物品外观 |
| 附加描述条件符号 | 显示哪些生成的描述行 |

隐藏按钮不能代替交易权限或购买验证。条件语法见[条件格式](../format/condition-format.md)，外观变体见[展示物品格式](../format/display-item-format.md)，条件文本见[附加描述](display-item-add-lore.md)。

## 图形备选项

Ore 和 ArcMenu 仍需为最终选中条目配置位置。替换按钮应提供兼容的 `ui.placement`。显式定位的条目也可能在根布局之外被收集，因此其自身显示条件与可见性必须明确表达何时出现。

[布局与按钮](layout-and-buttons.md) · [图形 UI 布局](../menus-getting-started/ui-layout.md)
