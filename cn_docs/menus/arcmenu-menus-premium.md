# ✨ArcMenu UI - 付费版

ArcMenu UI 为 Java 玩家将通用、商店、搜索与收藏菜单渲染为可交互的图形界面。它与 Ore 共用同一套图形样式、命名区域、坐标定位与商品卡片配置，但在插件对接与客户端资源上使用 ArcMenu 自身的方案。

## 启用 ArcMenu

先安装兼容的 ArcMenu 插件并完成其客户端资源配置（UltimateShop 对接 **ArcMenu 3.4.3 API**，含原生文本输入服务）。

在 `config.yml` 中开启全局设置：

```yaml
menu:
  arcmenu:
    enabled: true
  ui:
    style: classic
```

将以下通用菜单保存为 `menus/common_menus/categories.yml`：

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

使用前请确保已经创建了 `farming` 商店。保存配置并执行 `/shop reload` 后，运行 `/shop menu categories` 即可打开（按钮会显示在所选样式的 `sidebar` 区域中）。正常选择顺序下，Java 客户端会优先尝试 ArcMenu，再依次回退到 Dialog / Ore 与物品栏；全局强制规则见 [UI 模式](ui-overview.md)。

## 样式与布局预设

你可以通过全局 `menu.ui.style` 或菜单 `ui.style` 选择 `classic` 或 `bedrock` 预设，也可以复制 `plugins/UltimateShop/ui/` 下的文件来自定义样式：

| 预设 | 视觉风格与布局特点 |
| --- | --- |
| `classic` | 左侧纵向导航栏 + 右侧商品网格 |
| `bedrock` | 炭灰面板、斜边控件、绿色购买/悬停反馈、浅色出售控件、顶部 5 按钮横向导航栏 + 3 列商品网格 |

注意：`bedrock` 预设把顶部横向导航区域也命名为 `sidebar`（区域名只是一个逻辑标识，不固定在屏幕左侧）。切换预设时，请同步核对写死的 `row`/`column` 或自定义区域位置。

菜单自身的字段会覆盖样式默认值；若开启了 `menu.force-ui.style`，则会用强制样式覆盖本地默认值，详见[菜单配置与继承](menu-configuration.md)。几何规则与卡片参数见[图形 UI 布局](../menus-getting-started/ui-layout.md)。

## 为什么按钮在物品栏里有，ArcMenu 里却不显示？

**普通按钮如果不配置 `ui.placement`，ArcMenu 会直接跳过它。** 根级 `layout` 只决定物品栏的格子位置；商品可以继承 `ui.products.placement`，但普通按钮必须各自显式指定图形位置。

例如旧版商店中的翻页/跳转箭头，需要在原按钮配置下补充：

```yaml
ui:
  placement:
    area: sidebar
    order: 20
```

如果该按钮写在商店文件的 `buttons` 下，商店菜单的 `layout` 中仍需保留它的标记。如果是同一家商店内的多页商品翻页，无需手写箭头按钮，直接使用 `ui.layout.pagination` 自带的图形分页控件即可（`previous-page-item` / `next-page-item` 仅用于物品栏）。

## 商品操作与数量调整

商品卡片会从 `ui.products.actions.primary` 中最多展示两个可用操作，并通过 `ui.products.actions.extra` 展示其余或指定的额外操作（前提是商品本身绑定了对应点击事件，且通过了价格、条件与仅买/仅卖过滤）。

右键商品默认触发 `select-amount`（可通过 `ui.renderers.arcmenu.right-click-action` 修改），在 ArcMenu 中会直接打开图形化的**商品详情面板**，而不是物品栏批量购买界面。只要商品开启了批量购买且存在可用数量操作，玩家即可通过加/减按钮与鼠标滚轮调整数量（物品栏的 `amount-items` 与预览槽位不参与图形面板样式）。

另外，展示物品图标右下角显示的堆叠数字来自物品本身的堆叠数量，与当前选中的交易份数相互独立。

<a id="quantity-input"></a>

## 文本输入数量

当 ArcMenu 的文本输入服务可用时，玩家直接点击 `-` 与 `+` 之间的数字即可进入编辑模式（即使 ArcMenu 隐藏了原生铁砧界面，Focused 图形框内也会实时显示输入内容）。

结束编辑（鼠标模式下按 `Esc`）后，有效整数会被限制在 `1` 到所选批量购买上限之间，无效输入则保留原数量。输入完成后玩家仍需点击购买或出售按钮才能成交。输入框提示文本由 `ui.text.amount-input` 定义（商品仍需启用批量购买、配置好有效数量菜单并具备 `select-amount` 操作）。

## ArcMenu 渲染器设置

菜单内的专属配置写在 `ui.renderers.arcmenu` 下：

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
| `screen-scale` | 整体界面缩放比例 |
| `animation.enabled` / `duration` | 打开动画开关与时长（重新打开同一菜单时跳过） |
| `more-menu.columns` | 额外操作弹窗的列数 |
| `tooltip.enabled` / `width` / `font-size` / `padding` | 悬停提示框的开关、宽度、字号与内边距 |

刷新间隔**始终读取全局 `config.yml` 配置**（单位为 tick，最小 `5`，也可由全局样式提供；单个菜单内的 `update-interval` 不生效）：

```yaml
menu:
  ui:
    renderers:
      arcmenu:
        update-interval: 20
```

## 背景、装饰与文本提示

ArcMenu 从解析后的 `ui.theme` 读取配色：窗口背景与标题背景可独立开关，`theme.decorations` 控制边框、边角装饰与页眉分隔线，详见[主题与文本参考](../menus-getting-started/ui-layout.md#主题与文本)。

悬停提示框会自动将描述排成左对齐文本块，内容过长时自动缩放适配；鼠标模式与触屏/Vision 模式也会分别按绘制表面与光标设置自动适配边界。

## 各菜单类型的支持情况

* **搜索与收藏菜单**：使用 ArcMenu 图形布局与专用控件（最大结果数仍受根级结果槽位数量限制），收藏编辑提供排序与移除按钮。
* **批量购买**：直接在商品详情面板内完成数量选择。
* **物品出售 / 全部出售**：保留原版实体物品栏界面。

注意，ArcMenu 不使用 `/shop dialogpack`，该命令仅用于生成 Ore 资源包。

[搜索菜单](search-menus-premium.md) · [收藏菜单](favourite-menus-premium.md) · [批量购买菜单](buy-more-menus.md)
