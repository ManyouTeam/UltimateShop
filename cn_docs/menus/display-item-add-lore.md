# 🥉展示物品的附加描述

自动生成的描述会为商品展示添加价格、限额和操作说明，与原有 `display-item.lore` 相互独立。物品栏、商品信息和图形提示框都可显示这些文本，并通过条件为不同呈现方式选择描述行。

紧凑价格另行配置：表单使用 `bedrock.price-extra-line`，图形卡片使用 `ui.products.price`。

## 全局与单商品描述

全局默认值写在 `config.yml`：

```yaml
display-item:
  add-lore:
    - '@a&eBuy {buy-amount} trades: {buy-price}'
    - '@b&aSell {sell-amount} trades: {sell-price}'
    - '@c&7Player purchases: {buy-times-player}/{buy-limit-player}'
    - '@t[inventory]&7Use the configured product click controls'
    - '@t[ore]&7Choose a card operation or view details'
    - '@t[arcmenu-hover]&7Choose an operation on the card'
```

内置列表 `['{lang}']` 会加载相应语言的描述格式。商品根级 `add-lore` 可以替换此格式：

```yaml
items:
  A:
    display-item:
      material: APPLE
      name: '&aApple'
      modify-lore: true
    add-lore:
      - '@a&eBuy: {buy-price}'
      - '@b&aSell: {sell-price}'
```

将此片段加入已有商品。设置 `display-item.modify-lore: false`，可关闭该展示变体的自动附加描述。批量购买确认按钮使用控件根级的独立 `modify-lore` 开关，默认 `true`。

## 条件符号

为便于阅读，请把条件符号写在每行文本前面。同一行的所有条件必须满足，显示时会移除条件语法。没有条件的行始终显示。

| 符号 | 显示条件 |
| --- | --- |
| `@a` | 存在购买价格 |
| `@b` | 存在展示用出售价格 |
| `@c` / `@d` | 配置了玩家 / 全服购买上限 |
| `@e` / `@f` | 配置了玩家 / 全服出售上限 |
| `@g` / `@h` | 已达到大于零的玩家购买 / 出售上限 |
| `@i` / `@j` | 已达到大于零的全服购买 / 出售上限 |
| `@k` | 商品启用了批量购买 |
| `@l` | 商品允许收藏 |
| `@m` | 存在展示用出售价格，且启用了全部出售 |
| `@n` | 当前操作支持至少一个有价格的交易方向 |
| `@o` | 当前打开的是收藏菜单 |
| `@p` / `@q` | 位于 / 不位于批量购买描述上下文 |
| `@u` / `@v` | 当前操作支持购买 / 出售 |
| `@w` | 提供了明确的点击动作上下文 |
| `@x` / `@y` | 玩家被识别为 / 未被识别为 Bedrock |
| `@t[presentation]` | 当前呈现方式匹配列出的名称 |
| `@z[multiplier-id]` | 指定出售倍率正在生效 |

符号使用小写。用括号取反，例如 `(@a)` 或 `(@z[vip])`。组合多个符号表示“且”：

```yaml
add-lore:
  - '@a@t[inventory]&eBuy: {buy-price}'
  - '@g&cPlayer purchase limit reached'
  - '(@a)&7This product cannot be purchased'
```

条件只影响所在行。如果分隔行或空行也应随整段隐藏，请为它们同样添加条件。

## 呈现方式条件

| `@t[...]` 值 | 渲染上下文 |
| --- | --- |
| `inventory` | 箱子菜单描述 |
| `form` | Bedrock 商品信息 |
| `dialog` | 原生对话框信息 |
| `item-action-list` | 提供该上下文时的原生物品动作列表 |
| `ore` | Ore 卡片正文/详情 |
| `ore-hover` | Ore 悬停提示框 |
| `arcmenu` | ArcMenu 正文/详情 |
| `arcmenu-hover` | ArcMenu 悬停提示框 |

多个目标用逗号分隔，例如 `@t[inventory,ore]`，匹配忽略大小写和首尾空格。缺少呈现上下文时，不满足正向呈现条件。`dialog` 不会自动包含 Ore 或所有基于对话框的上下文。

物品栏鼠标/按键提示应使用 `@t[inventory]`。`@y` 也会匹配 ArcMenu 和 Ore 中的 Java 玩家。这些条件只过滤自动生成描述，不过滤物品原有静态描述。

Ore 正文描述受 `ui.products.card.lore-lines` 限制，提示框仍可显示完整的适用描述。ArcMenu 卡片的紧凑价格文本使用 `ui.products.price`，不会在卡片正文中展示所有生成行。

## 商品占位符

| 占位符 | 含义 |
| --- | --- |
| `{buy-price}`、`{sell-price}` | 按对应操作数量计算的价格 |
| `{buy-amount}`、`{sell-amount}` | 相互独立的购买/出售交易数量 |
| `{amount}` | 展示数量，受 `display-item.calculate-amount` 影响 |
| `{item-name}` | 商品显示名称 |
| `{buy-click}`、`{sell-click}` | 自动生成的操作可用性/点击提示 |
| `{buy-limit-player}`、`{sell-limit-player}` | 玩家限额 |
| `{buy-limit-server}`、`{sell-limit-server}` | 全服限额 |
| `{buy-times-player}`、`{sell-times-player}` | 玩家当前周期计数 |
| `{buy-times-server}`、`{sell-times-server}` | 全服当前周期计数 |
| `{buy-total-player}`、`{sell-total-player}` | 玩家累计总次数 |
| `{buy-total-server}`、`{sell-total-server}` | 全服累计总次数 |
| `{buy-refresh-player}`、`{sell-refresh-player}` | 玩家重置时间显示 |
| `{buy-refresh-server}`、`{sell-refresh-server}` | 全服重置时间显示 |
| `{buy-next-player}`、`{sell-next-player}` | 玩家下次重置时间 |
| `{buy-next-server}`、`{sell-next-server}` | 全服下次重置时间 |
| `{last-buy-player}`、`{last-sell-player}` | 玩家最近交易时间 |
| `{last-buy-server}`、`{last-sell-server}` | 全服最近交易时间 |
| `{last-reset-buy-player}`、`{last-reset-sell-player}` | 玩家最近重置时间 |
| `{last-reset-buy-server}`、`{last-reset-sell-server}` | 全服最近重置时间 |

选择数量前，默认购买/出售数量独立；选择后使用共同次数。显式展示物品堆叠数量与交易数量独立，见[默认交易数量](../shops/products.md#default-trade-amounts)。

`{buy-click}`、`{sell-click}` 使用全局 `placeholder.click`。启用 `placeholder.click.enabled: true` 后，会预览操作可用性，并从该配置段选择条件、限额或余额提示；实际交易仍会独立验证。

## 多行价格

在 `config.yml` 中使用 `;;` 价格分隔符，将格式化后的价格组成部分拆成多行描述：

```yaml
placeholder:
  price:
    split-symbol-any: ';;'
    split-symbol-all: ';;'
    unknown: 'Unknown'
display-item:
  add-lore:
    - '@a&ePurchase:'
    - '@a&7  {buy-price}'
    - '@b&aSell:'
    - '@b&7  {sell-price}'
```

各相关标题和价格行都应保留对应的购买/出售条件。

## 相关展示默认值

| 全局 `display-item` 配置键 | 用途 |
| --- | --- |
| `auto-set-first-product` | 未显式提供展示物品时，采用第一个适用商品奖励 |
| `calculate-amount` | 计算展示 `{amount}` 时包含奖励数量 |
| `auto-translate-item-name` | 在受支持的 Paper 1.17.1+ 自动翻译原版显示名称 |
| `auto-use-sprite-item-name` | 在兼容的 Paper 环境中使用 Java 图标物品名称 |

显式自定义名称优先。共享名称/资源系统见[物品名称本地化](../features/localized-item-name-premium.md)。

[商店菜单](shop-menus.md) · [Bedrock 表单](bedrock-menus-premium.md) · [图形 UI 布局](../menus-getting-started/ui-layout.md)
