# 🔀条件显示 - 付费版

通过 `display-conditions`，你可以控制某个按钮或商品是否展示给玩家；若需要根据玩家状态在同一槽位切换不同条目，请同时开启 `dynamic-layout: true`（注意：`display-conditions` 仅控制显示，点击与交易条件仍需各自独立校验）。

## 在两个条目之间动态切换

下面的通用菜单示例会向拥有 `shop.vip` 权限的玩家显示 VIP 商店按钮，向其他玩家显示普通商店按钮：

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

将文件保存至 `menus/common_menus/`、创建目标商店并重载即可生效。标记 `` `A||B` `` 在布局中只占一个槽位：运行时先检查 `A` 的可见性与显示条件，通过则显示 `A`，否则继续检查 `B`（如果第一个候选项没有条件，后续备选项永远不会被选中；若所有候选项都不满足条件，则该槽位留空）。

全局 `menu.force-ui` 同样作用于此示例；若在 Ore / ArcMenu 中使用，请确保各候选条目也配置了合适的图形 `ui.placement`。

## 动态布局规则

* 在同一个反引号布局标记内，使用 `||` 分隔多个备选条目 ID。
* 候选 ID 区分大小写，系统会自动忽略首尾空格。
* 候选项必须真实存在，且允许在当前呈现方式下显示（`menu-visibility`）。
* 商店布局同样适用此规则，可混合引用商品 ID 与普通按钮 ID。
* 开启 `dynamic-layout: true` 后，布局标记内的 PlaceholderAPI 占位符也会按玩家实时解析。

{% hint style="info" %}
静态布局（`dynamic-layout: false`）会被缓存，无法按玩家动态切换备选项；而 `menu-visibility` 会独立计算，无需开启 `dynamic-layout`。
{% endhint %}

## 各类条件的作用范围对比

| 配置项 | 控制范围 |
| --- | --- |
| 菜单根级 `conditions` | 是否允许打开整个菜单 |
| 按钮 `conditions` / `fail-actions` | 点击按钮时的资格校验与失败动作 |
| 按钮 / 商品 `display-conditions` | 该条目是否显示在菜单中 |
| 商品 `buy-conditions` / `sell-conditions` | 实际买卖交易的资格校验 |
| `menu-visibility` | 允许显示该条目的呈现方式 |
| 条件展示物品变体 | 物品本身使用哪套外观配置 |
| 附加描述条件符号 | 自动生成的 Lore 中显示哪些行 |

隐藏按钮不能替代权限或交易校验——请务必在操作或商品本身也配置好对应条件。条件语法见[条件格式](../format/condition-format.md)，展示变体见[展示物品格式](../format/display-item-format.md)，描述条件见[展示物品的附加描述](display-item-add-lore.md)。

## 图形界面中的备选项

在 Ore 与 ArcMenu 图形界面中，无论最终选中了 `A` 还是 `B`，**每个备选条目都必须配置好自己的 `ui.placement`**，否则被选中后也不会显示在图形界面上。

此外，只要一个条目配置了 `ui.placement`，即使它没有出现在根级 `layout` 中，也可能会被图形商店收集并显示出来。因此，建议同时配合 `display-conditions` 与 `menu-visibility` 明确限制它的显示时机。

[布局与按钮](layout-and-buttons.md) · [图形 UI 布局](../menus-getting-started/ui-layout.md)
