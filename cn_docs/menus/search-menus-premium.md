# 🔍搜索菜单 - 付费版

搜索菜单按物品和/或名称查找玩家可见的商店商品。物品栏支持放入实际物品筛选，以及通过聊天输入关键词。表单、原生对话框和 ArcMenu 各有自己的关键词输入流程，并保留来源商品的价格与交易规则。

文件保存在 `menus/search_menus/`。通过 `/shop menu search` 或 `open_menu` 打开内置菜单。此目录中的文件自动识别为搜索菜单。通用目录中的文件可使用 `menu-type: search`，也可通过 `config.yml` 的旧版 `menu.search-gui.menu` ID 列表指定。

<a id="inventory-example"></a>

## 物品栏示例

保存为 `menus/search_menus/my-search.yml`：

```yaml
title: 'Search'
content: 'Find a product.'
size: 27
layout:
  - '000HNC000'
  - '001111100'
  - '066666660'
dialog:
  enabled: false
bedrock:
  enabled: false
arcmenu:
  enabled: false
input-item: '1'
result-item: '6'
action-items:
  H:
    action-type: input-name
    display-item:
      material: HOPPER
      name: '&eEnter a name'
  C:
    action-type: clear-search
    display-item:
      material: BARRIER
      name: '&cClear'
state-items:
  N:
    empty-input:
      display-item:
        material: PAPER
        name: '&7Waiting for input'
    has-input:
      display-item:
        material: COMPASS
        name: '&a{showing-amount}/{result-amount} results'
no-result-item:
  slot: 22
  display-item:
    material: BARRIER
    name: '&cNo results'
result-lore:
  - '&7Shop: {shop}'
  - '&7Product: {product}'
```

重载后执行 `/shop menu my-search`。正常选择时使用物品栏：向 `1` 槽位放入筛选物品，点击 `H` 输入关键词，点击 `C` 清除。全局强制 UI 优先于菜单开关。

## 搜索配置

| 配置键 | 用途 |
| --- | --- |
| `input-item`、`input-items` | 合并使用的实际输入标记，均未配置时默认 `1` |
| `result-item` | 结果标记，默认 `6`，出现次数决定结果容量 |
| `action-items.<id>.action-type` | `input-name`（别名 `search-name`）或 `clear-search` |
| `action-items.<id>.display-item` | 专用动作外观 |
| `clear-search-item.<id>` | 旧版动作配置段，默认 `clear-search` |
| `state-items.<id>.empty-input.display-item` | 尚未输入时的状态 |
| `state-items.<id>.has-input.display-item` | 已输入时的状态 |
| `no-result-item.slot` | 无匹配结果的显示槽位，从零编号 |
| `no-result-item.display-item` | 无结果外观 |
| `result-lore` | 结果附加描述，`{shop}`、`{product}` 表示来源 |

普通导航放在 `buttons` 中。专用动作和状态 ID 放入根级 `layout`，预留输入/结果标记，并设置有效的无结果槽位。多字符 ID 见[布局与按钮](layout-and-buttons.md)。

状态物品支持 `{result-amount}`（总匹配数）、`{showing-amount}`（已显示数）、`{input-amount}`（实际输入物品数）、`{name-keyword}`。这些值只用于搜索状态渲染，不能任意用于菜单文本。

## 关键词与物品输入

物品栏输入会筛选与放入物品关联的商品。关键词筛选商品或物品名称；纯文本搜索无需放入物品。名称匹配使用共享的[物品名称本地化系统](../features/localized-item-name-premium.md)。

聊天输入的特殊关键词在 `config.yml` 中全局配置：

```yaml
menu:
  prompt:
    cancel-keyword: cancel
  search-gui:
    prompt:
      clear-keyword: clear
```

`cancel` 保留原关键词，`clear` 清除关键词。内置 `{lang}` 值使用配置的语言。清除搜索也会返还物品栏输入；关闭或离开菜单时返还剩余放入物品。结果和展示图标是 GUI 物品，不是玩家放入的库存。

## 各界面的行为

| 呈现方式 | 输入 | 控件 |
| --- | --- | --- |
| 物品栏 | 实际物品与聊天关键词 | 配置的动作、状态和无结果物品 |
| Bedrock 表单 | 自定义表单关键词输入 | 搜索按钮与普通导航，无实际输入/状态槽位 |
| 原生对话框 | 原生关键词输入 | 搜索提交与结果信息，无实际输入槽位 |
| ArcMenu | 聊天关键词输入 | 图形动作、状态、无结果控件和商品卡片 |

表单先打开搜索首页，再进入关键词输入。表单/对话框关键词为空时不展示商品。输入框预填旧关键词，取消时保留。商品信息中的返回操作恢复搜索。内部提交或取消输入不会反复执行菜单生命周期动作，普通导航则会离开此流程。

表单标签优先使用菜单 `bedrock.search.*`，再使用全局 `menu.bedrock.search.*`。对话框使用 `dialog.search.*` 和全局 `menu.dialog.search.*`。

## 图形搜索

搜索支持 ArcMenu 图形界面。结果使用 `ui.products.placement`，普通与专用控件使用 `ui.placement`。根级结果槽位容量仍限制可显示的来源结果数，图形分页不会扩大此限制。

搜索不渲染 Ore 图形，也不生成 Ore 资源。此类型使用 `dialog.layout: ore` 时会选择原生 `multi-action`。界面开关与强制选择遵循 [UI 模式](ui-overview.md)。

[收藏菜单](favourite-menus-premium.md) · [ArcMenu](arcmenu-menus-premium.md) · [图形 UI 布局](../menus-getting-started/ui-layout.md)
