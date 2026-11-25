# 🪨Ore UI - 付费版

Ore UI 利用 Paper 原生对话框通道配合生成的客户端资源包，为 Java 玩家绘制图形化的商品卡片与导航控件。在受支持的 **Paper 1.21.9+** 服务端上，支持通用菜单与商店菜单，并与 ArcMenu 共用同一套图形布局与样式配置。

## 启用 Ore

在 `config.yml` 中开启全局对话框与 Ore 设置：

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

将以下通用菜单保存为 `menus/common_menus/categories.yml`：

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

使用前请确保已经创建了 `farming` 商店。执行 `/shop reload` 后，按下方步骤生成并让客户端加载资源包，再运行 `/shop menu categories` 即可打开（示例中的按钮使用了所选样式的 `sidebar` 区域；全局 `menu.force-ui` 可覆盖界面开关）。

<a id="generate-the-resource-pack"></a>

## 生成资源包

1. 修改 YAML 配置后执行 `/shop reload`，让插件加载最新菜单定义。
2. 执行 `/shop dialogpack`（需权限 `ultimateshop.dialogpack`），构建所有启用了 Ore 的菜单资源。
3. 将 `plugins/UltimateShop/pack/` 目录中生成的内容合并到分发给玩家的客户端资源包中。
4. 让客户端重新加载更新后的资源包，再打开对应菜单。

可以通过 `/shop dialogpack <format>` 手动指定资源包格式版本号（支持 `69` 到 `10000`）；全局 `menu.dialog.ore.pack-format: 0` 则会自动使用当前服务端版本对应的格式。

{% hint style="warning" %}
每当修改了图形布局、主题颜色、纹理模板或物品图标后，都**必须重新执行 `/shop dialogpack` 并分发新资源包**；仅执行 `/shop reload` 不会更新客户端资源。
{% endhint %}

`/shop dialogpack` 会为所有启用 Ore 的通用菜单与商店菜单（含强制开启 Ore 的菜单）生成资源；搜索与收藏菜单自动回退为原生对话框，不会生成 Ore 资源。

## 布局与外观配置键

| 配置键 | 用途 |
| --- | --- |
| `dialog.enabled`、`dialog.layout: ore` | 正常模式下启用 Ore 渲染 |
| `dialog.content` | 页眉说明文本（回退到菜单根级 `content`） |
| `ui.style` | 选择共享图形样式预设 |
| `ui.layout.canvas/header/areas/pagination` | 画布、页眉、命名区域与分页控件 |
| `ui.products.placement` | 商品卡片的默认区域与位置 |
| `ui.products.card` | 卡片图标大小、内边距与 Ore 正文行数限制 |
| `ui.products.price` / `actions` | 紧凑价格文本与卡片操作按钮 |
| 条目 `ui.placement`、`ui.appearance` | 单个按钮/商品的位置与外观（`button` / `card`） |
| `ui.theme` | 共享主题配色 |
| `ui.renderers.ore.templates` | 资源包卡片纹理模板 |
| 全局 `menu.dialog.ore.show-item-icon.*` | 物品图标与堆叠数量显示 |
| 全局 `menu.dialog.ore.pack-format` | 资源包 `pack_format` 元数据 |

根级物品栏 `layout` 仅用于标识有哪些条目，不决定图形坐标。普通按钮需要显式配置 `ui.placement`，商品卡片则默认继承 `ui.products.placement`。几何尺寸与容量限制见[图形 UI 布局](../menus-getting-started/ui-layout.md)。

## 自定义模板纹理

将 PNG 源图放入 `plugins/UltimateShop/textures/`（例如 `textures/shop/card.png`），然后在 `ui.renderers.ore.templates` 中引用：

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

纹理路径相对于 `textures/`（禁止使用绝对路径或 `..`），颜色值须加引号。修改纹理后请重新生成并分发资源包。

## 文本显示与各菜单类型支持

Ore 使用 Minecraft 原生文本渲染，因此 ArcMenu 中的字体缩放参数不会对 Ore 产生完全相同的效果。`ui.products.card.lore-lines` 与 `description-lines` 用于限制卡片表面展示的行数，完整描述仍保留在悬停提示中。

当界面选择 Ore 时：搜索与收藏菜单会使用原生 `multi-action`；批量购买菜单使用原生数量输入或物品栏；物品出售、全部出售与游戏内编辑器始终保持原版物品栏界面。

[原生对话框 UI](dialog-menus-premium.md) · [ArcMenu UI](arcmenu-menus-premium.md) · [UI 模式](ui-overview.md)
