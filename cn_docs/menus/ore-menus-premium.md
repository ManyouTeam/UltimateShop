# 🪨Ore UI - 付费版

Ore 通过 Paper 对话框与生成的资源包绘制商品卡片和导航控件。在受支持的 Paper 1.21.9+ 服务器上，支持通用和商店菜单。图形布局与样式和 ArcMenu 共享。

## 启用 Ore

在 `config.yml` 中设置：

```yaml
menu:
  dialog:
    enabled: true
    ore:
      show-item-icon:
        enabled: true
        auto-hide-one: true
      pack-format: 0
  ui:
    style: classic
```

保存为 `menus/common_menus/categories.yml`：

```yaml
title: '&8Categories'
size: 9
layout:
  - '0000A0000'
dialog:
  enabled: true
  layout: ore
  content: '&7Choose a category'
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
    ui:
      appearance: button
      placement:
        area: sidebar
        order: 0
```

创建目标商店，重载，按下方步骤生成资源包，并在 Java 客户端加载后执行 `/shop menu categories`。此示例使用所选样式的 `sidebar` 区域。全局强制呈现方式可以覆盖开关。

## 生成资源包

1. 修改 YAML 后执行 `/shop reload`，加载菜单定义。
2. 执行 `/shop dialogpack`，构建已启用的 Ore 菜单。需要权限 `ultimateshop.dialogpack`。
3. 将 `plugins/UltimateShop/pack/` 下生成的内容合入分发给玩家的资源包。
4. 客户端加载更新后的资源包，再打开修改过的界面。

`/shop dialogpack <format>` 可显式指定资源包格式，命令接受 `69` 到 `10000`。全局 `menu.dialog.ore.pack-format: 0` 使用内置的对应版本格式。修改图形布局、主题、模板或图标后，必须重新构建和分发；仅重载 YAML 不会向客户端分发新资源。

生成范围包括使用 Ore 的通用/商店菜单，以及有效的强制 Ore 选择。搜索/收藏使用原生对话框后备，不生成 Ore 资源。

## 布局与外观

| 配置键 | 用途 |
| --- | --- |
| `dialog.enabled`、`dialog.layout: ore` | 正常选择时使用 Ore |
| `dialog.content` | 页眉内容，后备为菜单根级 `content` |
| `ui.style` | 共享图形预设 |
| `ui.layout.canvas/header/areas/pagination` | 画布、区域、文本与分页 |
| `ui.products.placement` | 默认商品位置 |
| `ui.products.card` | 卡片图标、内边距和 Ore 正文描述限制 |
| `ui.products.price/actions` | 商品价格文本与操作选择 |
| 条目 `ui.placement`、`ui.appearance` | 单个按钮/商品的位置与按钮/卡片外观 |
| `ui.theme` | 共享颜色 |
| `ui.renderers.ore.templates` | 资源包纹理模板 |
| 全局 `menu.dialog.ore.show-item-icon.*` | 物品图标与数量显示 |
| 全局 `menu.dialog.ore.pack-format` | 资源包元数据格式 |

根级物品栏布局只标识条目，不提供图形坐标。普通控件需要明确位置；商品卡片默认使用 `ui.products.placement`，除非自身覆盖。几何与容量规则见[图形 UI 布局](../menus-getting-started/ui-layout.md)。

## 自定义模板纹理

将 PNG 源文件放在 `plugins/UltimateShop/textures/`，例如 `textures/shop/card.png`，然后配置：

```yaml
ui:
  renderers:
    ore:
      templates:
        product:
          texture: 'shop/card.png'
          selected-texture: 'shop/card-selected.png'
          selected-background: '#244A32'
          selected-border: '#65D98B'
```

纹理路径相对于 `textures/`，不得使用绝对路径或 `..`。RGB 颜色需要加引号。共享布局、卡片和主题值可以为生成的模板提供几何尺寸与基础颜色。修改后重新生成资源包。

## 文本与支持的菜单流程

Ore 使用原生文本渲染，ArcMenu 字体缩放不保证得到相同的 Ore 效果。`ui.products.card.lore-lines` 限制商品正文描述行数，`description-lines` 限制分类说明行数；完整的适用描述仍可保留在悬停信息中。

选择 Ore 时，搜索/收藏使用原生 `multi-action`；批量购买使用原生数量输入或物品栏；物品出售、全部出售和游戏内编辑器保留实际物品栏界面。

[原生对话框](dialog-menus-premium.md) · [ArcMenu](arcmenu-menus-premium.md) · [UI 模式](ui-overview.md)
