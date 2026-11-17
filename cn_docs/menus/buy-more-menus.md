# 🔽批量购买菜单

批量购买菜单为已选商品选择数量。数量表示**交易单位数**，一个交易单位可以奖励多个实际物品。通过商品的 `select-amount` 操作，或带有商店/商品上下文的 `buy_more_menu` 动作打开。

物品栏数量菜单位于 `menus/buy_more_menus/`。内置文件包括 `buy-more.yml`、`buy-more-buy.yml`、`buy-more-sell.yml`，不能通过 `/shop menu` 独立打开。

## 选择菜单与数量上限

`config.yml` 中的全局默认值：

```yaml
menu:
  buy-more-menu:
    not-open-when-invalid: true
    display-item-max-stack: true
    default:
      menu: buy-more
      max-amount: 693
    only-buy:
      menu: buy-more-buy
      max-amount: 693
    only-sell:
      menu: buy-more-sell
      max-amount: 693
```

| `menu.buy-more-menu` 下的选项 | 用途 |
| --- | --- |
| `default` | 同时有购买与出售价格时使用的菜单和上限 |
| `only-buy` | 没有出售价格时使用的菜单和上限 |
| `only-sell` | 没有购买价格时使用的菜单和上限 |
| `not-open-when-invalid` | 商品没有有效确认控件时禁止打开 |
| `display-item-max-stack` | 在受支持的 Minecraft 1.20.5+ 环境中，将预览堆叠上限扩展到 99 |

商店 `settings.buy-more` 提供默认启用状态，商品 `buy-more` 和 `buy-more-menu` 可以覆盖：

```yaml
items:
  A:
    buy-more: true
    buy-more-menu:
      menu: buy-more
      max-amount: 128
```

将此片段加入已有商品。所选数量被限制在 1 到配置上限之间，预览槽位容量不决定此上限。

内置点击绑定为 `menu.click-event.select-amount: SHIFT_RIGHT`。将点击绑定设为 `NEVER` 可禁用。`buy_more_menu` 动作接受 `shop` 和 `item`；付费版还可为本次打开指定 `buy-more-menu.menu`、`buy-more-menu.max-amount`。见[自定义点击事件](../features/custom-click-event-premium.md#example-only-buy-more-menu)。

## 物品栏数量菜单示例

在 `menus/buy_more_menus/` 中新建文件，并在上述设置中选择其 ID：

```yaml
title: 'Choose amount'
size: 27
layout:
  - '000000000'
  - '01a2b3c00'
  - '000D0E000'
dialog:
  enabled: false
amount-items:
  '1':
    display-item:
      material: RED_WOOL
      name: '&c-1'
    add-amount: -1
  '2':
    display-item:
      material: GREEN_WOOL
      name: '&a+1'
    add-amount: 1
  '3':
    display-item:
      material: PAPER
      name: '&eSet to 64'
    set-amount: 64
display-items: [a, b, c]
display-items-padding-item:
  material: GRAY_STAINED_GLASS_PANE
  name: ' '
confirm-items:
  D:
    display-item:
      material: EMERALD
      name: '&aBuy x{amount}'
    click-action: buy
    modify-lore: true
  E:
    display-item:
      material: GOLD_INGOT
      name: '&eSell x{amount}'
    click-action: sell
    modify-lore: true
```

数量、预览和确认 ID 必须出现在 `layout` 中。普通 `buttons` 可用于装饰或导航，各种控件应使用不同 ID。此示例初始数量为 1。

## 数量菜单配置参考

| 配置键 | 行为 |
| --- | --- |
| `amount-items.<id>.display-item` | 数量控件外观 |
| `amount-items.<id>.add-amount` | 增加或减少数量 |
| `amount-items.<id>.set-amount` | `add-amount` 缺失或为零时设置固定值 |
| `display-items` | 按顺序排列的预览标记，优先于单数 `display-item` |
| `display-item` | 单个预览标记的替代写法 |
| `display-items-padding-item` | 未使用预览空间的填充物，使用展示物品格式 |
| `confirm-items.<id>.display-item` | 确认按钮外观，支持 `{amount}` |
| `confirm-items.<id>.click-action` | 固定操作，如 `buy`、`sell`、`buy-or-sell`、`sell-all`；省略则使用该次点击配置的操作 |
| `confirm-items.<id>.modify-lore` | 按所选数量生成商品描述，默认 `true` |
| 控件 `menu-visibility` | 按呈现方式过滤控件 |
| `dialog.enabled` | 对话框可用时允许原生数量输入 |

预览会按有效展示堆叠上限，依次拆分到预览槽位。未使用位置显示填充物或空气。确认时按所选数量执行正常交易验证；大量预览物品并不意味着直接发放物品。

此文件支持常规标题、大小、布局、按钮和生命周期设置，但不继承来源商店的 `settings.menu-settings` 或全局图形 `menu.ui`。其中的 `bedrock`、`arcmenu`、`ui`、`display-items-ui` 不用于定义其他数量界面。

## 各呈现方式的数量选择

| 呈现方式 | 数量控件 | 文本配置 |
| --- | --- | --- |
| 物品栏 | 本文件的数量按钮、预览和确认控件 | 当前批量购买文件 |
| Bedrock 表单 | 文本输入，然后展示商品信息 | 来源商店菜单 `bedrock.buy-or-sell` → 全局 `menu.bedrock.buy-or-sell` |
| 原生对话框 | 本文件允许对话框时使用原生输入，否则物品栏 | 来源商店菜单 `dialog.buy-more` → 全局 `menu.dialog.buy-more` |
| ArcMenu | 商品详情中的数量控件 | 图形设置与 ArcMenu 文本 |

表单数量输入属于来源商店流程，不渲染本文件的槽位控件。对话框在输入与确认期间保留动作指定的菜单和上限覆盖。全局强制 UI 可以覆盖正常对话框选择。

ArcMenu 的 `select-amount` 打开商品详情。调整数量需要启用批量购买、选定数量菜单，并存在可用数量操作。右键默认是 `select-amount`，加减按钮和滚轮用于调整数量。兼容版本还支持直接[输入数量文本](arcmenu-menus-premium.md#quantity-input)。物品栏确认标签不会重命名图形或原生信息操作。

## 默认交易数量

手动选择数量前，`default-buy-amount` 与 `default-sell-amount` 分别生效；选择后，购买和出售都使用选定次数。生成的 `{buy-amount}`、`{sell-amount}` 反映对应操作次数，显式展示物品堆叠数量则保持独立。见[默认交易数量](../shops/products.md#default-trade-amounts)。

[商店菜单](shop-menus.md) · [UI 模式](ui-overview.md) · [展示物品的附加描述](display-item-add-lore.md)
