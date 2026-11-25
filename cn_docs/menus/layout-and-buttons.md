# 🧱布局与按钮

物品栏界面通过根级 `layout` 将条目 ID 映射到格子槽位：普通按钮定义在 `buttons` 下，商店菜单还可以直接引用商店文件中的商品与按钮。Ore 和 ArcMenu 则使用独立的[图形位置设置](../menus-getting-started/ui-layout.md)。

## 完整按钮菜单示例

将以下配置保存为 `menus/common_menus/categories.yml`：

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

创建对应的 `farming` 商店并执行 `/shop reload` 后，运行 `/shop menu categories` 即可打开。在物品栏界面中，槽位从 `0` 开始编号，`A` 位于槽位 12，`X` 位于槽位 14。

## 槽位 ID 规则

`layout` 按「从左到右、从上到下」的顺序解析，每行 9 个槽位，`size` 必须与总槽位数一致（可选 `9`、`18`、`27`、`36`、`45`、`54`）。

* **区分大小写**：`A` 与 `a` 代表不同的条目。
* **重复放置**：同一个 ID 出现多次时，会在对应多个槽位显示同一条目。
* **留空**：未在 `buttons` 或商店中定义的 ID 会自动留空。
* **`0` 也是普通 ID**：如果定义了 `buttons.'0'`，所有写 `0` 的位置都会被该按钮填充（常用于背景玻璃板）。

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

{% hint style="tip" %}
像 `'0'` 这样纯数字的 YAML 键建议加上引号。上方同时演示了如何让背景玻璃板只在物品栏界面显示，而在表单、对话框和图形界面中自动隐藏。
{% endhint %}

## 多字符 ID

在 `layout` 中用反引号（`` ` ``）包裹多字符 ID，无论名称多长，整段标记都只占**一个**槽位：

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

这一行共 9 个槽位，依次为 `0`、`food_buy`、`food_sell` 以及六个 `0`。反引号仅作为 `layout` 中的分隔符，定义 `buttons` 时不要带反引号。

## 商店菜单的条目来源

商店菜单模板的 `layout` 可以同时引用以下三种来源的条目：

| 来源 | 配置位置 |
| --- | --- |
| 商品 | 商店 `items.<id>` |
| 商店专用普通按钮 | 商店 `buttons.<id>` |
| 可复用普通按钮 | 模板 `buttons.<id>` |

建议为不同来源使用不同的 ID。若不小心写了重名 ID，插件会按以下优先级取第一个可用的条目：**商店复制条目 → 商店 `buttons` → 商店 `items` → 模板 `buttons`**。

普通按钮点击后执行自身的 `actions`；商品条目则执行商品的买卖交易逻辑以及 `buy`、`sell`、`select-amount` 等点击绑定。导航按钮即使使用了与商品相同的展示材质，也不会变成可交易商品。

## 按钮动作与条件

| 配置键 | 用途 |
| --- | --- |
| `display-item` | 按钮展示物品，写法见[展示物品格式](../format/display-item-format.md) |
| `actions` | 点击条件通过后执行的动作 |
| `conditions` | 点击时校验的条件 |
| `fail-actions` | 点击条件未通过时执行的动作 |
| `display-conditions` | 控制按钮是否显示（付费版功能） |
| `menu-visibility` | 按不同呈现方式控制可见性 |
| `dialog.layout`、`dialog.show-sprite` | 原生对话框中的条目外观 |
| `ui` | Ore / ArcMenu 中的外观与位置 |

下面的示例让按钮始终可见，但在点击时检查权限：

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

`conditions` 只负责拦截点击，不会隐藏按钮；需要按条件隐藏按钮时，请使用 `display-conditions`。另外，菜单根级的 `conditions` 控制整个界面能否打开，商品的买卖条件则独立校验交易。

## 各呈现方式的可见性

在按钮或商品根级添加 `menu-visibility`，即可按呈现方式分别控制是否显示：

```yaml
menu-visibility:
  inventory: true
  form: true
  dialog: true
  item-action-list: false
  ore: false
  arcmenu: true
```

未填写的项默认为 `true`。其中，若未单独设置 `ore` 或 `item-action-list`，它们会沿用 `menu-visibility.dialog` 的值（即设置 `dialog: false` 会同时在原生对话框、Ore 和 `item-action-list` 中隐藏，除非为对应项单独设为 `true`）。

`menu-visibility` 仅负责过滤已有条目，不会创建新槽位、图形坐标或点击事件，可独立于 `dynamic-layout` 使用。

## 按玩家动态解析布局 - 付费版

启用 `dynamic-layout: true` 后，插件会为每位玩家实时解析 `layout` 中的占位符与显示条件。在反引号标记内使用 `||` 可以按优先级列出多个候选项：

```yaml
dynamic-layout: true
layout:
  - '000`A||B`00000'
```

该行仍为 9 个槽位。运行时会按从左到右的顺序依次检查 `A` 和 `B`，选中第一个存在、允许在当前呈现方式显示且 `display-conditions` 通过的条目；若全部不满足则留空。通常将默认后备条目放在最后，完整用法见[条件显示](conditional-display-premium.md)。

## 各呈现方式的条目位置

| 呈现方式 | 位置决定方式 |
| --- | --- |
| 物品栏 | 根级 `layout` 槽位 |
| Bedrock 表单 | 按条目列表顺序排列（自动忽略空槽间隔） |
| 原生对话框 | 按根级 `layout` 顺序读取动作/列表条目 |
| Ore / ArcMenu | 命名图形区域与 `ui.placement` |

普通图形按钮需要显式配置 `ui.placement`（即使该按钮未出现在根级 `layout` 中，只要配置了有效位置就能在图形界面显示）：

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

商品通常直接继承菜单的 `ui.products.placement`，再由商品自身的 `ui.placement` 按需覆盖。未放置任何条目的空区域不会自动生成返回、关闭或分类按钮。

[通用菜单](general-menus.md) · [商店菜单](shop-menus.md) · [图形 UI 布局](../menus-getting-started/ui-layout.md)
