# 💬原生对话框 UI - 付费版

原生对话框基于 **Paper 1.21.9+** 的 Dialog API，为 Java 玩家呈现原生的动作按钮或物品动作列表。使用原生对话框布局**无需生成 Ore 资源包**（Ore 是在对话框通道之上额外封装的图形化布局）。

## 启用原生对话框

在 `config.yml` 中开启全局对话框设置：

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

在通用菜单（例如 `menus/common_menus/categories.yml`）中配置：

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

使用前请确保已经创建了 `farming` 商店。执行 `/shop reload` 后，在受支持的 Java 1.21.9+ 客户端中运行 `/shop menu categories` 即可看到原生对话框界面。正常模式下需要全局与菜单的 `dialog.enabled` 同时为 `true`（注意：若 ArcMenu 同时启用且可用，Java 客户端会优先打开 ArcMenu；全局 `menu.force-ui` 可覆盖开关）。

## 原生布局模式

| 菜单 `dialog.layout` | 呈现效果 |
| --- | --- |
| `multi-action` | 原生多动作按钮网格 |
| `item-action-list` | 原生物品动作列表 |
| `ore` | 对支持的菜单类型启用 Ore 图形界面 |

条目来源与顺序由根级 `layout` 决定，物品栏空槽位不会变成空白行；各条目也可通过自身的 `dialog.layout` 覆盖原生物品动作行为。可见性会随当前布局读取 `menu-visibility.dialog` 或 `menu-visibility.item-action-list`。

如果需要隐藏某个按钮的 Sprite 材质图标，可在该按钮根级添加（注意不要写在 `display-item` 或 `ui` 下）：

```yaml
dialog:
  show-sprite: false
```

全局 `auto-add-sprite.auto-hide: true` 会在条目本身已带物品图标时自动避免重复显示 Sprite。

## 对话框文本与控件选项

| 菜单 `dialog` 配置键 | 用途 |
| --- | --- |
| `enabled` | 是否允许对话框 |
| `layout` | 选择原生布局或 `ore` |
| `content` | 对话框正文（回退到菜单根级 `content`） |
| `button-width`、`columns` | 原生动作按钮宽度与列数（默认 `150`、`2`） |
| `not-auto-close` | 在受支持的信息界面完成操作后是否保持打开 |
| `default-button` | 后备退出按钮文本 |
| `info.display-item`、`info.title` | 商品信息对话框外观与标题 |
| `info.buttons.buy/sell/buy-more/sell-all/back` | 商品信息操作按钮标签 |
| `buy-more.display-item/title/input/buttons.confirm` | 原生数量输入流程文本 |
| `search.input`、`search.buttons.search` | 搜索对话框控件文本 |
| `favourite-edit.title/content/buttons.*` | 收藏编辑对话框控件文本 |
| `details` | Ore 商品详情标签 |

受支持的共享文本与行为选项会回退读取全局 `menu.dialog.*`（`content`、`button-width`、`columns` 仅从菜单自身读取，自动 Sprite 设置仅支持全局 `menu.dialog.auto-add-sprite`），所有标签均支持语言引用与占位符。

## 商品与数量流程

在对话框中选中商品后，会展示当前可用的操作（按点击绑定、价格与商品资格过滤；注意 `ui.products.actions` 仅用于图形卡片，不影响原生对话框按钮）。

当选用的批量购买菜单开启了 `dialog.enabled` 且商品支持 `select-amount` 时，会弹出原生数量输入对话框（输入框文本读取来源商店的 `dialog.buy-more`）；否则回退为物品栏批量购买界面。选定数量与本次打开的菜单/上限覆盖会贯穿整个原生输入与确认流程。

## 搜索与收藏

在对话框模式下，搜索菜单提供关键词输入与结果展示，收藏菜单提供浏览与编辑控件；两者均不会把物品栏输入槽位转成原生控件，且 `dialog.layout: ore` 会自动回退为原生 `multi-action`。

<a id="ore-layout"></a>

## Ore 布局

如需使用图形化的 Ore 界面，只需将 `dialog.layout` 设为 `ore`，并在 `ui` 下配置图形区域与条目位置即可（仅支持通用菜单与商店菜单，且需要[生成资源包](ore-menus-premium.md)；原生的 `button-width` 与 `columns` 不参与 Ore 卡片定位）。

[UI 模式](ui-overview.md) · [批量购买菜单](buy-more-menus.md) · [搜索菜单](search-menus-premium.md) · [收藏菜单](favourite-menus-premium.md)
