# 🔲通用菜单

通用菜单展示已配置的按钮，用于导航和自定义动作。保存到 `menus/common_menus/`，通过 `/shop menu <id>` 或 `open_menu` 动作打开。

## 创建导航菜单

保存为 `menus/common_menus/main.yml`：

```yaml
title: '&8Main Menu'
size: 9
layout:
  - '000A0X000'
dialog:
  enabled: false
bedrock:
  enabled: false
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
  X:
    display-item:
      material: BARRIER
      name: '&cClose'
    actions:
      1:
        type: close
```

创建 `farming` 商店，执行 `/shop reload`，再执行 `/shop menu main`。此示例在正常选择时使用物品栏，全局 `menu.force-ui` 可以覆盖这些开关。

`type: shop_menu` 配合 `shop` 打开商店；`type: open_menu` 配合 `menu` 打开其他通用、搜索、收藏或物品出售菜单。通用菜单不会自动填入商店商品。

## 通用菜单配置键

| 配置键 | 用途 |
| --- | --- |
| `title` | 界面标题 |
| `content` | 共享说明的后备值 |
| `size`、`layout` | 物品栏大小与条目 ID |
| `buttons` | 普通展示物品、动作与条件 |
| `conditions` | 打开菜单的条件 |
| `open-actions`、`close-actions` | 打开和关闭菜单时的动作 |
| `dynamic-layout` | 按玩家解析布局，付费版功能 |
| `dialog`、`bedrock`、`arcmenu` | 界面开关与受支持的原生选项 |
| `ui` | Ore/ArcMenu 样式、布局与绘制设置 |

按钮点击条件写在 `buttons.<id>.conditions`，显示条件写在 `buttons.<id>.display-conditions`。根级 `conditions` 作用于整个菜单，见[布局与按钮](layout-and-buttons.md)。

## 使用其他呈现方式 - 付费版

要使用原生对话框，将示例中的对话框开关替换为：

```yaml
dialog:
  enabled: true
  layout: multi-action
  content: '&7Choose a category'
  button-width: 150
  columns: 2
```

同时启用全局 `menu.dialog.enabled`。Ore 使用 `dialog.layout: ore`，还需配置图形位置并分发生成的资源包。ArcMenu 需要兼容的对接环境，启用全局和菜单 ArcMenu 开关并配置图形位置。Bedrock 表单则使用全局与菜单的 Bedrock 开关。

优先级、强制选择和客户端支持见 [UI 模式](ui-overview.md)；样式与区域见[图形 UI 布局](../menus-getting-started/ui-layout.md)。

## 自定义命令 - 付费版

在菜单根级添加以下内容，为菜单注册命令：

```yaml
custom-command:
  name: categories
  description: Open the category menu
```

此命令为执行玩家打开当前菜单。命令名称应避免与已有命令冲突。

<a id="all-buttons-update"></a>
<a id="title-update-premium"></a>
<a id="title-update-premium-require-paper"></a>

## 物品栏刷新与标题更新

全局 `menu.menu-update.circle-update` 每秒刷新物品显示，`click-update` 在点击后刷新。付费版标题更新使用 `menu.title-update.enabled/circle-update/click-update`，并需要 PacketEvents。完整配置见[物品栏 UI](inventory-ui.md#clicks-visibility-and-refresh)。

## 通用菜单目录中的特殊类型文件

搜索、收藏、物品出售专用目录会自动确定文件类型。已有的通用菜单文件也可通过根级 `menu-type: search`、`favourite`（别名 `favorite`）、`item-sell`（别名 `item_sell`）声明类型。搜索和收藏 ID 也可以由全局旧版菜单列表分类。

新建特殊类型菜单时，优先使用专用目录。即使文件放在 `common_menus`，其专用控件和界面支持仍以声明类型为准。

[商店菜单](shop-menus.md) · [配置与继承](menu-configuration.md)
