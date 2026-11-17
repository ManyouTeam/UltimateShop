# ✨ ArcMenu UI — 付费版

ArcMenu 为 Java 玩家把通用、商店、搜索和收藏菜单呈现为可交互的图形界面。它与 Ore 共用样式、区域、位置和商品卡片设置，但使用自己的一套 ArcMenu 对接方式与客户端配置。

## 启用 ArcMenu

安装一个兼容的 ArcMenu 插件，并完成它的客户端资源配置。本插件对接 ArcMenu 3.4.3 API，其中包含原生文本输入服务。

在 `config.yml` 中设置：

```yaml
menu:
  arcmenu:
    enabled: true
  ui:
    style: classic
```

另存为 `menus/common_menus/categories.yml`：

```yaml
title: '&8Categories'
size: 9
layout:
  - '0000A0000'
arcmenu:
  enabled: true
  content: '&7Choose a category'
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

创建一个名为 `farming` 的商店，重载配置后打开 `/shop menu categories`。所选样式负责提供画布和 `sidebar` 区域。在常规选择流程中，Java 会先尝试 ArcMenu，然后才轮到已启用的 Dialog/Ore 路线和物品栏；Bedrock 则走自己的呈现方式选择。全局强制选择见 [UI 模式](ui-overview.md)。

## 样式与布局

菜单可以设置 `ui.style: classic` 或 `ui.style: bedrock`，也可以通过 `menu.ui.style` 指定全局默认样式。样式文件位于 `plugins/UltimateShop/ui/`，直接包含图形相关字段，可以复制出来作为自定义预设。插件在补建缺失的内置预设时，会保留已有文件。

| 预设 | 外观与位置 |
| --- | --- |
| `classic` | 左侧纵向导航，右侧商品网格 |
| `bedrock` | 炭灰色面板、斜边控件、绿色的购买与悬停状态、浅色的出售控件、顶部五按钮导航，以及三列商品网格 |

`bedrock` 预设把名为 `sidebar` 的区域用于横向导航。这个名称只标识一个区域，并不代表固定的屏幕位置。两个预设都沿用现有的物品图标与字体。切换预设时，需要相应调整写死的行/列位置和自定义区域。

菜单内设置的字段会覆盖样式的常规默认值；强制样式则可通过 `menu.force-ui.style` 反过来覆盖这些本地字段，详见[配置与继承](menu-configuration.md)。几何参数与共享卡片配置键的说明见[图形 UI 布局](../menus-getting-started/ui-layout.md)。

## 商品操作

卡片会从 `ui.products.actions.primary` 中最多展示两个可用操作，并通过 `ui.products.actions.extra` 展示其余的或指定的额外操作。这些操作必须已存在于商品点击绑定中，并且仍会被价格、条件、数量权限以及自定义的仅购买/仅出售限制过滤。

右键默认执行 `select-amount`。修改菜单的 `ui.renderers.arcmenu.right-click-action`，即可换成其他可用操作。`select-amount` 打开的是图形化的商品详情，而不是渲染一个批量购买文件。

在玩家选定数量之前，默认的购买/出售数量各自独立生效。当批量购买与某个数量操作都可用时，加/减按钮和滚轮可以调整已选数量。物品栏的 `amount-items`、预览槽位和确认槽位不参与图形详情面板的样式定义。

物品图标上的堆叠数量大于 1 时，会在图标右下角显示这个数字。它表示所展示堆叠的数量，与已选中的交易单位数无关。

## 数量输入

ArcMenu 的文本输入服务可用时，点击 `-` 与 `+` 之间的数量即可直接编辑。即使 ArcMenu 隐藏了原生铁砧界面，UltimateShop 也会在聚焦的图形字段里实时显示正在输入的内容。

结束编辑（鼠标模式下按 Esc）后才会应用有效的整数。数值会被限制在 1 到所选批量购买上限之间；无效输入则保留原数量。结束输入只是选定了数量，玩家之后还要点击购买或出售才能完成交易。

输入框的文字由 `ui.text.amount-input` 定义。来源商品仍需启用批量购买、配置好有效的数量菜单，并具备可用的 `select-amount` 操作。

## ArcMenu 渲染器设置

菜单内的渲染器设置写在 `ui.renderers.arcmenu` 下：

```yaml
ui:
  renderers:
    arcmenu:
      right-click-action: select-amount
      screen-scale: 1.5
      animation:
        enabled: true
        duration: 8
      more-menu:
        columns: 1
      tooltip:
        enabled: true
        width: 260
        font-size: 1.1
        padding: 8
```

| 相对配置键 | 用途 |
| --- | --- |
| `right-click-action` | 商品右键触发的操作 |
| `screen-scale` | 整体图形缩放比例 |
| `animation.enabled/duration` | 打开动画；重新打开同一菜单时跳过 |
| `more-menu.columns` | 额外操作弹窗的列数 |
| `tooltip.enabled/width/font-size/padding` | 悬停提示框的外观 |

刷新间隔始终读取**全局**值，即使其他设置都来自菜单：

```yaml
menu:
  ui:
    renderers:
      arcmenu:
        update-interval: 20
```

该间隔以游戏刻为单位，最小为 5，也可以由所选全局样式提供。单个菜单内的 `update-interval` 不会决定刷新计划。

## 背景、装饰与文本

ArcMenu 从解析后的 `ui.theme` 读取各项图形颜色。窗口背景和标题背景各有独立的启用开关。`theme.decorations` 支持边框、边角装饰和页眉点缀；斜边与控件专用颜色让样式可以自行定义外观，而不必依赖文件名。详见[主题参考](../menus-getting-started/ui-layout.md#theme-and-text)。

提示框会把带格式的描述文字排成左对齐的文本块，内容过大时会自动缩小以适配。鼠标模式的布局适配会考虑当前绘制表面、光标灵敏度、玩家偏好、光标边距和垂直俯仰上限；触屏/Vision 模式则使用该表面的边界。

## 各菜单类型的行为

搜索/收藏菜单使用 ArcMenu 布局和自己的控件，但根级结果槽位的容量仍然限制来源结果的数量。收藏编辑会提供排序和移除控件。物品出售/全部出售仍是实体物品栏。批量购买的数量选择始终在商品详情内完成。

ArcMenu 的控件不由 `/shop dialogpack` 生成；该命令构建的是 Ore 资源。

[搜索](search-menus-premium.md) · [收藏](favourite-menus-premium.md) · [批量购买](buy-more-menus.md)
