# 🧱布局与按钮

根级 `layout` 将条目 ID 分配到物品栏槽位。普通条目定义在 `buttons` 下；商店菜单还可以从商店文件中读取商品与按钮。Ore 和 ArcMenu 使用独立的[图形位置设置](../menus-getting-started/ui-layout.md)。

## 完整按钮菜单

保存为 `menus/common_menus/categories.yml`：

```yaml
title: '&8Categories'
size: 18
layout:
  - '000000000'
  - '000A0X000'
buttons:
  A:
    display-item:
      material: WHEAT
      name: '&aFarming'
    actions:
      1:
        type: shop_menu
        shop: farming
  X:
    display-item:
      material: BARRIER
      name: '&cClose'
    actions:
      1:
        type: close
```

创建 `farming` 商店，执行 `/shop reload`，再通过 `/shop menu categories` 打开。使用物品栏界面时，`A` 位于槽位 12，`X` 位于槽位 14，槽位从 0 开始编号。

## 槽位 ID

每个字符对应一个槽位，先从左到右，再从上到下读取。每行九个槽位，`size` 必须与总数一致，可选 `9`、`18`、`27`、`36`、`45`、`54`。

- ID 区分大小写，`A` 与 `a` 不同。
- 重复 ID 会在多个槽位显示同一条目。
- 未定义的 ID 留空。
- `0` 也是普通 ID。定义 `buttons.'0'` 后，所有对应槽位都会被填充。

例如，以下片段填满九个槽位：

```yaml
layout:
  - '000000000'
buttons:
  '0':
    display-item:
      material: GRAY_STAINED_GLASS_PANE
      name: ' '
    menu-visibility:
      form: false
      dialog: false
      ore: false
      arcmenu: false
```

看起来像数字的 YAML ID 应加引号。上例还展示了如何在列表式和图形界面中隐藏物品栏装饰物。

## 多字符 ID

在 `layout` 中用反引号包围多字符 ID，每个完整标记只占**一个**槽位：

```yaml
layout:
  - '0`food_buy``food_sell`000000'
buttons:
  food_buy:
    display-item:
      material: EMERALD
      name: '&aBuy food'
  food_sell:
    display-item:
      material: GOLD_INGOT
      name: '&eSell food'
```

此行九个槽位依次为 `0`、`food_buy`、`food_sell`，再加六个 `0`。反引号只是布局分隔符，不属于按钮配置键。示例条目只用于展示，添加动作后才可交互。

## 商店菜单的条目来源

模板布局可以引用：

| 来源 | 位置 |
| --- | --- |
| 商品 | 商店 `items.<id>` |
| 商店专用普通按钮 | 商店 `buttons.<id>` |
| 可复用普通按钮 | 模板 `buttons.<id>` |

不同来源应使用不同 ID。若发生冲突，会先检查可用的商店复制条目，再依次检查商店按钮、商店商品和模板按钮，使用第一个符合条件的条目。

普通按钮执行自身 `actions`。商品条目使用商品交易操作及 `buy`、`sell`、`select-amount` 等点击绑定。导航按钮即使使用与商品相同的展示材质，也不会变成交易条目。

## 按钮动作与条件

| 配置键 | 用途 |
| --- | --- |
| `display-item` | 物品外观，见[展示物品格式](../format/display-item-format.md) |
| `actions` | 点击条件通过后执行的动作 |
| `conditions` | 点击时检查的条件 |
| `fail-actions` | 点击条件失败时执行的动作 |
| `display-conditions` | 是否允许显示，付费版功能 |
| `menu-visibility` | 按呈现方式控制可见性 |
| `dialog.layout`、`dialog.show-sprite` | 原生对话框条目外观 |
| `ui` | Ore/ArcMenu 外观与位置 |

以下按钮始终显示，但点击时会检查权限：

```yaml
buttons:
  A:
    display-item:
      material: DIAMOND
      name: '&bVIP Shop'
    conditions:
      1:
        type: permission
        permission: shop.vip
    actions:
      1:
        type: shop_menu
        shop: vip
    fail-actions:
      1:
        type: message
        message: '&cYou do not have permission!'
```

`vip` 商店必须存在。`conditions` 不会隐藏按钮；需要隐藏时使用 `display-conditions`。菜单根级 `conditions` 控制整个界面能否打开，商品购买/出售条件则单独验证交易。

## 各呈现方式的可见性

在按钮或商品根级添加：

```yaml
menu-visibility:
  inventory: true
  form: true
  dialog: true
  item-action-list: false
  ore: false
  arcmenu: true
```

未指定的值默认为可见。未单独设置 `ore` 或 `item-action-list` 时，它们使用 `menu-visibility.dialog`。例如，`dialog: false` 会同时隐藏这三种模式，除非明确启用某个单独模式。

可见性只过滤已有条目，不会创建槽位、图形位置或点击动作。它可独立于 `dynamic-layout` 使用。

## 按玩家动态解析布局 - 付费版

启用 `dynamic-layout: true` 后，会针对每位玩家解析布局占位符和显示条件。反引号标记内可用 `||` 分隔按顺序检查的候选项：

```yaml
dynamic-layout: true
layout:
  - '000`A||B`00000'
```

此行共有九个槽位。运行时先检查 `A`，再检查 `B`，选择第一个存在、允许在当前呈现方式显示且显示条件通过的条目。均不符合时留空。后备条目应放在最后，完整示例见[条件显示](conditional-display-premium.md)。

## 各呈现方式的条目位置

| 呈现方式 | 位置来源 |
| --- | --- |
| 物品栏 | 根布局槽位 |
| 表单 | 条目列表，忽略物品栏空槽间距 |
| 原生对话框 | 从根布局读取的动作/列表条目 |
| Ore / ArcMenu | 命名图形区域与条目位置 |

普通图形按钮需要显式配置位置：

```yaml
buttons:
  A:
    display-item:
      material: WHEAT
      name: '&aFarming'
    actions:
      1:
        type: shop_menu
        shop: farming
    ui:
      appearance: button
      placement:
        area: sidebar
        order: 0
```

这里使用所选样式的 `sidebar` 区域。显式定位的普通按钮即使不在根级 `layout` 中，也可在图形界面出现。商品可以继承 `ui.products.placement`，自身 `ui.placement` 会覆盖默认值。空区域不会自动生成返回、关闭或分类控件。

[通用菜单](general-menus.md) · [商店菜单](shop-menus.md) · [图形 UI 布局](../menus-getting-started/ui-layout.md)
