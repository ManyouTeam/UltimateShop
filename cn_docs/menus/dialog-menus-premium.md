# 💬原生对话框 UI - 付费版

原生对话框使用受支持的 Paper 1.21.9+ API，为 Java 玩家显示动作按钮和物品动作列表。这些原生布局不需要 Ore 生成的资源包。Ore 则是建立在对话框路径上的独立图形布局。

## 启用原生对话框

在 `config.yml` 中设置：

```yaml
menu:
  dialog:
    enabled: true
    not-auto-close: true
    auto-add-sprite:
      enabled: true
      auto-hide: true
      format: '<sprite:"{namespace}:{atlas}":{path}>'
```

将以下通用菜单保存为 `menus/common_menus/categories.yml`：

```yaml
title: '&8Categories'
size: 9
layout:
  - '0000A0000'
dialog:
  enabled: true
  layout: multi-action
  content: '&7Choose a category'
  button-width: 150
  columns: 2
arcmenu:
  enabled: false
buttons:
  A:
    display-item:
      material: WHEAT
      name: '&aFarming'
    actions:
      1:
        type: shop_menu
        shop: farming
```

创建 `farming`，重载后在兼容的 Java 客户端执行 `/shop menu categories`。正常选择时，需要同时启用全局与菜单对话框开关。ArcMenu 已启用且可用时优先；全局强制呈现方式会覆盖正常开关。

## 原生布局

| 菜单 `dialog.layout` | 结果 |
| --- | --- |
| `multi-action` | 原生动作按钮 |
| `item-action-list` | 原生物品动作列表 |
| `ore` | 对支持的菜单类型使用 Ore 图形界面 |

根级 `layout` 提供来源条目与顺序，物品栏空槽不会生成空白对话框行。各条目可以通过自身 `dialog.layout` 覆盖原生物品动作行为。可见性按当前布局使用 `menu-visibility.dialog` 或 `menu-visibility.item-action-list`。

要隐藏某个按钮的材质图标，在按钮根级添加：

```yaml
dialog:
  show-sprite: false
```

`dialog.show-sprite` 覆盖自动标签图标选择。全局 `auto-add-sprite.auto-hide` 会在已有物品图标时避免重复显示标签图标。这些选项不应放在 `display-item` 或图形 `ui` 中。

## 对话框文本与控件

| 菜单 `dialog` 下的配置键 | 用途 |
| --- | --- |
| `enabled` | 允许对话框 |
| `layout` | 选择原生布局或 Ore |
| `content` | 正文，后备为菜单根级 `content` |
| `button-width`、`columns` | 菜单原生动作控件尺寸，默认 `150`、`2` |
| `not-auto-close` | 受支持的信息操作后保持打开 |
| `default-button` | 后备退出标签 |
| `info.display-item`、`info.title` | 商品信息外观 |
| `info.buttons.buy/sell/buy-more/sell-all/back` | 信息操作标签 |
| `buy-more.display-item/title/input/buttons.confirm` | 数量输入流程 |
| `search.input`、`search.buttons.search` | 搜索控件 |
| `favourite-edit.title/content/buttons.*` | 收藏编辑控件 |
| `details` | Ore 商品详情标签 |

受支持的共享文本和行为选项可回退到全局 `menu.dialog.*`；`content`、`button-width`、`columns` 从菜单自身读取。自动图标设置位于全局 `menu.dialog.auto-add-sprite`。标签支持普通语言引用和占位符。

## 商品与数量流程

选择商品后会显示可用信息操作，并按点击绑定、价格和商品资格过滤。`ui.products.actions` 配置的是图形卡片，不是原生对话框动作按钮。

所选批量购买文件允许对话框，且商品支持 `select-amount` 时，使用原生数量输入，否则使用受支持的后备方式。输入标签由来源商店 `dialog.buy-more` 决定；批量购买文件的物品栏数量按钮不会转换成原生控件。

选择数量前，购买与出售分别使用各自默认数量。选定数量以及本次打开的菜单/上限覆盖，会贯穿原生输入和确认流程。

## 搜索与收藏

搜索提供关键词输入和结果信息，收藏提供正常与编辑控件。二者都不会将实际物品投放槽位转换为原生控件。这些类型的 `dialog.layout: ore` 会解析为原生 `multi-action`。

<a id="ore-layout"></a>

## Ore 布局

设置菜单 `dialog.layout: ore`，再在 `ui` 下配置图形区域和位置。通用与商店菜单支持 Ore，并需要[生成的资源包](ore-menus-premium.md)。原生 `button-width` 和 `columns` 不用于定位 Ore 卡片。

[UI 模式](ui-overview.md) · [批量购买](buy-more-menus.md) · [搜索](search-menus-premium.md) · [收藏](favourite-menus-premium.md)
