# 🔽批量购买菜单

批量购买菜单用于让玩家为已选中的商品选择交易数量（这里的数量指**交易份数**，若单份商品包含多个物品，实际数量会按比例放大）。玩家可通过商品的 `select-amount` 点击操作，或带有商店/商品上下文的 `buy_more_menu` 动作进入该界面。

物品栏数量菜单存放在 `menus/buy_more_menus/`（内置 `buy-more.yml`、`buy-more-buy.yml`、`buy-more-sell.yml`），不能通过 `/shop menu` 直接打开。

## 选择菜单与数量上限

在 `config.yml` 中配置全局默认设置：

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

| `menu.buy-more-menu` 选项 | 用途 |
| --- | --- |
| `default` | 商品同时可买可卖时使用的菜单与最大份数 |
| `only-buy` | 商品仅可购买时使用的菜单与最大份数 |
| `only-sell` | 商品仅可出售时使用的菜单与最大份数 |
| `not-open-when-invalid` | 当商品没有可用的确认按钮时禁止打开 |
| `display-item-max-stack` | 在 Minecraft 1.20.5+ 环境中，将预览物品单格堆叠上限扩展至 99 |

商店的 `settings.buy-more` 控制默认开关，商品自身也可以单独覆盖 `buy-more` 与 `buy-more-menu`（所选份数限制在 `1` 到 `max-amount` 之间，不受预览槽位数量限制）：

```yaml
items:
  A:
    buy-more: true
    buy-more-menu:
      menu: buy-more
      max-amount: 128
```

默认打开快捷键为 `menu.click-event.select-amount: SHIFT_RIGHT`（设为 `NEVER` 可关闭）。`buy_more_menu` 动作支持传入 `shop` 和 `item`，付费版还支持在动作中临时覆盖 `buy-more-menu.menu` 与 `buy-more-menu.max-amount`，详见[自定义点击事件](../features/custom-click-event-premium.md#example-only-buy-more-menu)。

## 物品栏数量菜单示例

在 `menus/buy_more_menus/` 中新建菜单文件，并在上方配置中引用其 ID：

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

数量调节、预览槽位和确认按钮的 ID 都必须出现在 `layout` 中，普通 `buttons` 可用于装饰或返回（各类控件请使用不同 ID）。该菜单打开时初始数量默认为 `1`。

## 数量菜单配置参考

| 配置键 | 说明 |
| --- | --- |
| `amount-items.<id>.display-item` | 数量调节按钮的展示物品 |
| `amount-items.<id>.add-amount` | 增减当前已选数量 |
| `amount-items.<id>.set-amount` | 直接设为指定数值（仅当 `add-amount` 未设置或为 `0` 时生效） |
| `display-items` | 按顺序排列的预览槽位标记（优先于单数 `display-item`） |
| `display-item` | 单个预览槽位标记的简写 |
| `display-items-padding-item` | 多余预览槽位的填充物品（使用展示物品格式） |
| `confirm-items.<id>.display-item` | 确认按钮外观，支持 `{amount}` |
| `confirm-items.<id>.click-action` | 固定交易方向：`buy`、`sell`、`buy-or-sell`、`sell-all`；省略则沿用触发本次打开的点击动作 |
| `confirm-items.<id>.modify-lore` | 是否自动按所选数量生成价格/说明 Lore，默认 `true` |
| 控件 `menu-visibility` | 按呈现方式过滤控件 |
| `dialog.enabled` | 对话框可用时是否允许使用原生数量输入 |

预览物品会按有效堆叠上限自动拆分填满预览槽位，多余位置显示填充物或留空；确认交易时仍按实际交易引擎校验（预览堆叠本身不会直接发给玩家）。

此菜单支持常规标题、大小、布局、按钮和生命周期动作，但**不会**继承来源商店的 `settings.menu-settings` 或全局 `menu.ui`，其中的 `bedrock`、`arcmenu`、`ui`、`display-items-ui` 也不会用于生成其他呈现方式的数量界面。

## 各呈现方式的数量选择

| 呈现方式 | 数量控件形式 | 文本配置来源 |
| --- | --- | --- |
| 物品栏 | 使用本文件的调节按钮、预览与确认控件 | 当前批量购买菜单文件 |
| Bedrock 表单 | 文本输入框，随后展示商品确认信息 | 来源商店菜单 `bedrock.buy-or-sell` → 全局 `menu.bedrock.buy-or-sell` |
| 原生对话框 | 本文件开启 `dialog.enabled` 时使用原生输入，否则回退物品栏 | 来源商店菜单 `dialog.buy-more` → 全局 `menu.dialog.buy-more` |
| ArcMenu | 直接在商品详情面板中调整数量 | 图形设置与 ArcMenu 文本 |

在 Bedrock 表单模式下，数量输入属于来源商店流程，不会渲染本文件的槽位控件；在原生对话框模式下，输入与确认期间仍保留触发动作指定的菜单和 `max-amount` 覆盖（全局 `menu.force-ui` 仍可强制切换呈现方式）。

在 ArcMenu 中，触发 `select-amount`（默认右键）会打开商品详情面板：只要商品开启了批量购买且存在可用数量操作，即可通过加减按钮、鼠标滚轮或直接[输入数量文本](arcmenu-menus-premium.md#quantity-input)调整份数。注意，物品栏 `confirm-items` 的按钮文本不会重命名图形界面或原生对话框中的操作标签。

## 默认交易数量

在玩家手动调整数量之前，商品的 `default-buy-amount` 与 `default-sell-amount` 会分别生效；一旦在批量购买界面选定数量，买入与卖出都会使用所选份数。占位符 `{buy-amount}` 与 `{sell-amount}` 反映对应操作次数，展示物品自身的堆叠数量则保持独立。详见[默认交易数量](../shops/products.md#default-trade-amounts)。

[商店菜单](shop-menus.md) · [UI 模式](ui-overview.md) · [展示物品的附加描述](display-item-add-lore.md)
