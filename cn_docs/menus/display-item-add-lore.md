# 🥉展示物品的附加描述

自动附加描述（`add-lore`）会在商品展示物品的原有 `display-item.lore` 下方，自动追加价格、限额与操作提示。物品栏菜单、原生商品信息以及图形悬停提示框均可显示这些文本，并支持通过条件符号按呈现方式或商品状态动态筛选显示行。

注意：Bedrock 表单列表的紧凑价格使用 `bedrock.price-extra-line`，图形卡片表面的紧凑价格使用 `ui.products.price`，两者与 `add-lore` 相互独立。

## 全局与单商品配置

全局默认模板写在 `config.yml` 中：

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

内置值 `['{lang}']` 会自动加载当前语言文件中的描述格式；在单个商品根级配置 `add-lore` 则可以覆盖全局模板：

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

若不想让某个展示物品追加自动描述，可在其内部设置 `display-item.modify-lore: false`；批量购买菜单的确认按钮则在控件根级使用独立的 `modify-lore` 开关（默认 `true`）。

## 条件符号

建议将条件符号写在每行开头以便阅读。同一行上的所有条件必须同时满足才会显示该行（渲染时会自动移除符号本身）；没有任何符号的行始终显示。

| 符号 | 生效条件 |
| --- | --- |
| `@a` | 存在购买价格 |
| `@b` | 存在展示用出售价格 |
| `@c` / `@d` | 配置了玩家 / 全服购买上限 |
| `@e` / `@f` | 配置了玩家 / 全服出售上限 |
| `@g` / `@h` | 已达到（大于 0 的）玩家购买 / 出售上限 |
| `@i` / `@j` | 已达到（大于 0 的）全服购买 / 出售上限 |
| `@k` | 商品启用了批量购买 |
| `@l` | 商品允许收藏 |
| `@m` | 存在展示用出售价格，且启用了全部出售 |
| `@n` | 当前操作支持至少一个有价格的交易方向 |
| `@o` | 当前处于收藏菜单中 |
| `@p` / `@q` | 处于 / 不处于批量购买描述上下文 |
| `@r[共享价格ID]` | 商品价格引用了指定共享价格模板（参数留空表示任意共享价格，多个 ID 用逗号分隔） |
| `@u` / `@v` | 当前操作支持购买 / 出售 |
| `@w` | 存在明确的点击动作上下文 |
| `@x` / `@y` | 玩家被识别为 / 未被识别为 Bedrock 玩家 |
| `@t[presentation]` | 当前呈现方式匹配括号内列出的名称 |
| `@z[multiplier-id]` | 指定的出售倍率正在生效 |

符号均使用小写字母；用小括号包裹表示「取反」（如 `(@a)`、`(@z[vip])`），多个符号连写表示「且」：

```yaml
add-lore:
  - '@a@t[inventory]&eBuy: {buy-price}'
  - '@g&cPlayer purchase limit reached'
  - '(@a)&7This product cannot be purchased'
```

每个条件仅控制当前这一行；如果分隔线或空行也需要随某段内容一起隐藏，请为分隔线/空行也加上相同的条件符号。

<a id="presentation-conditions"></a>

## 呈现方式条件（`@t[...]`）

| `@t[...]` 取值 | 对应渲染上下文 |
| --- | --- |
| `inventory` | 物品栏箱子菜单描述 |
| `form` | Bedrock 商品信息表单 |
| `dialog` | 原生对话框信息 |
| `item-action-list` | 原生物品动作列表（提供该上下文时） |
| `ore` | Ore 卡片正文 / 详情 |
| `ore-hover` | Ore 悬停提示框 |
| `arcmenu` | ArcMenu 正文 / 详情 |
| `arcmenu-hover` | ArcMenu 悬停提示框 |

多个目标之间用逗号分隔（如 `@t[inventory,ore]`），匹配时忽略大小写与首尾空格（`dialog` 不会自动包含 `ore` 或其他基于对话框的上下文）。

* 物品栏按键操作提示建议加上 `@t[inventory]`；`@y` 同样会匹配使用 ArcMenu 和 Ore 的 Java 玩家。
* 这些条件仅过滤 `add-lore` 自动生成的行，不会影响物品原本的静态 `lore`。
* Ore 卡片正文行数受 `ui.products.card.lore-lines` 限制，但悬停提示框仍会显示完整适用描述；ArcMenu 卡片表面的紧凑价格使用 `ui.products.price`，不会把所有 `add-lore` 行都画在卡片上。

## 商品占位符

| 占位符 | 含义 |
| --- | --- |
| `{buy-price}`、`{sell-price}` | 按对应操作数量计算出的价格 |
| `{buy-amount}`、`{sell-amount}` | 相互独立的购买 / 出售交易数量 |
| `{amount}` | 展示数量（受 `display-item.calculate-amount` 影响） |
| `{item-name}` | 商品显示名称 |
| `{buy-click}`、`{sell-click}` | 自动生成的操作可用性与点击提示 |
| `{buy-limit-player}`、`{sell-limit-player}` | 玩家购买 / 出售上限 |
| `{buy-limit-server}`、`{sell-limit-server}` | 全服购买 / 出售上限 |
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

在玩家进入批量购买选择数量之前，默认购买与出售数量各自独立；选定数量后两者共用选定次数（展示物品自身的堆叠数量始终独立，详见[默认交易数量](../shops/products.md#default-trade-amounts)）。

`{buy-click}` 与 `{sell-click}` 读取全局 `placeholder.click`；开启 `placeholder.click.enabled: true` 后，会自动预览操作是否可用并显示对应的条件、限额或余额不足提示（实际成交时仍会独立校验）。

## 多行价格拆行

当一个商品有多种货币或复杂价格时，可以在 `config.yml` 中将价格分隔符设为 `;;`，从而在 `add-lore` 中拆成多行展示：

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

注意为拆行后的标题行与价格行都保留对应的 `@a` / `@b` 条件。

## 相关全局展示默认值

| 全局 `display-item` 选项 | 用途 |
| --- | --- |
| `auto-set-first-product` | 未显式配置展示物品时，自动选用第一个适用商品作为展示物品 |
| `calculate-amount` | 计算展示 `{amount}` 时是否乘以商品数量 |
| `auto-translate-item-name` | 在受支持的 Paper 1.17.1+ 服务端自动翻译原版物品名称 |
| `auto-use-sprite-item-name` | 在兼容的 Paper 环境中使用 Java 图标物品名称 |

显式配置的自定义名称始终优先。共享名称与资源系统详见[物品名称本地化](../features/localized-item-name-premium.md)。

[商店菜单](shop-menus.md) · [Bedrock 表单](bedrock-menus-premium.md) · [图形 UI 布局](../menus-getting-started/ui-layout.md)
