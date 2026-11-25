# 🔍搜索菜单 - 付费版

搜索菜单允许玩家按物品或名称关键词检索全服所有可见的商店商品。物品栏界面支持放入实际物品筛选或通过聊天框输入关键词；Bedrock 表单、原生对话框与 ArcMenu 也各自提供了关键词输入流程，且搜索结果均保留来源商店的价格与交易规则。

文件存放在 `menus/search_menus/`（放入该目录即自动识别为搜索菜单），可通过 `/shop menu search` 或 `open_menu` 动作打开内置菜单。放在 `common_menus` 中的旧版文件可通过根级 `menu-type: search` 或 `config.yml` 中的 `menu.search-gui.menu` 列表声明。

<a id="inventory-example"></a>

## 物品栏示例

将以下配置保存为 `menus/search_menus/my-search.yml`：

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

重载后执行 `/shop menu my-search` 即可打开：玩家可往 `1` 槽位放入物品来按物品匹配搜索，点击 `H` 在聊天栏输入名称关键词，点击 `C` 清除当前搜索条件（全局 `menu.force-ui` 优先级高于菜单开关）。

## 搜索配置键

| 配置键 | 用途 |
| --- | --- |
| `input-item`、`input-items` | 物品筛选槽位标记（合并生效，均未配置时默认 `'1'`） |
| `result-item` | 搜索结果槽位标记（默认 `'6'`，出现次数决定每页/最大结果容量） |
| `action-items.<id>.action-type` | 专用动作类型：`input-name`（别名 `search-name`）或 `clear-search` |
| `action-items.<id>.display-item` | 专用动作按钮外观 |
| `clear-search-item.<id>` | 旧版清除按钮配置段（默认视为 `clear-search`） |
| `state-items.<id>.empty-input.display-item` | 尚未输入条件时的状态展示物品 |
| `state-items.<id>.has-input.display-item` | 已有搜索条件时的状态展示物品 |
| `no-result-item.slot` | 无匹配结果时提示物品的槽位（从 `0` 开始编号） |
| `no-result-item.display-item` | 无结果提示物品外观 |
| `result-lore` | 结果商品附加的来源说明，`{shop}` 与 `{product}` 表示所属商店与商品 |

普通导航按钮依然放在 `buttons` 中；专用动作与状态 ID 需写入根级 `layout`，并预留输入/结果标记和有效的 `no-result-item.slot`（多字符 ID 写法见[布局与按钮](layout-and-buttons.md)）。

状态物品支持以下专用占位符：`{result-amount}`（总匹配数）、`{showing-amount}`（当前展示数）、`{input-amount}`（已放入的筛选物品数）与 `{name-keyword}`（当前关键词）。这些占位符仅用于搜索状态渲染，不可用于其他菜单文本。

## 关键词与物品输入

* **物品筛选**：放入输入槽位后，会筛选出与之匹配的商店商品。
* **名称筛选**：输入关键词后会匹配商品或物品名称（纯文本搜索无需放入物品），名称匹配依赖[物品名称本地化](../features/localized-item-name-premium.md)。

聊天输入的取消与清除关键词可在 `config.yml` 中全局配置：

```yaml
menu:
  prompt:
    cancel-keyword: cancel
  search-gui:
    prompt:
      clear-keyword: clear
```

输入 `cancel` 会保留原有关键词退出输入，输入 `clear` 会清空关键词（内置 `{lang}` 值会随插件语言切换）。清除搜索或关闭/离开菜单时，放入输入槽位的实际物品会自动返还给玩家。

## 各呈现方式的行为差异

| 呈现方式 | 输入方式 | 界面控件 |
| --- | --- | --- |
| 物品栏 | 放入实际物品 + 聊天关键词 | 专用动作、状态、无结果物品与普通导航 |
| Bedrock 表单 | 表单文本框输入关键词 | 搜索按钮与普通导航（无实际输入/状态槽位） |
| 原生对话框 | 对话框原生输入关键词 | 搜索提交与结果信息（无实际输入槽位） |
| ArcMenu | 聊天关键词输入 | 图形动作、状态、无结果控件与商品卡片 |

在表单和原生对话框中，关键词为空时不展示结果，再次打开输入框时会预填上次关键词，取消输入则保留原关键词；从商品详情返回会回到搜索结果页。搜索内部提交或取消输入不会重复触发菜单的 `open-actions` / `close-actions`，只有通过普通导航离开时才会触发。

表单文本优先读取菜单 `bedrock.search.*`，回退全局 `menu.bedrock.search.*`；对话框文本则读取 `dialog.search.*` 与全局 `menu.dialog.search.*`。

## 图形搜索

搜索菜单支持 **ArcMenu** 图形界面：结果商品使用 `ui.products.placement`，专用控件与普通按钮使用 `ui.placement`。注意，根级 `layout` 中结果槽位的数量仍然限制了最多收集的结果数量，图形分页不会突破该上限。

搜索菜单**不渲染 Ore 图形**，也不会生成 Ore 资源包；当设置 `dialog.layout: ore` 时会自动回退为原生 `multi-action` 对话框。呈现方式切换与强制规则见 [UI 模式](ui-overview.md)。

[收藏菜单](favourite-menus-premium.md) · [ArcMenu UI](arcmenu-menus-premium.md) · [图形 UI 布局](../menus-getting-started/ui-layout.md)
